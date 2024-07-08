package sparta.orderservice.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "`shipment`")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Shipment extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    public enum ShipmentStatus {
        ORDERED, // 주문완료
        SHIPPED, // 배송중
        DELIVERED, // 배송완료
        CANCELLED, // 취소완료
        RETURNED // 반품중
    }
}
