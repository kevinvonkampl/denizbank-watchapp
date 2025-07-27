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

    // Constructor'ı yeni FMP ayarlarını okuyacak şekilde güncelliyoruz.
    public MarketDataService(WebClient.Builder webClientBuilder,
                             @Value("${app.financial-api.fmp.base-url}") String baseUrl,
                             @Value("${app.financial-api.fmp.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    // ===================================================================
    // FMP'den Gelen Cevapları Temsil Eden DTO'lar (Daha Basit)
    // ===================================================================

    // FMP, hisse senedi ve döviz verilerini genellikle bir liste içinde tek bir eleman olarak döndürür.
    // Bu yüzden gelen cevabın bir List<FmpQuote> veya List<FmpExchangeRate> olmasını bekliyoruz.
    private record FmpQuote(
            String symbol,
            double price,
            double change,
            @JsonProperty("changesPercentage") double changePercent // FMP bu ismi kullanıyor
    ) {}

    private record FmpExchangeRate(
            String symbol,
            double rate,
            String timestamp // timestamp de geliyor, kullanmayacağız ama DTO'da bulunsun
    ) {}


    // ===================================================================
    // Gerçek API Metotları (FMP API'sine göre güncellendi)
    // ===================================================================

    @Cacheable("stocks")
    public List<StockDTO> getRecentStocks() {
        System.out.println(">>> HİSSE SENEDİ VERİSİ FMP API'DEN ÇEKİLİYOR (CACHE'DE DEĞİL) <<<");
        // Borsa İstanbul sembollerini virgülle birleştirerek tek bir istekte yollayabiliriz.
        String symbols = "TUPRS.IS,THYAO.IS,GARAN.IS";

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/quote/{symbols}")
                        .queryParam("apikey", apiKey)
                        .build(symbols))
                .retrieve()
                .bodyToFlux(FmpQuote.class) // Cevap bir liste olduğu için Flux kullanıyoruz
                // Gelen her FmpQuote nesnesini bizim standart StockDTO'muza çeviriyoruz.
                .map(fmpQuote -> new StockDTO(fmpQuote.symbol(), fmpQuote.price(), fmpQuote.change()))
                .collectList() // Tüm sonuçları bir listede topla
                .block(); // Asenkron işlemin bitmesini bekle
    }

    @Cacheable("exchangeRates")
    public List<ExchangeRateDTO> getExchangeRates() {
        System.out.println(">>> DÖVİZ KURU VERİSİ FMP API'DEN ÇEKİLİYOR (CACHE'DE DEĞİL) <<<");
        // İzlemek istediğimiz döviz çiftleri
        List<String> currencyPairs = List.of("USDTRY", "EURTRY", "GBPTRY");

        // Her bir döviz çifti için ayrı ayrı istek atıyoruz.
        // FMP'nin limitleri daha yüksek olduğu için gecikmeye gerek yok.
        return Flux.fromIterable(currencyPairs)
                .flatMap(this::fetchExchangeRate)
                .collectList()
                .block();
    }

    // --- Yardımcı Metot ---

    private Mono<ExchangeRateDTO> fetchExchangeRate(String pair) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/fx/{pair}")
                        .queryParam("apikey", apiKey)
                        .build(pair))
                .retrieve()
                .bodyToFlux(FmpExchangeRate.class) // Cevap yine liste içinde tek eleman olarak gelebilir
                .next() // Listenin ilk (ve tek) elemanını al
                .map(fmpRate -> {
                    // USDTRY'yi USD/TRY formatına çevirelim
                    String formattedPair = fmpRate.symbol().substring(0, 3) + "/" + fmpRate.symbol().substring(3);
                    return new ExchangeRateDTO(formattedPair, fmpRate.rate());
                });
    }
}