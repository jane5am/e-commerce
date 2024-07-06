package sparta.productservice.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`stock`")
@Data
@AllArgsConstructor // 지정하는 모든 필드에 대해 생성자 만들어줌
@NoArgsConstructor // 매개변수 없는 생성자 만들어줌
@EqualsAndHashCode(callSuper = true)
public class Stock extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "productId", nullable = false)
    private int productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
