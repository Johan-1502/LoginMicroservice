package co.edu.uptc.login.login_microservice.user.insfrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginJpaRepository extends JpaRepository<LoginEntity, Long>{
    LoginEntity findByUserId(Long userId);
}
