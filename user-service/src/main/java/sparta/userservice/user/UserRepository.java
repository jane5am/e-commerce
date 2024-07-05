package sparta.userservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.userservice.domain.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> { // 엔티티 클래스, PK의 타입
    Optional<User> findByName(String name);
    
    Optional<User> findByEmail(String email);

}