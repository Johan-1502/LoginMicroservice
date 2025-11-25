package co.edu.uptc.login.login_microservice.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class VerifyMfaRequest {

    @Schema(description = "ID del usuario que intenta verificar MFA",
            example = "12345")
    private String userId;

    @Schema(description = "Código MFA enviado al usuario",
            example = "938201")
    private String code;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
