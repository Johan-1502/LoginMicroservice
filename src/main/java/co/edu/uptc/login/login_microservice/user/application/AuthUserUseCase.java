package co.edu.uptc.login.login_microservice.user.application;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import co.edu.uptc.login.login_microservice.user.application.dto.AuthUserRequest;
import co.edu.uptc.login.login_microservice.user.application.events.LoginAlertEvent;
import co.edu.uptc.login.login_microservice.user.application.events.MfaRequiredEvent;
import co.edu.uptc.login.login_microservice.user.application.mfa.MfaService;
import co.edu.uptc.login.login_microservice.user.domain.User;
import co.edu.uptc.login.login_microservice.user.domain.UserId;
import co.edu.uptc.login.login_microservice.user.domain.UserRepository;
import co.edu.uptc.login.login_microservice.user.insfrastructure.KafkaEventPublisher;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthUserUseCase {

    private final UserRepository userRepository;
    private final KafkaEventPublisher eventPublisher;
    private final MfaService mfaService;

    // Intentos fallidos de usuarios existentes
    private final Map<String, Integer> failedAttempts = new HashMap<>();

    // Intentos fallidos de usuarios NO registrados
    private final Map<String, Integer> failedNotRegisteredAttempts = new HashMap<>();

    // Usuarios bloqueados (por 10 minutos)
    private final Map<String, LocalDateTime> lockedUsers = new HashMap<>();

    public AuthUserUseCase(
            UserRepository userRepository,
            KafkaEventPublisher eventPublisher,
            MfaService mfaService) {
        this.mfaService = mfaService;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public String execute(AuthUserRequest request) {

        String userId = request.getUserId().toString();
        LocalDateTime now = LocalDateTime.now();

        // ========================
        // 1. VERIFICAR BLOQUEO
        // ========================
        if (lockedUsers.containsKey(userId)) {
            LocalDateTime lockTime = lockedUsers.get(userId);
            if (lockTime.isAfter(now)) {
                long minutesLeft = java.time.Duration.between(now, lockTime).toMinutes();
                throw new RuntimeException("Usuario bloqueado por " + minutesLeft + " minutos");
            } else {
                // Se venció bloqueo → limpiar
                lockedUsers.remove(userId);
                failedAttempts.remove(userId);
            }
        }

        // ========================
        // 2. USUARIO EXISTE
        // ========================
        return userRepository.findByUserId(new UserId(request.getUserId()))
                .map(user -> handleExistingUser(user, request, userId, now))
                .orElseGet(() -> handleNonExistingUser(userId, now.toString()));
    }

    private String handleExistingUser(User user, AuthUserRequest request,
                                      String userId, LocalDateTime now) {

        // Validar contraseña
        if (!user.getPassword().getValue().equals(request.getPassword())) {

            int attempts = failedAttempts.getOrDefault(userId, 0) + 1;
            failedAttempts.put(userId, attempts);

            // 3 intentos → bloqueo + alerta Kafka
            if (attempts >= 3) {

                lockedUsers.put(userId, LocalDateTime.now().plusMinutes(10));

                eventPublisher.publishLoginAlert(
                        new LoginAlertEvent(
                                userId,
                                now.toString(),
                                "LOGIN_USR_ATTEMPS_EXCEEDED"
                        )
                );
            }

            return null;
        }

        // Login correcto → resetear
        failedAttempts.remove(userId);
        lockedUsers.remove(userId);

        // Enviar MFA
        String code = mfaService.generateCode(user.getId().value().toString());
        eventPublisher.publishMfaEvent(new MfaRequiredEvent(userId, code));

        return "MFA code sent";
    }

    private String handleNonExistingUser(String userId, String now) {

        int attempts = failedNotRegisteredAttempts.getOrDefault(userId, 0) + 1;
        failedNotRegisteredAttempts.put(userId, attempts);

        // A partir del segundo intento → alerta
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
    }
}
