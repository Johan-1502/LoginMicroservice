package co.edu.uptc.login.login_microservice.user.application;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            MfaService mfaService
    ) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.mfaService = mfaService;
    }

    public String execute(AuthUserRequest request) {

        // Siempre obtener el MISMO userId limpio
        String userId = request.getUserId().toString();  // <-- Se usa EL MISMO para TODO

        LocalDateTime now = LocalDateTime.now();

        // ========================
        // 1. VERIFICAR SI ESTÁ BLOQUEADO
        // ========================
        if (lockedUsers.containsKey(userId)) {
            LocalDateTime lockedUntil = lockedUsers.get(userId);

            if (lockedUntil.isAfter(now)) {
                long minutesLeft = java.time.Duration.between(now, lockedUntil).toMinutes();
                throw new RuntimeException("Usuario bloqueado por " + minutesLeft + " minutos");
            } else {
                // Desbloquear porque ya pasó el tiempo
                lockedUsers.remove(userId);
                failedAttempts.remove(userId);
            }
        }

        // ========================
        // 2. REVISAR SI USUARIO EXISTE
        // ========================
        return userRepository.findByUserId(new UserId(Long.parseLong(userId)))
                .map(user -> handleExistingUser(user, request, userId, now))
                .orElseGet(() -> handleNonExistingUser(userId, now));
    }

    // ==========================================================
    // USUARIO EXISTE
    // ==========================================================
    private String handleExistingUser(User user, AuthUserRequest request,
            String userId, LocalDateTime now) {

        // Contraseña incorrecta
        if (!user.getPassword().getValue().equals(request.getPassword())) {

            int attempts = failedAttempts.getOrDefault(userId, 0) + 1;
            failedAttempts.put(userId, attempts);

            if (attempts >= 3) {

                // Bloquear 10 min
                lockedUsers.put(userId, LocalDateTime.now().plusMinutes(10));

                // Mandar alerta Kafka
                eventPublisher.publishLoginAlert(
                        new LoginAlertEvent(
                                userId,
                                now.toString(),
                                "LOGIN_USR_ATTEMPTS_EXCEEDED"
                        )
                );
            }

            return null;
        }

        // ========================
        // Contraseña correcta
        // ========================
        failedAttempts.remove(userId);
        lockedUsers.remove(userId);

        // Enviar MFA
        String code = mfaService.generateCode(user.getId().value().toString());
        eventPublisher.publishMfaEvent(new MfaRequiredEvent(userId, code));

        return "MFA code sent";
    }

    // ==========================================================
    // USUARIO NO EXISTE
    // ==========================================================
    private String handleNonExistingUser(String userId, LocalDateTime now) {

        int attempts = failedNotRegisteredAttempts.getOrDefault(userId, 0) + 1;
        failedNotRegisteredAttempts.put(userId, attempts);

        if (attempts >= 2) {
            Optional<User> user = userRepository.findByUserId(new UserId(Long.parseLong(userId)));
            if (user.isEmpty()) {
                eventPublisher.publishLoginAlert(
                        new LoginAlertEvent(
                                userId,
                                now.toString(),
                                "LOGIN_USR_NOT_REGISTERED"
                        )
                );
            }
        }

        return null;
    }
}
