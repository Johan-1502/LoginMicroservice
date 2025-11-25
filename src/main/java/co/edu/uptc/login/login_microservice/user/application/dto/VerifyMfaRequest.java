package co.edu.uptc.login.login_microservice.user.application.dto;

public class VerifyMfaRequest {
    private String userId;
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

