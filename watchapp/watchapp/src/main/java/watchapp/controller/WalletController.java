package watchapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import watchapp.dto.*;
import watchapp.service.AccountService;
import watchapp.service.ActionService;
import watchapp.service.IbanService;
import watchapp.service.MarketDataService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{userId}")
public class WalletController {

    private final AccountService accountService;
    private final MarketDataService marketDataService;
    private final ActionService actionService;
    private final IbanService ibanService;

    public WalletController(AccountService accountService, MarketDataService marketDataService, ActionService actionService, IbanService ibanService) {
        this.accountService = accountService;
        this.marketDataService = marketDataService;
        this.actionService = actionService;
        this.ibanService = ibanService;
    }
    // ... diğer service'ler

    // 'Hesabım' ve 'Kredi Kartım' ekranı için
    @GetMapping("/account")
    public ResponseEntity<AccountDTO> getAccountDetails(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountDetails(userId));
    }

    // 'Hisse Senedi' ekranı
    @GetMapping("/stocks")
    public ResponseEntity<List<StockDTO>> getStocks(@PathVariable Long userId) {
        return ResponseEntity.ok(marketDataService.getRecentStocks());
    }

    // 'Döviz Kuru' ekranı
    @GetMapping("/exchange-rates")
    public ResponseEntity<List<ExchangeRateDTO>> getExchangeRates(@PathVariable Long userId) {
        return ResponseEntity.ok(marketDataService.getExchangeRates());
    }

    // 'Para Çek' talebi başlatma
    @PostMapping("/withdraw/initiate")
    public ResponseEntity<Void> initiateWithdrawal(@PathVariable Long userId, @RequestParam double amount) {
        actionService.initiateWithdrawal(userId, amount);
        return ResponseEntity.ok().build();
    }

    // 'Para Çek' onayı (Bildirimden gelinir)
    @PostMapping("/withdraw/confirm/{txId}")
    public ResponseEntity<Void> confirmWithdrawal(@PathVariable Long userId, @PathVariable String txId) {
        actionService.confirmWithdrawal(txId);
        return ResponseEntity.ok().build();
    }

    // 'Geri Bildirim' gönderme
    @PostMapping("/feedback")
    public ResponseEntity<Void> submitFeedback(@PathVariable Long userId, @RequestBody FeedbackRequestDTO feedbackRequest) {
        actionService.submitFeedback(userId, feedbackRequest);
        return ResponseEntity.ok().build();
    }

    // Kayıtlı IBAN'ları getir
    @GetMapping("/ibans")
    public ResponseEntity<List<SavedIbanDTO>> getSavedIbans(@PathVariable Long userId) {
        return ResponseEntity.ok(ibanService.getIbansForUser(userId));
    }

    // Yeni IBAN kaydet
    @PostMapping("/ibans")
    public ResponseEntity<SavedIbanDTO> addIban(@PathVariable Long userId, @RequestBody SavedIbanDTO newIban) {
        SavedIbanDTO createdIban = ibanService.addIbanForUser(userId, newIban);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIban);
    }

    // Kayıtlı IBAN'ı sil
    // NOT: Burada ibanId kullanıyoruz, userId değil. Çünkü her IBAN'ın kendine özel bir ID'si var.
    // Güvenlik için normalde bu IBAN'ın o kullanıcıya ait olup olmadığı da kontrol edilir.
    @DeleteMapping("/ibans/{ibanId}")
    public ResponseEntity<Void> deleteIban(@PathVariable Long userId, @PathVariable Long ibanId) {
        ibanService.deleteIban(ibanId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Dolar al/sat işlemi
    @PostMapping("/exchange/execute")
    public ResponseEntity<Void> executeExchange(@PathVariable Long userId, @RequestParam String from, @RequestParam String to, @RequestParam double amount) {
        actionService.executeCurrencyExchange(userId, from, to, amount);
        return ResponseEntity.ok().build();
    }

    // Randevu Al
    @PostMapping("/appointments/create")
    public ResponseEntity<String> createAppointment(@PathVariable Long userId, @RequestParam String phoneNumber) {
        actionService.createAppointmentAndSendSms(phoneNumber);
        return ResponseEntity.ok("Randevu talebi alındı, SMS gönderildi.");
    }

    @GetMapping("/support/atms")
    public ResponseEntity<List<AtmLocationDTO>> getAtms() {
        return ResponseEntity.ok(supportService.getNearbyAtms());
    }

    @GetMapping("/support/contact")
    public ResponseEntity<ContactInfoDTO> getContact() {
        return ResponseEntity.ok(supportService.getContactInfo());
    }

    // Diğer statik bilgiler için de endpointler eklenebilir (En yakın ATM vs.)
}