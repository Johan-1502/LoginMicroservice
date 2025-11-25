package co.edu.uptc.login.login_microservice.user.insfrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login")
public class LoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL autoincrement
    private Long id;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "password", nullable = false, length = 20)
    private String password;

    public LoginEntity() {}

    public LoginEntity(Long userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
