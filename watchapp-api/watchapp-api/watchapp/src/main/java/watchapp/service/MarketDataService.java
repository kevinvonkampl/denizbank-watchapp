// Dosya Yolu: src/main/java/watchapp/service/MarketDataService.java
package watchapp.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import watchapp.dto.ExchangeRateDTO;
import watchapp.dto.StockDTO;

import java.util.List;

@Service
public class MarketDataService {

    private final WebClient webClient;
    private final String apiKey;

    public MarketDataService(WebClient.Builder webClientBuilder,
                             @Value("${app.financial-api.fmp.base-url}") String baseUrl,
                             @Value("${app.financial-api.fmp.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    // FMP'den gelen cevapları temsil eden DTO'lar
    private record FmpQuote(
            String symbol, double price, double change,
            @JsonProperty("changesPercentage") double changePercent
    ) {}

    // FMP Forex API'sinden gelen response için düzeltilmiş DTO
    private record FmpForexResponse(
            String ticker,
            @JsonProperty("bid") double bid,
            @JsonProperty("ask") double ask,
            @JsonProperty("open") double open,
            @JsonProperty("low") double low,
            @JsonProperty("high") double high,
            @JsonProperty("changes") double changes,
            @JsonProperty("date") String date
    ) {}

    // ===================================================================
    // GERÇEK, ÇALIŞAN API METOTLARI
    // ===================================================================

    @Cacheable("stocks")
    public List<StockDTO> getRecentStocks() {
        System.out.println(">>> NASDAQ HİSSE VERİSİ FMP API'DEN ÇEKİLİYOR (CACHE'DE DEĞİL) <<<");

        String symbols = "AAPL,MSFT,GOOGL,AMZN,NVDA";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/quote/{symbols}")
                        .queryParam("apikey", apiKey)
                        .build(symbols))
                .retrieve()
                .bodyToFlux(FmpQuote.class)
                .onErrorResume(e -> {
                    System.err.println("Hisse senedi verisi alınamadı. API hatası: " + e.getMessage());
                    return Flux.empty();
                })
                .map(fmpQuote -> new StockDTO(fmpQuote.symbol(), fmpQuote.price(), fmpQuote.change()))
                .collectList()
                .block();
    }

    @Cacheable("exchangeRates")
    public List<ExchangeRateDTO> getExchangeRates() {
        System.out.println(">>> DÖVİZ KURU VERİSİ FMP API'DEN ÇEKİLİYOR (CACHE'DE DEĞİL) <<<");

        // FMP'nin desteklediği forex çiftleri (doğru format)
        List<String> currencyPairs = List.of("EURUSD", "GBPUSD", "USDJPY");

        return Flux.fromIterable(currencyPairs)
                .flatMap(this::fetchExchangeRateAlternative)
                .collectList()
                .block();
    }

    // --- Düzeltilmiş Yardımcı Metot ---
    private Mono<ExchangeRateDTO> fetchExchangeRateFixed(String pair) {
        System.out.println(">>> Döviz çifti çekiliyor: " + pair);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/fx")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(FmpForexResponse.class)
                .filter(forex -> pair.equals(forex.ticker())) // İstediğimiz çifti filtrele
                .next()
                .onErrorResume(e -> {
                    System.err.println("Döviz kuru verisi alınamadı (" + pair + "). Hata: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.empty();
                })
                .map(fmpRate -> {
                    System.out.println(">>> Başarılı forex verisi: " + fmpRate.ticker() + " = " + fmpRate.bid());

                    // Formatı düzenle: EURUSD -> EUR/USD
                    String formattedPair = pair.substring(0, 3) + "/" + pair.substring(3);

                    // Bid fiyatını kullanıyoruz (ask de kullanılabilir)
                    return new ExchangeRateDTO(formattedPair, fmpRate.bid());
                });
    }

    // Alternatif metot - Eğer yukarıdaki çalışmazsa bu deneyin
    private Mono<ExchangeRateDTO> fetchExchangeRateAlternative(String pair) {
        System.out.println(">>> Alternatif yöntemle döviz çifti çekiliyor: " + pair);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/quote/{symbol}")
                        .queryParam("apikey", apiKey)
                        .build(pair))
                .retrieve()
                .bodyToFlux(FmpQuote.class)
                .next()
                .onErrorResume(e -> {
                    System.err.println("Alternatif döviz kuru verisi alınamadı (" + pair + "). Hata: " + e.getMessage());
                    return Mono.empty();
                })
                .map(quote -> {
                    System.out.println(">>> Alternatif başarılı forex verisi: " + quote.symbol() + " = " + quote.price());

                    String formattedPair = pair.substring(0, 3) + "/" + pair.substring(3);
                    return new ExchangeRateDTO(formattedPair, quote.price());
                });
    }
}