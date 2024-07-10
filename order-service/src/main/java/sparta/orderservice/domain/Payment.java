package sparta.orderservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "`payment`")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Payment extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "orderId", nullable = false)
    private int orderId;

    @Column(name = "paymentDate", nullable = false)
    private Date paymentDate;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "paymentMethod", nullable = false)
    private String paymentMethod;
}
