package sparta.productservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor // 지정하는 모든 필드에 대해 생성자 만들어줌
@NoArgsConstructor // 매개변수 없는 생성자 만들어줌
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int productId;

    @Column(name = "sellerId", nullable = false)
    private int sellerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성시각 (기본값 현재시각)

    @Column(nullable = true)
    private LocalDateTime updatedAt; // 수정시각

    @Column(nullable = false)
    private String isDelete; // 삭제 상태

    @Column(nullable = false)
    private String status; // 상태, 주문가능 여부


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}