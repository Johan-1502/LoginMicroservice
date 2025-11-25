package co.edu.uptc.login.login_microservice.user.application.events;

public class MfaRequiredEvent {

    private String userId;
    private String code;

    public MfaRequiredEvent() {
    }

    public MfaRequiredEvent(String userId, String code) {
        this.userId = userId;
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }
}
