// Dosya Yolu: watchapp/controller/WalletController.java
package watchapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import watchapp.dto.*;
import watchapp.service.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1") // Sınıf seviyesinde ana yolu belirliyoruz, daha temiz.
public class WalletController {

    private final AccountService accountService;
    private final MarketDataService marketDataService;
    private final ActionService actionService;
    private final IbanService ibanService;
    private final SupportService supportService;

    // Sabit kullanıcı ID'sini application.yml'den enjekte ediyoruz.
    @Value("${demo.user.id}")
    private Long demoUserId;

    public WalletController(AccountService accountService, MarketDataService marketDataService, ActionService actionService, IbanService ibanService, SupportService supportService) {
        this.accountService = accountService;
        this.marketDataService = marketDataService;
        this.actionService = actionService;
        this.ibanService = ibanService;
        this.supportService = supportService;
    }

    // 'Hesabım' ekranı için - Artık path'te userId yok.
    @GetMapping("/account")
    public ResponseEntity<AccountDTO> getAccountDetails() {
        // Servise artık path'ten gelen bir ID yerine sabit demoUserId'yi veriyoruz.
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

    // 'Para Çek' onayı (Bu metot zaten userId kullanmıyordu)
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
    @DeleteMapping("/ibans/{ibanId}")
    public ResponseEntity<Void> deleteIban(@PathVariable Long ibanId) {
        // Güvenlik için demoUserId'yi servise göndererek,
        // sadece bu kullanıcıya ait IBAN'ın silinmesini sağlıyoruz.
        ibanService.deleteIban(demoUserId, ibanId);
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

    // QR-Wallet: Ödeme için QR kod oluşturma
    @PostMapping("/payment/qr/generate")
    public ResponseEntity<QrCodeDTO> generateQrCode(@RequestParam double amount) {
        // Sabit demo kullanıcımız için QR kod oluşturuyoruz.
        QrCodeDTO qrCode = actionService.generatePaymentQrCode(demoUserId, amount);
        return ResponseEntity.ok(qrCode);
    }

    // Fatura Ödeme: Fatura ödeme talebi gönderme
    @PostMapping("/bills/pay")
    public ResponseEntity<String> payBill(@RequestBody BillPaymentRequestDTO billPaymentRequest) {
        actionService.payBill(demoUserId, billPaymentRequest);
        return ResponseEntity.ok("Fatura ödeme talebi başarıyla alındı ve işleme konuldu.");
    }
}