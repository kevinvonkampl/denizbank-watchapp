package watchapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObpAuthToken() {
        return obpAuthToken;
    }

    public void setObpAuthToken(String obpAuthToken) {
        this.obpAuthToken = obpAuthToken;
    }

    private String obpAuthToken;

}