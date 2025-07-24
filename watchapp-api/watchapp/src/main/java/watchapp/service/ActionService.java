package watchapp.service;

import org.springframework.stereotype.Service;
import watchapp.dto.FeedbackRequestDTO;
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
}