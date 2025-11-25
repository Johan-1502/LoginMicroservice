package co.edu.uptc.login.login_microservice.user.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import co.edu.uptc.login.login_microservice.security.JwtService;
import co.edu.uptc.login.login_microservice.user.application.dto.AuthUserRequest;
import co.edu.uptc.login.login_microservice.user.application.events.LoginAlertEvent;
import co.edu.uptc.login.login_microservice.user.application.events.UserLoggedInEvent;
import co.edu.uptc.login.login_microservice.user.domain.UserId;
import co.edu.uptc.login.login_microservice.user.domain.UserRepository;
import co.edu.uptc.login.login_microservice.user.insfrastructure.KafkaEventPublisher;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthUserUseCase {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final KafkaEventPublisher eventPublisher;

    // Contadores en memoria
    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final Map<String, Integer> failedNotRegisteredAttempts = new HashMap<>();

    public AuthUserUseCase(
            UserRepository userRepository,
            JwtService jwtService,
            KafkaEventPublisher eventPublisher) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    public String execute(AuthUserRequest request) {

        String userId = request.getUserId().toString();
        String now = java.time.LocalDateTime.now().toString();

        return userRepository.findByUserId(new UserId(request.getUserId()))
            .map(user -> {
                // ===== Usuario existe =====

                // Validar contraseña
                if (!user.getPassword().getValue().equals(request.getPassword())) {
                    int attempts = failedAttempts.getOrDefault(userId, 0) + 1;
                    failedAttempts.put(userId, attempts);

                    if (attempts >= 3) {
                        eventPublisher.publishLoginAlert(
                            new LoginAlertEvent(
                                userId,
                                now,
                                "LOGIN_USR_ATTEMPS_EXCEEDED"
                            )
                        );
                    }

                    return null;
                }

                // Login correcto → limpiar intentos
                failedAttempts.remove(userId);
                failedNotRegisteredAttempts.remove(userId);

                // Generar token
                String token = jwtService.generateToken(userId);

                // Publicar evento normal
                eventPublisher.publishUserLoggedIn(
                    new UserLoggedInEvent(
                        userId,
                        now
                    )
                );

                return token;
            })
            .orElseGet(() -> {

                // ===== Usuario NO existe =====

                int attempts = failedNotRegisteredAttempts.getOrDefault(userId, 0) + 1;
                failedNotRegisteredAttempts.put(userId, attempts);

                if (attempts >= 2) {
                    eventPublisher.publishLoginAlert(
                        new LoginAlertEvent(
                            userId,
                            now,
                            "LOGIN_USR_NOT_REGISTERED"
                        )
                    );
                }

                return null;
            });
    }
}
