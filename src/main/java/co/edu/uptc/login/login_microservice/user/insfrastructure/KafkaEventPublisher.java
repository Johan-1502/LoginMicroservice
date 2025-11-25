package co.edu.uptc.login.login_microservice.user.insfrastructure;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import co.edu.uptc.login.login_microservice.user.application.events.LoginAlertEvent;
import co.edu.uptc.login.login_microservice.user.application.events.UserLoggedInEvent;

@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserLoggedIn(UserLoggedInEvent event) {
        System.out.println("📤 Enviando evento a Kafka: " + event.getUserId());
        kafkaTemplate.send("user.loggedin", event).whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("✓ Evento enviado a Kafka: " + event.getUserId());
            } else {
                System.out.println("✗ Error enviando evento a Kafka: " + ex.getMessage());
            }
        });
    }

    public void publishLoginAlert(LoginAlertEvent event) {
        kafkaTemplate.send("user.login.alert", event);
    }
}
