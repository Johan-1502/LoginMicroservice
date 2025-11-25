package co.edu.uptc.login.login_microservice.user.application.events;

public class UserLoggedInEvent {

    private String userId;
    private String loginTime;

    public UserLoggedInEvent() {}

    public UserLoggedInEvent(String userId, String loginTime) {
        this.userId = userId;
        this.loginTime = loginTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getLoginTime() {
        return loginTime;
    }
}
