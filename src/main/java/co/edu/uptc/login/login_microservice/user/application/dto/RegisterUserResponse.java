package co.edu.uptc.login.login_microservice.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RegisterUserResponse {

    @Schema(description = "Indica si el registro fue exitoso",
            example = "true")
    private boolean success;

    @Schema(description = "Mensaje del resultado del registro",
            example = "Usuario registrado")
    private String message;

    public RegisterUserResponse(boolean success, String message){
        this.success = success;
        this.message = message;
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
}
