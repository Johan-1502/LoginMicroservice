package co.edu.uptc.login.login_microservice.user.insfrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import co.edu.uptc.login.login_microservice.user.domain.Password;
import co.edu.uptc.login.login_microservice.user.domain.User;
import co.edu.uptc.login.login_microservice.user.domain.UserId;
import co.edu.uptc.login.login_microservice.user.domain.UserRepository;

@Repository
public class LoginRepositoryImpl implements UserRepository {
    private final LoginJpaRepository jpaRepository;

    public LoginRepositoryImpl(LoginJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        LoginEntity entity = new LoginEntity(user.getId().value(), user.getPassword().getValue());
        jpaRepository.save(entity);
        return user;
    }

    @Override
    public Optional<User> findByUserId(UserId id) {
        LoginEntity entity = jpaRepository.findByUserId(id.value());
        if (entity == null) return Optional.empty();

        return Optional.of(
            new User(
                UserId.of(entity.getUserId()),
                Password.of(entity.getPassword())
            )
        );
    }

}
