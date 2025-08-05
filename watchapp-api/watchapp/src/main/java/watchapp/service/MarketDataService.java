// Dosya Yolu: src/main/java/watchapp/service/MarketDataService.java
package watchapp.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import watchapp.dto.CryptoDTO;
import watchapp.dto.ExchangeRateDTO;
import watchapp.dto.StockDTO;

import java.util.List;

@Service
public class MarketDataService {

    // FMP API için WebClient
    private final WebClient fmpWebClient;
    private final String fmpApiKey;

    // CoinGecko API için WebClient
    private final WebClient coingeckoWebClient;

    public MarketDataService(WebClient.Builder webClientBuilder,
                             @Value("${app.financial-api.fmp.base-url}") String fmpBaseUrl,
                             @Value("${app.financial-api.fmp.api-key}") String fmpApiKey) {
        // Financial Modeling Prep için WebClient kurulumu
        this.fmpWebClient = webClientBuilder.baseUrl(fmpBaseUrl).build();
        this.fmpApiKey = fmpApiKey;

        // CoinGecko için WebClient kurulumu
        this.coingeckoWebClient = webClientBuilder.baseUrl("https://api.coingecko.com/api/v3").build();
    }

    // ===================================================================
    // API'lerden Gelen Cevapları Temsil Eden Dahili Kayıtlar (Records)
    // ===================================================================

    // FMP /quote endpoint'i için
    private record FmpQuote(
            String symbol, double price, double change,
            @JsonProperty("changesPercentage") double changePercent
    ) {}

    // CoinGecko /coins/markets endpoint'i için
    private record CoinGeckoMarket(
            String id,
            String symbol,
            String name,
            @JsonProperty("current_price") double currentPrice,
            @JsonProperty("price_change_percentage_24h") double priceChangePercentage24h
    ) {}


    // ===================================================================
    // PUBLIC SERVİS METOTLARI (CONTROLLER TARAFINDAN KULLANILAN)
    // ===================================================================

    /**
     * Popüler NASDAQ hisselerinin anlık verilerini FMP API'sinden çeker.
     * @Cacheable kaldırıldı, her istekte yeniden veri çekilir.
     */
    public List<StockDTO> getRecentStocks() {
        System.out.println(">>> (CACHE'SİZ) NASDAQ HİSSE VERİSİ FMP API'DEN ÇEKİLİYOR <<<");

        String symbols = "AAPL,MSFT,GOOGL,AMZN,NVDA";

        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/quote/{symbols}")
                        .queryParam("apikey", fmpApiKey)
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

    /**
     * Popüler döviz kurlarının anlık verilerini FMP API'sinden çeker.
     * @Cacheable kaldırıldı, her istekte yeniden veri çekilir.
     */
    public List<ExchangeRateDTO> getExchangeRates() {
        System.out.println(">>> (CACHE'SİZ) DÖVİZ KURU VERİSİ FMP API'DEN ÇEKİLİYOR <<<");

        List<String> currencyPairs = List.of("EURUSD", "GBPUSD", "USDJPY");

        // Her bir döviz çifti için paralel olarak API isteği atıyoruz.
        return Flux.fromIterable(currencyPairs)
                .flatMap(this::fetchExchangeRateFromFmp)
                .collectList()
                .block();
    }

    /**
     * Bitcoin ve Ethereum'un anlık piyasa verilerini CoinGecko API'sinden çeker.
     * Bu metot cache kullanmaz.
     */
    public List<CryptoDTO> getCryptoPrices() {
        System.out.println(">>> (CACHE'SİZ) KRİPTO VERİSİ COINGECKO API'DEN ÇEKİLİYOR <<<");

        String ids = "bitcoin,ethereum";

        return coingeckoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/coins/markets")
                        .queryParam("vs_currency", "usd")
                        .queryParam("ids", ids)
                        .build())
                .retrieve()
                .bodyToFlux(CoinGeckoMarket.class)
                .onErrorResume(e -> {
                    System.err.println("Kripto para verisi alınamadı. API hatası: " + e.getMessage());
                    return Flux.empty();
                })
                .map(cgMarket -> new CryptoDTO(
                        cgMarket.id(),
                        cgMarket.symbol().toUpperCase(),
                        cgMarket.name(),
                        cgMarket.currentPrice(),
                        cgMarket.priceChangePercentage24h()
                ))
                .collectList()
                .block();
    }


    // ===================================================================
    // YARDIMCI (PRIVATE) METOT
    // ===================================================================

    /**
     * Tek bir döviz çifti için FMP API'sinden veri çeken yardımcı metot.
     * /quote endpoint'ini kullanır, çünkü bu daha güvenilir çalışıyor.
     * @param pair "EURUSD" gibi bir döviz çifti.
     * @return ExchangeRateDTO içeren bir Mono.
     */
    private Mono<ExchangeRateDTO> fetchExchangeRateFromFmp(String pair) {
        System.out.println(">>> Döviz çifti çekiliyor: " + pair);

        return fmpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/quote/{symbol}")
                        .queryParam("apikey", fmpApiKey)
                        .build(pair))
                .retrieve()
                .bodyToFlux(FmpQuote.class)
                .next() // Gelen listeden ilk elemanı al
                .onErrorResume(e -> {
                    System.err.println("Döviz kuru verisi alınamadı (" + pair + "). Hata: " + e.getMessage());
                    return Mono.empty();
                })
                .map(quote -> {
                    // Formatı düzenle: EURUSD -> EUR/USD
                    String formattedPair = pair.substring(0, 3) + "/" + pair.substring(3);
                    return new ExchangeRateDTO(formattedPair, quote.price());
                });
    }
}