// Dosya Yolu: src/main/java/watchapp/service/AccountService.java
package watchapp.service;

import org.springframework.stereotype.Service;
import watchapp.dto.AccountDTO;
import watchapp.dto.TransactionDTO;

import java.util.List;

@Service
public class AccountService {

    // Artık ObpApiClient veya TokenManagerService'e ihtiyacımız yok.
    // Constructor boş kalabilir veya hiç olmayabilir.
    public AccountService() {}

    /**
     * "Hesabım" ekranı için sabit (mock) verileri döndüren metot.
     * Bu, OBP entegrasyonunun çalışmadığı durumlarda demo'nun devam etmesini sağlar.
     * @param userId Loglama için tutulur, veri üretiminde kullanılmaz.
     * @return Önceden tanımlanmış hesap bilgilerini içeren bir AccountDTO nesnesi.
     */
    public AccountDTO getAccountDetails(Long userId) {
        System.out.println("SİMÜLASYON: Sabit hesap detayları getiriliyor. Kullanıcı ID: " + userId);

        // Sabit son 3 işlemi oluşturalım.
        List<TransactionDTO> mockTransactions = List.of(
                new TransactionDTO("Spotify Abonelik", -64.99, "2024-07-25"),
                new TransactionDTO("Starbucks", -110.50, "2024-07-24"),
                new TransactionDTO("Maaş Yatırıldı", 15000.00, "2024-07-23")
        );

        // Sabit hesap bilgilerini (bakiye, IBAN) ve işlemleri bir DTO içinde döndürelim.
        return new AccountDTO(
                "TR33 0006 1005 1978 6457 8413 26", // Sabit bir IBAN
                9923.25, // Sabit bir bakiye
                mockTransactions // Oluşturduğumuz sabit işlemler
        );
    }
}