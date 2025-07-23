package watchapp.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ObpApiClient {

    private final WebClient webClient;
    private final String consumerKey;
    // 'consumerSecret' gibi alanları burada tanımlamaya gerek yok, çünkü sadece login'de kullanılıyor
    // ve doğrudan constructor'da alınabilir.

    // DOĞRU YÖNTEM: Constructor'a parametre olarak @Value ile değerleri alıyoruz.
    public ObpApiClient(WebClient.Builder webClientBuilder,
                        @Value("${obp.api.base-url}") String baseUrl,
                        @Value("${obp.api.consumer-key}") String consumerKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.consumerKey = consumerKey;
    }

    // ===================================================================
    // OBP'den Gelen Cevapları Temsil Eden DTO'lar (İç İçe Sınıflar)
    // ===================================================================

    // Hesap ve Bakiye DTO'ları
    public record ObpAccount(String id, String label, String iban, @JsonProperty("bank_id") String bankId, ObpBalance balance) {}
    public record ObpBalance(String currency, String amount) {}
    private record ObpAccountList(List<ObpAccount> accounts) {}

    // İşlem (Transaction) DTO'ları
    public record ObpTransaction(String id, @JsonProperty("details") ObpTransactionDetails details) {}
    public record ObpTransactionDetails(String type, String description, @JsonProperty("completed") String completedDate, @JsonProperty("value") ObpTransactionValue value) {}
    public record ObpTransactionValue(String currency, String amount) {}
    private record ObpTransactionList(List<ObpTransaction> transactions) {}

    // Token DTO'su
    private record ObpToken(String token) {}

    // Birden fazla API çağrısının sonucunu birleştirmek için yardımcı DTO
    public record AccountDataPackage(ObpAccount account, List<ObpTransaction> transactions) {}


    // ===================================================================
    // Gerçek API Metotları
    // ===================================================================

    public Mono<String> getAuthToken(String username, String password) {
        String authHeader = String.format(
                "DirectLogin username=\"%s\", password=\"%s\", consumer_key=\"%s\"",
                username, password, this.consumerKey
        );

        return this.webClient.post()
                .uri("/obp/v4.0.0/my/logins/direct")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(ObpToken.class)
                .map(ObpToken::token);
    }

    public Mono<ObpAccount> getPrimaryAccount(String authToken) {
        // Auth header'ı artık "DirectLogin token" değil, doğrudan "DirectLogin token=..." olmalı.
        String authHeader = String.format("DirectLogin token=\"%s\"", authToken);

        return this.webClient.get()
                .uri("/obp/v4.0.0/my/accounts")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ObpAccountList.class)
                .map(accountList -> {
                    if (accountList.accounts() == null || accountList.accounts().isEmpty()) {
                        throw new RuntimeException("Kullanıcıya ait OBP hesabı bulunamadı.");
                    }
                    return accountList.accounts().get(0);
                });
    }

    public Mono<List<ObpTransaction>> getRecentTransactions(String authToken, String bankId, String accountId, int limit) {
        String authHeader = String.format("DirectLogin token=\"%s\"", authToken);

        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/obp/v4.0.0/banks/{bankId}/accounts/{accountId}/owner/transactions")
                        .queryParam("limit", limit)
                        .build(bankId, accountId))
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ObpTransactionList.class)
                .map(ObpTransactionList::transactions);
    }

    public Mono<AccountDataPackage> getAccountDataPackage(String authToken) {
        return getPrimaryAccount(authToken)
                .flatMap(account -> {
                    Mono<List<ObpTransaction>> transactionsMono = getRecentTransactions(authToken, account.bankId(), account.id(), 3);
                    // Düzeltme: Burada new AccountDataPackage(...) demeliyiz, AccountService'e referans vermeden.
                    return Mono.zip(Mono.just(account), transactionsMono)
                            .map(tuple -> new AccountDataPackage(tuple.getT1(), tuple.getT2()));
                });
    }

    // Para transferi için simüle edilmiş metot (içi hala boş).
    public Mono<Void> createTransactionRequest(String authToken, String fromBankId, String fromAccountId, String toBankId, String toAccountId, double amount, String currency) {
        System.out.println("OBP PARA TRANSFERİ SİMÜLASYONU: " + amount + " " + currency + " gönderiliyor...");
        // Gerçek implementasyon buraya gelecek.
        return Mono.empty();
    }
}