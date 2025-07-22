package watchapp.service;

import org.springframework.stereotype.Service;
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

    public void deleteIban(Long ibanId) {
        // IBAN'ın var olup olmadığını kontrol et
        if (!ibanRepository.existsById(ibanId)) {
            throw new RuntimeException("Silinecek IBAN bulunamadı: " + ibanId);
        }
        ibanRepository.deleteById(ibanId);
    }
}