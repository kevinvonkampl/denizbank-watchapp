//package watchapp.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import watchapp.entity.User;
//import watchapp.repository.UserRepository;
//
//@Service
//public class AuthService {
//
//    private final ObpApiClient obpApiClient;
//    private final UserRepository userRepository;
//
//    public AuthService(ObpApiClient obpApiClient, UserRepository userRepository) {
//        this.obpApiClient = obpApiClient;
//        this.userRepository = userRepository;
//    }
//
//    @Transactional // Bu metodun bir veritabanı işlemi olduğunu belirtir.
//    public void loginAndStoreToken(Long userId, String obpUsername, String obpPassword) {
//        // 1. Önce OBP'ye giriş yapıp token'ı almayı dene.
//        String token = obpApiClient.getAuthToken(obpUsername, obpPassword).block(); // Asenkron işlemi bekle.
//
//        if (token == null || token.isBlank()) {
//            throw new RuntimeException("OBP login failed. Invalid credentials.");
//        }
//
//        // 2. Token başarıyla alındıysa, bizim sistemimizdeki kullanıcıyı bul.
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//
//        // 3. Alınan token'ı kullanıcının ilgili alanına set et ve DB'ye kaydet.
//        user.setObpAuthToken(token);
//        userRepository.save(user);
//    }
//}