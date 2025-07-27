// Dosya Yolu: watchapp/service/AccountService.java
package watchapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import watchapp.dto.AccountDTO;
import watchapp.dto.TransactionDTO;
import watchapp.service.ObpApiClient.AccountDataPackage;
import watchapp.service.ObpApiClient.ObpAccount;
import watchapp.service.ObpApiClient.ObpTransaction;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final ObpApiClient obpApiClient;

    // Sabit OBP token'ını application.yml dosyasından enjekte ediyoruz.
    @Value("${demo.user.obp-auth-token}")
    private String obpAuthToken;

    public AccountService(ObpApiClient obpApiClient) {
        this.obpApiClient = obpApiClient;
    }

    /**
     * Demo kullanıcısının sabit OBP token'ını kullanarak hesap detaylarını
     * ve son üç işlemi getiren metot.
     * @param userId Loglama gibi amaçlar için alınır, OBP çağrısında doğrudan kullanılmaz.
     * @return Hesap bilgilerini içeren bir AccountDTO nesnesi.
     */
    public AccountDTO getAccountDetails(Long userId) {
        System.out.println("Hesap detayları getiriliyor. Sabit Kullanıcı ID: " + userId);

        // Token'ın application.yml'de ayarlanıp ayarlanmadığını kontrol et.
        if (obpAuthToken == null || obpAuthToken.isBlank() || obpAuthToken.equals("${OBP_STATIC_TOKEN}")) {
            throw new IllegalStateException("Geçerli bir OBP token'ı 'application.yml' dosyasında 'demo.user.obp-auth-token' olarak tanımlanmamış veya ortam değişkeni bulunamamıştır.");
        }

        // ObpApiClient üzerinden hem hesap bilgisini hem de transaction'ları tek çağrıda getir.
        // Artık veritabanından token okumaya gerek yok, doğrudan enjekte edilen token'ı kullanıyoruz.
        AccountDataPackage dataPackage = obpApiClient.getAccountDataPackage(obpAuthToken).block();

        if (dataPackage == null) {
            throw new RuntimeException("OBP'den hesap verileri alınamadı. Token geçersiz veya API ulaşılamıyor olabilir.");
        }

        ObpAccount obpAccount = dataPackage.account();
        List<ObpTransaction> obpTransactions = dataPackage.transactions();

        List<TransactionDTO> transactionDTOs = obpTransactions.stream()
                .map(tx -> new TransactionDTO(
                        tx.details().description(),
                        Double.parseDouble(tx.details().value().amount()),
                        tx.details().completedDate()
                ))
                .collect(Collectors.toList());

        return new AccountDTO(
                obpAccount.iban(),
                Double.parseDouble(obpAccount.balance().amount()),
                transactionDTOs
        );
    }
}