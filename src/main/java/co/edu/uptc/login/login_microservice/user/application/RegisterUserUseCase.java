package co.edu.uptc.login.login_microservice.user.application;

import org.springframework.stereotype.Service;

import co.edu.uptc.login.login_microservice.user.application.dto.RegisterUserRequest;
import co.edu.uptc.login.login_microservice.user.domain.Password;
import co.edu.uptc.login.login_microservice.user.domain.User;
import co.edu.uptc.login.login_microservice.user.domain.UserId;
import co.edu.uptc.login.login_microservice.user.domain.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegisterUserUseCase {
    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User execute(RegisterUserRequest request){
        UserId userId = UserId.of(request.getUserId());

        // Validar si ya existe
        //if (userRepository.findByUserId(userId).isPresent()) {
        //    throw new IllegalStateException("User already exists");
        //}

        Password password = Password.of(request.getPassword());
        User user = new User(userId, password);
        User savedUser = userRepository.save(user);
        return savedUser;
    }
}
