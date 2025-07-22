package watchapp.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import watchapp.dto.ExchangeRateDTO;
import watchapp.dto.StockDTO;

import java.util.List;

@Service
public class MarketDataService {

    private final WebClient webClient;
    private final String apiKey;

    public MarketDataService(WebClient.Builder webClientBuilder,
                             @Value("${app.financial-api.alphavantage.base-url}") String baseUrl,
                             @Value("${app.financial-api.alphavantage.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    // --- Alpha Vantage'dan Gelen Cevapları Temsil Eden DTO'lar ---
    private record AlphaVantageQuoteResponse(@JsonProperty("Global Quote") GlobalQuoteData globalQuote) {}
    private record GlobalQuoteData(
            @JsonProperty("01. symbol") String symbol,
            @JsonProperty("05. price") String price,
            @JsonProperty("10. change percent") String changePercent
    ) {}

    private record AlphaVantageExchangeResponse(@JsonProperty("Realtime Currency Exchange Rate") ExchangeRateData exchangeRate) {}
    private record ExchangeRateData(
            @JsonProperty("5. Exchange Rate") String rate,
            @JsonProperty("1. From_Currency Code") String fromCurrency,
            @JsonProperty("3. To_Currency Code") String toCurrency
    ) {}


    // --- Gerçek API Metotları ---

    /**
     * Popüler hisse senetlerinin güncel fiyatlarını Alpha Vantage'dan çeker.
     * Bu metodun sonucu, ilk çağrıdan sonra 30 dakika boyunca önbellekte tutulur.
     */
    @Cacheable("stocks") // "stocks" isimli cache'e sonucu kaydet
    public List<StockDTO> getRecentStocks() {
        System.out.println(">>> HİSSE SENEDİ VERİSİ API'DEN ÇEKİLİYOR (CACHE'DE DEĞİL) <<<");
        // İzlemek istediğimiz hisseler (Yahoo Finance formatı, Türkiye için .IS uzantılı)
        List<String> symbols = List.of("TUPRS.IS", "THYAO.IS", "GARAN.IS");

        // Her bir sembol için asenkron olarak API'yi çağır ve sonuçları birleştir.
        return Flux.fromIterable(symbols)
                .flatMap(this::fetchStockQuote)
                .collectList()
                .block(); // Asenkron işlemlerin bitmesini bekle ve sonucu List olarak al.
    }

    /**
     * Popüler döviz kurlarının güncel değerlerini Alpha Vantage'dan çeker.
     * Bu metodun sonucu da 30 dakika boyunca önbellekte tutulur.
     */
    @Cacheable("exchangeRates") // "exchangeRates" isimli cache'e sonucu kaydet
    public List<ExchangeRateDTO> getExchangeRates() {
        System.out.println(">>> DÖVİZ KURU VERİSİ API'DEN ÇEKİLİYOR (CACHE'DE DEĞİL) <<<");
        List<String[]> currencyPairs = List.of(new String[]{"USD", "TRY"}, new String[]{"EUR", "TRY"}, new String[]{"GBP", "TRY"});

        return Flux.fromIterable(currencyPairs)
                .flatMap(pair -> fetchExchangeRate(pair[0], pair[1]))
                .collectList()
                .block();
    }

    // --- Yardımcı Metotlar ---

    private Flux<StockDTO> fetchStockQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(AlphaVantageQuoteResponse.class)
                .map(response -> {
                    GlobalQuoteData data = response.globalQuote();
                    // Gelen '1.23%' gibi bir stringi double'a çevir
                    double change = Double.parseDouble(data.changePercent().replace("%", ""));
                    return new StockDTO(data.symbol(), Double.parseDouble(data.price()), change);
                })
                .flux(); // Mono'yu Flux'a çevirerek flatMap ile uyumlu hale getir.
    }

    private Flux<ExchangeRateDTO> fetchExchangeRate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "CURRENCY_EXCHANGE_RATE")
                        .queryParam("from_currency", from)
                        .queryParam("to_currency", to)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(AlphaVantageExchangeResponse.class)
                .map(response -> {
                    ExchangeRateData data = response.exchangeRate();
                    return new ExchangeRateDTO(data.fromCurrency() + "/" + data.toCurrency(), Double.parseDouble(data.rate()));
                })
                .flux();
    }
}