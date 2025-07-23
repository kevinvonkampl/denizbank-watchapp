package watchapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import watchapp.dto.AccountDTO;
import watchapp.dto.TransactionDTO;

// Gerekli olan iç sınıfları ObpApiClient'ten import ediyoruz.
import watchapp.service.ObpApiClient.AccountDataPackage;
import watchapp.service.ObpApiClient.ObpAccount;
import watchapp.service.ObpApiClient.ObpTransaction;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final ObpApiClient obpApiClient;

    // Sabit OBP token'ını application.yml dosyasından alıyoruz.
    @Value("${demo.user.obp-auth-token}")
    private String obpAuthToken;

    public AccountService(ObpApiClient obpApiClient) {
        this.obpApiClient = obpApiClient;
    }

    /**
     * Demo kullanıcısının OBP token'ını kullanarak hesap detaylarını (bakiye, IBAN)
     * ve son üç işlemi getiren metot.
     * @param userId Simülasyon gereği alınan ancak aktif olarak kullanılmayan kullanıcı ID'si. Loglama için kullanılabilir.
     * @return Hesap bilgilerini içeren bir AccountDTO nesnesi.
     */
    public AccountDTO getAccountDetails(Long userId) {
        // Bu metot artık userId'yi veritabanı sorgusu için kullanmıyor,
        // ama loglama veya gelecekteki bir özellik için parametre olarak tutulabilir.
        System.out.println("Hesap detayları getiriliyor. Kullanıcı ID (simüle edilen): " + userId);

        // Token'ın application.yml'de ayarlanıp ayarlanmadığını kontrol et.
        if (obpAuthToken == null || obpAuthToken.isBlank() || obpAuthToken.equals("buraya_sizin_uzun_sureli_obp_tokeniniz_gelecek")) {
            throw new IllegalStateException("Geçerli bir OBP token'ı 'application.yml' dosyasında tanımlanmamış. Lütfen 'demo.user.obp-auth-token' alanını güncelleyin.");
        }

        // ObpApiClient üzerinden hem hesap bilgisini hem de transaction'ları tek çağrıda getir.
        // .block() metodu, asenkron işlemin tamamlanmasını bekler ve sonucunu döndürür.
        AccountDataPackage dataPackage = obpApiClient.getAccountDataPackage(obpAuthToken).block();

        // API'den cevap gelmeme durumunu kontrol et.
        if (dataPackage == null) {
            throw new RuntimeException("OBP'den hesap verileri alınamadı. Token geçersiz veya API ulaşılamıyor olabilir.");
        }

        // Gelen paket içerisinden hesap ve işlem bilgilerini çıkar.
        ObpAccount obpAccount = dataPackage.account();
        List<ObpTransaction> obpTransactions = dataPackage.transactions();

        // OBP'den gelen işlem listesini, bizim saat uygulamamızın anlayacağı DTO formatına dönüştür.
        List<TransactionDTO> transactionDTOs = obpTransactions.stream()
                .map(tx -> new TransactionDTO(
                        tx.details().description(),
                        Double.parseDouble(tx.details().value().amount()), // OBP'den gelen para miktarı String olabilir, Double'a çevir.
                        tx.details().completedDate()
                ))
                .collect(Collectors.toList());

        // Son olarak, tüm bu bilgileri birleştirerek ana DTO'muzu oluştur ve döndür.
        return new AccountDTO(
                obpAccount.iban(),
                Double.parseDouble(obpAccount.balance().amount()), // Bakiye de String olabilir, Double'a çevir.
                transactionDTOs
        );
    }
}