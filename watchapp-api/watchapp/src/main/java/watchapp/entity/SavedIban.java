// Dosya Yolu: src/main/java/watchapp/entity/SavedIban.java
package watchapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "saved_ibans")
public class SavedIban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu IBAN'ın hangi kullanıcıya ait olduğunu belirtir.
    // User entity ile direkt @ManyToOne ilişkisi de kurulabilir,
    // ama basitlik için şimdilik sadece ID'sini tutmak yeterli.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true) // IBAN'ların benzersiz olması mantıklıdır.
    private String iban;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}