package watchapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import watchapp.dto.LoginRequestDTO;
import watchapp.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Login endpoint'i. Kullanıcı OBP kullanıcı adı ve şifresini gönderir.
    // Biz de bu bilgilerle OBP'ye login olup token'ı DB'ye kaydederiz.
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        // userId: bizim sistemimizdeki kullanıcı ID'si
        // OBP'ye giriş yapılacak ve token, bu userId'ye ait kullanıcıya kaydedilecek.
        authService.loginAndStoreToken(loginRequest.getUserId(),
                loginRequest.getObpUsername(),
                loginRequest.getObpPassword());

        return ResponseEntity.ok("Login successful. OBP token stored.");
    }
}