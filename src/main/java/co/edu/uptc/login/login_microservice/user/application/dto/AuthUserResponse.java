package co.edu.uptc.login.login_microservice.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthUserResponse {

    @Schema(description = "Indica si el proceso de autenticación fue exitoso", 
            example = "true")
    private boolean success;

    @Schema(description = "Mensaje informativo del resultado",
            example = "Acceso concedido")
    private String message;

    @Schema(description = "Token JWT generado tras autenticación exitosa", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    public AuthUserResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
