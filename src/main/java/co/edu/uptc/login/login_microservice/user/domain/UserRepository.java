package co.edu.uptc.login.login_microservice.user.domain;

import java.util.Optional;

public interface UserRepository {
    User save(User order);
    Optional<User> findByUserId(UserId id);
}