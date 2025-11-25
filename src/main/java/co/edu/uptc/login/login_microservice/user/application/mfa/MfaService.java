package co.edu.uptc.login.login_microservice.user.application.mfa;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class MfaService {

    private final ConcurrentHashMap<String, String> mfaCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateCode(String userId) {
        String code = String.format("%06d", random.nextInt(999999));
        mfaCodes.put(userId, code);
        return code;
    }

    public boolean verify(String userId, String code) {
        return code.equals(mfaCodes.get(userId));
    }

    public void clear(String userId) {
        mfaCodes.remove(userId);
    }
}