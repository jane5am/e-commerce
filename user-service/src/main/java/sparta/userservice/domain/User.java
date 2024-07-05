package sparta.userservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor // 지정하는 모든 필드에 대해 생성자 만들어줌
@NoArgsConstructor // 매개변수 없는 생성자 만들어줌
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String type;// 카카오인지 네이버인지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @PrePersist
    protected void onCreate() {
        this.role = Role.USER;
//        this.type = "web";
    }

    public enum Role {
        USER, SELLER
    }

    public User(String email, String name, String type) {
        this.password = "Passw0rd";
        this.name = name;
        this.email = email;
        this.type = type;
        this.role = Role.USER;

    }
}