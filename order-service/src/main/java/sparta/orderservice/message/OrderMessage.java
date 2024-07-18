package sparta.orderservice.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sparta.common.messages.CreateOrderDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage {
    private int userId;
    private List<CreateOrderDto> orderItems;
}
