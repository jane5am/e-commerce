package sparta.userservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.userservice.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { // 엔티티 클래스, PK의 타입
    Optional<User> findByName(String name);
    
    Optional<User> findByEmail(String email);



}