package watchapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import watchapp.dto.SavedIbanDTO;
import watchapp.entity.SavedIban;
import watchapp.repository.SavedIbanRepository;
import watchapp.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IbanService {

    private final SavedIbanRepository ibanRepository;
    private final UserRepository userRepository;

    public IbanService(SavedIbanRepository ibanRepository, UserRepository userRepository) {
        this.ibanRepository = ibanRepository;
        this.userRepository = userRepository;
    }

    public List<SavedIbanDTO> getIbansForUser(Long userId) {
        return ibanRepository.findByUserId(userId).stream()
                .map(iban -> new SavedIbanDTO(iban.getNickname(), iban.getIban()))
                .collect(Collectors.toList());
    }

    public SavedIbanDTO addIbanForUser(Long userId, SavedIbanDTO newIbanDto) {
        // Kullanıcının var olup olmadığını kontrol et
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));

        SavedIban savedIban = new SavedIban();
        savedIban.setUserId(userId);
        savedIban.setNickname(newIbanDto.nickname());
        savedIban.setIban(newIbanDto.iban());

        ibanRepository.save(savedIban);

        return newIbanDto;
    }

    @Transactional
    public void deleteIban(Long userId, Long ibanId) {
        // Önce silinmek istenen IBAN'ı bul.
        SavedIban ibanToDelete = ibanRepository.findById(ibanId)
                .orElseThrow(() -> new RuntimeException("Silinecek IBAN bulunamadı: " + ibanId));

        // Bu IBAN'ın, isteği yapan kullanıcıya ait olup olmadığını kontrol et.
        // Bu, başkasının IBAN'ını silmesini engeller.
        if (!ibanToDelete.getUserId().equals(userId)) {
            throw new SecurityException("Bu IBAN'ı silme yetkiniz yok.");
        }

        ibanRepository.delete(ibanToDelete);
    }
}