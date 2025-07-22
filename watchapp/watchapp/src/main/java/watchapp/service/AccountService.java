package watchapp.service;

import org.springframework.stereotype.Service;
import watchapp.dto.AccountDTO;
import watchapp.dto.TransactionDTO;
import watchapp.entity.User;
import watchapp.mapper.TransactionMapper;
import watchapp.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final ObpApiClient obpApiClient;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    // OBP'den gelen verileri bir arada tutmak için bir "paket" record'u.
    public record AccountDataPackage(ObpApiClient.ObpAccount account, List<ObpApiClient.ObpTransaction> transactions) {}

    public AccountService(ObpApiClient obpApiClient, UserRepository userRepository, TransactionMapper transactionMapper) {
        this.obpApiClient = obpApiClient;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
    }

    public AccountDTO getAccountDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: ID " + userId));

        String obpAuthToken = user.getObpAuthToken();
        if (obpAuthToken == null || obpAuthToken.isBlank()) {
            throw new IllegalStateException("Kullanıcı OBP sistemine giriş yapmamış. Lütfen /auth/login endpoint'ini kullanın.");
        }

        // Asenkron işlemi bekle ve sonucu al.
        AccountDataPackage dataPackage = obpApiClient.getAccountDataPackage(obpAuthToken).block();

        if (dataPackage == null) {
            throw new RuntimeException("OBP'den veri alınamadı.");
        }

        ObpApiClient.ObpAccount obpAccount = dataPackage.account();
        List<ObpApiClient.ObpTransaction> obpTransactions = dataPackage.transactions();

        // MapStruct, record'ları doğrudan DTO'ya çeviremez, bu yüzden manuel bir dönüşüm yapalım.
        // Bu kısım MapStruct'sız, en basit haliyle.
        List<TransactionDTO> transactionDTOs = obpTransactions.stream()
                .map(tx -> new TransactionDTO(
                        tx.details().description(),
                        Double.parseDouble(tx.details().value().amount()), // String'i double'a çevir
                        tx.details().completedDate()
                ))
                .collect(Collectors.toList());

        return new AccountDTO(
                obpAccount.iban(),
                Double.parseDouble(obpAccount.balance().amount()), // String'i double'a çevir
                transactionDTOs
        );
    }
}