package watchapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import watchapp.dto.*;
import watchapp.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // {userId} path'ten kaldırıldı.
public class WalletController {

    private final AccountService accountService;
    private final MarketDataService marketDataService;
    private final ActionService actionService;
    private final IbanService ibanService;
    private final SupportService supportService;

    // Sabit kullanıcı ID'sini application.yml'den alıyoruz.
    @Value("${demo.user.id}")
    private Long demoUserId;

    public WalletController(AccountService accountService, MarketDataService marketDataService, ActionService actionService, IbanService ibanService, SupportService supportService) {
        this.accountService = accountService;
        this.marketDataService = marketDataService;
        this.actionService = actionService;
        this.ibanService = ibanService;
        this.supportService = supportService;
    }

    // 'Hesabım' ve 'Kredi Kartım' ekranı için
    @GetMapping("/account")
    public ResponseEntity<AccountDTO> getAccountDetails() {
        // Servise artık path'ten gelen bir ID yerine sabit ID'yi veriyoruz.
        return ResponseEntity.ok(accountService.getAccountDetails(demoUserId));
    }

    // 'Hisse Senedi' ekranı (Bu zaten kullanıcıya özel değildi)
    @GetMapping("/stocks")
    public ResponseEntity<List<StockDTO>> getStocks() {
        return ResponseEntity.ok(marketDataService.getRecentStocks());
    }

    // 'Döviz Kuru' ekranı (Bu da kullanıcıya özel değildi)
    @GetMapping("/exchange-rates")
    public ResponseEntity<List<ExchangeRateDTO>> getExchangeRates() {
        return ResponseEntity.ok(marketDataService.getExchangeRates());
    }

    // 'Para Çek' talebi başlatma
    @PostMapping("/withdraw/initiate")
    public ResponseEntity<Void> initiateWithdrawal(@RequestParam double amount) {
        actionService.initiateWithdrawal(demoUserId, amount);
        return ResponseEntity.ok().build();
    }

    // 'Para Çek' onayı (Bu metot userId zaten kullanmıyordu)
    @PostMapping("/withdraw/confirm/{txId}")
    public ResponseEntity<Void> confirmWithdrawal(@PathVariable String txId) {
        actionService.confirmWithdrawal(txId);
        return ResponseEntity.ok().build();
    }

    // 'Geri Bildirim' gönderme
    @PostMapping("/feedback")
    public ResponseEntity<Void> submitFeedback(@RequestBody FeedbackRequestDTO feedbackRequest) {
        actionService.submitFeedback(demoUserId, feedbackRequest);
        return ResponseEntity.ok().build();
    }

    // Kayıtlı IBAN'ları getir
    @GetMapping("/ibans")
    public ResponseEntity<List<SavedIbanDTO>> getSavedIbans() {
        return ResponseEntity.ok(ibanService.getIbansForUser(demoUserId));
    }

    // Yeni IBAN kaydet
    @PostMapping("/ibans")
    public ResponseEntity<SavedIbanDTO> addIban(@RequestBody SavedIbanDTO newIban) {
        SavedIbanDTO createdIban = ibanService.addIbanForUser(demoUserId, newIban);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIban);
    }

    // Kayıtlı IBAN'ı sil
    // Güvenlik kontrolü artık servisin içinde yapılıyor.
    @DeleteMapping("/ibans/{ibanId}")
    public ResponseEntity<Void> deleteIban(@PathVariable Long ibanId) {
        ibanService.deleteIban(demoUserId, ibanId); // Güvenlik için demoUserId'yi de yolluyoruz.
        return ResponseEntity.noContent().build();
    }

    // Dolar al/sat işlemi
    @PostMapping("/exchange/execute")
    public ResponseEntity<Void> executeExchange(@RequestParam String from, @RequestParam String to, @RequestParam double amount) {
        actionService.executeCurrencyExchange(demoUserId, from, to, amount);
        return ResponseEntity.ok().build();
    }

    // Randevu Al
    @PostMapping("/appointments/create")
    public ResponseEntity<String> createAppointment(@RequestParam String phoneNumber) {
        actionService.createAppointmentAndSendSms(phoneNumber);
        return ResponseEntity.ok("Randevu talebi alındı, SMS gönderildi.");
    }

    // Destek endpoint'leri (Bunlar zaten kullanıcıya özel değildi)
    @GetMapping("/support/atms")
    public ResponseEntity<List<AtmLocationDTO>> getAtms() {
        return ResponseEntity.ok(supportService.getNearbyAtms());
    }

    @GetMapping("/support/contact")
    public ResponseEntity<ContactInfoDTO> getContact() {
        return ResponseEntity.ok(supportService.getContactInfo());
    }
}