package watchapp.service;

import org.springframework.stereotype.Service;
import watchapp.dto.BillPaymentRequestDTO;
import watchapp.dto.FeedbackRequestDTO;
import watchapp.dto.QrCodeDTO;
import watchapp.entity.Feedback;
import watchapp.entity.User;
import watchapp.repository.FeedbackRepository;
import watchapp.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class ActionService {
    private final FeedbackRepository feedbackRepository;
    private final TwilioSmsService twilioSmsService;
    private final UserRepository userRepository;
    private final ObpApiClient obpApiClient;

    public ActionService(FeedbackRepository feedbackRepository, TwilioSmsService twilioSmsService, UserRepository userRepository, ObpApiClient obpApiClient) {
        this.feedbackRepository = feedbackRepository;
        this.twilioSmsService = twilioSmsService;
        this.userRepository = userRepository;
        this.obpApiClient = obpApiClient;
    }


    // Para çekme, dolar al/sat gibi işlemlerin mantığı burada olacak.
    // Şimdilik sadece loglama veya basit DB işlemleri yapıyoruz.

    public void initiateWithdrawal(Long userId, double amount) {
        // 1. Gerçekte burada bir işlem kaydı oluşturulur (pending state).
        // 2. Push notification servisi tetiklenir.
        System.out.println("Kullanıcı " + userId + " için " + amount + " TL'lik çekim talebi oluşturuldu. Onay bekleniyor...");
    }

    public void confirmWithdrawal(String transactionId) {
        System.out.println(transactionId + " nolu çekim işlemi onaylandı.");
    }

    public void submitFeedback(Long userId, FeedbackRequestDTO request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setMessage(request.message());
        feedback.setSubmittedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);
    }

    /**
     * OBP'nin para transferi API'sini kullanarak kullanıcının kendi
     * hesapları arasında döviz alım/satım işlemi yapar.
     */
    public void executeCurrencyExchange(Long userId, String fromCurrency, String toCurrency, double amount) {
        User user = userRepository.findById(userId).orElseThrow(/*...*/);
        String token = user.getObpAuthToken();

        // 1. Kullanıcının TRY ve USD hesaplarını bulmak için OBP'ye istek at.
        // ObpApiClient.ObpAccount tryAccount = obpApiClient.findAccountByCurrency(token, "TRY").block();
        // ObpApiClient.ObpAccount usdAccount = obpApiClient.findAccountByCurrency(token, "USD").block();

        System.out.println("Kullanıcı " + userId + " için " + amount + " " + fromCurrency + " -> " + toCurrency + " işlemi başlatılıyor...");

        // 2. Para transferi isteğini başlat.
        // obpApiClient.createTransactionRequest(token, tryAccount.bankId(), tryAccount.id(), usdAccount.bankId(), usdAccount.id(), amount, toCurrency).block();
    }

    /**
     * Randevu talebi alır ve kullanıcıya Twilio üzerinden SMS gönderir.
     */
    public void createAppointmentAndSendSms(String userPhoneNumber) {
        // Gerçekte burada randevu DB'ye kaydedilebilir.
        System.out.println("Randevu talebi alındı. SMS gönderiliyor...");

        String message = "Anlık Cüzdan: Bankamızdaki randevunuz başarıyla oluşturulmuştur. Sağlıklı günler dileriz.";
        twilioSmsService.sendSms(userPhoneNumber, message);
    }

    /**
     * QR kod ile ödeme için bir QR kodu verisi oluşturur.
     * Bu metot, gerçekte bir ödeme isteği ID'si oluşturup bunu bir URL'ye çevirir
     * ve son kullanma tarihiyle birlikte DTO olarak döndürür.
     * @param userId işlemi başlatan kullanıcının ID'si.
     * @param amount QR kod ile yapılacak ödemenin tutarı.
     * @return QR kod verisini ve son kullanma tarihini içeren bir QrCodeDTO.
     */
    public QrCodeDTO generatePaymentQrCode(Long userId, double amount) {
        // Gerçek bir sistemde, benzersiz bir işlem ID'si oluşturulur ve veritabanına kaydedilir.
        String transactionId = java.util.UUID.randomUUID().toString();
        System.out.println("Kullanıcı " + userId + " için " + amount + " TL'lik QR ödeme kodu oluşturuldu. ID: " + transactionId);

        // QR kodun içine gömülecek veri. Genellikle bir URL olur.
        // Örn: "watchapp://pay?txId=...&amount=..."
        String qrData = String.format("watchapp-payment:user_id=%d,amount=%.2f,tx_id=%s", userId, amount, transactionId);

        // QR kodunun 2 dakika sonra geçersiz olacağını belirtelim.
        String expiresAt = java.time.LocalDateTime.now().plusMinutes(2).toString();

        return new QrCodeDTO(qrData, expiresAt);
    }

    /**
     * Fatura ödeme işlemini simüle eder.
     * Gerçek bir sistemde bu metot, OBP'nin veya başka bir bankacılık sağlayıcısının
     * fatura ödeme API'sini çağırır.
     * @param userId ödemeyi yapan kullanıcının ID'si.
     * @param request Fatura ve abone bilgilerini içeren DTO.
     */
    public void payBill(Long userId, BillPaymentRequestDTO request) {
        // 1. Kullanıcının OBP token'ını al (eğer OBP işlemi yapılacaksa).
        // User user = userRepository.findById(userId).orElseThrow(...);
        // String token = user.getObpAuthToken();

        // 2. Simülasyon: Sadece loglama yapıyoruz.
        System.out.println(String.format(
                "FATURA ÖDEME SİMÜLASYONU: Kullanıcı %d, Kurum: %s, Abone No: %s, Tutar: %.2f TL",
                userId,
                request.billerCode(),
                request.subscriberNumber(),
                request.amount()
        ));

        // 3. Gerçek senaryo:
        // obpApiClient.executeBillPayment(token, request.billerCode(), request.subscriberNumber(), request.amount()).block();
        // Bu metot ObpApiClient içinde tanımlanmalı.
    }
}