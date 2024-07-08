package sparta.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private int id;
    private int orderId;
    private int productId;
    private int shipmentId;
    private int quantity;
    private String shipmentStatus;
}
