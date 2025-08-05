package watchapp.dto;

import lombok.Data;

@Data // Getter, Setter, toString vs. otomatik oluşturur.
public class LoginRequestDTO {
    private Long userId;
    private String obpUsername;
    private String obpPassword;
}