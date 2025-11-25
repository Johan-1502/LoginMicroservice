package co.edu.uptc.login.login_microservice.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uptc.login.login_microservice.security.JwtService;
import co.edu.uptc.login.login_microservice.user.application.AuthUserUseCase;
import co.edu.uptc.login.login_microservice.user.application.RegisterUserUseCase;
import co.edu.uptc.login.login_microservice.user.application.dto.AuthUserRequest;
import co.edu.uptc.login.login_microservice.user.application.dto.AuthUserResponse;
import co.edu.uptc.login.login_microservice.user.application.dto.RegisterUserRequest;
import co.edu.uptc.login.login_microservice.user.application.dto.RegisterUserResponse;
import co.edu.uptc.login.login_microservice.user.application.dto.VerifyMfaRequest;
import co.edu.uptc.login.login_microservice.user.application.dto.VerifyMfaResponse;
import co.edu.uptc.login.login_microservice.user.application.mfa.MfaService;
import co.edu.uptc.login.login_microservice.user.domain.User;
import co.edu.uptc.login.login_microservice.user.insfrastructure.KafkaEventPublisher;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthUserUseCase authUserUseCase;
    private final MfaService mfaService;
    private final JwtService jwtService;
    private final KafkaEventPublisher eventPublisher;

    public LoginController(RegisterUserUseCase registerUserUseCase, AuthUserUseCase authUserUseCase, MfaService mfaService,JwtService jwtService, KafkaEventPublisher eventPublisher) {
        this.registerUserUseCase = registerUserUseCase;
        this.authUserUseCase = authUserUseCase;
        this.mfaService = mfaService;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/createuser")
    public RegisterUserResponse registerUser(@RequestBody RegisterUserRequest request) {
        try {
            User user = registerUserUseCase.execute(request);
            return new RegisterUserResponse(true, "Usuario registrado");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new RegisterUserResponse(false, "Error al registrar el usuario");
        }
    }

    @PostMapping("/authuser")
    public AuthUserResponse authUser(@RequestBody AuthUserRequest request) {
        try {
            String token = authUserUseCase.execute(request);

            if (token != null) {
                return new AuthUserResponse(true, "Acceso concedido", token);
            } else {
                return new AuthUserResponse(false, "Acceso denegado", null);
            }

        } catch (Exception e) {
            System.err.println(e);
            return new AuthUserResponse(false, e.getMessage(), null);
        }
    }

    @PostMapping("/verify-mfa")
    public VerifyMfaResponse verifyMfa(@RequestBody VerifyMfaRequest request) {

        if (!mfaService.verify(request.getUserId(), request.getCode())) {
            return new VerifyMfaResponse(false, "Acceso denegado - Código MFA inválido", null);
        }

        mfaService.clear(request.getUserId());

        String token = jwtService.generateToken(request.getUserId());

        return new VerifyMfaResponse(true, "Acceso concedido", token);
    }
}
