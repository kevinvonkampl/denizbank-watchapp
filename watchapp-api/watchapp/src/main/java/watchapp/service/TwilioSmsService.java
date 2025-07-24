package watchapp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private boolean isInitialized = false;

    public TwilioSmsService(
            @Value("${app.twilio.account-sid}") String accountSid,
            @Value("${app.twilio.auth-token}") String authToken,
            @Value("${app.twilio.from-number}") String fromNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
    }

    private void initialize() {
        if (!isInitialized) {
            Twilio.init(accountSid, authToken);
            isInitialized = true;
        }
    }

    public void sendSms(String to, String messageBody) {
        initialize(); // Her seferinde init çağırmamak için kontrol
        Message.creator(
                        new PhoneNumber(to),      // Kime gidecek (örn: +905xxxxxxxxx)
                        new PhoneNumber(fromNumber), // Twilio numaranız
                        messageBody)
                .create();
        System.out.println("SMS gönderildi: " + to);
    }
}