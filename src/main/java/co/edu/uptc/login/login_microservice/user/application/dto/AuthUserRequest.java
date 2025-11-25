package co.edu.uptc.login.login_microservice.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthUserRequest {

    @Schema(description = "ID del usuario que intenta autenticarse",
            example = "12345")
    private Long userId;

    @Schema(description = "Contraseña del usuario", 
            example = "MySecurePassword123")
    private String password;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
