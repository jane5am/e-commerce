package sparta.orderservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "`orderItem`")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderItem extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "orderId", nullable = false)
    private int orderId;

    @Column(name = "productId", nullable = false)
    private int productId;

    @Column(name = "shipmentId", nullable = false)
    private int shipmentId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unitPrice", nullable = false)
    private int unitPrice;

    @Column(name = "totalStm", nullable = false)
    private int totalSum;

}
