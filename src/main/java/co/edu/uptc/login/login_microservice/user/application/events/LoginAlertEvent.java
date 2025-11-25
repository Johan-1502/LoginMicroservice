package co.edu.uptc.login.login_microservice.user.application.events;

public class LoginAlertEvent {

    private String userId;
    private String loginTime;
    private String alertCode;

    public LoginAlertEvent() {
    }

    public LoginAlertEvent(String userId, String loginTime, String alertCode) {
        this.userId = userId;
        this.loginTime = loginTime;
        this.alertCode = alertCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public String getAlertCode() {
        return alertCode;
    }
}
