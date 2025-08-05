package watchapp.service;

import org.springframework.stereotype.Service;
import watchapp.dto.AtmLocationDTO; // Yeni DTO
import watchapp.dto.ContactInfoDTO; // Yeni DTO

import java.util.List;

@Service
public class SupportService {

    public List<AtmLocationDTO> getNearbyAtms() {
        // Gerçekte burada konum servisi kullanılır. Biz sabit veri döndürelim.
        return List.of(
                new AtmLocationDTO("DenizBank Beşiktaş Şubesi", "Barbaros Blv. No:1, Beşiktaş"),
                new AtmLocationDTO("DenizBank Zincirlikuyu ATM", "Büyükdere Cad. No:185, Şişli")
        );
    }

    public ContactInfoDTO getContactInfo() {
        return new ContactInfoDTO("444 0 800", "DenizBank Çağrı Merkezi");
    }
}