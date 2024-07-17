package sparta.orderservice.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sparta.orderservice.dto.CreateOrderDto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int userId;
    private List<CreateOrderDto> orderItems;

}