package co.edu.uptc.login.login_microservice.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

import co.edu.uptc.login.login_microservice.shared.domain.AggregateRoot;

public class User extends AggregateRoot<UserId> {
    private Password password;

    public User(UserId id, Password password) {
        super(id);
        this.password = password;
    }

    public Password getPassword() {
        return password;
    }

    public static User restore(UserId userId, Password password) {
        User user = new User(userId, password);
        return user;
    }
}
