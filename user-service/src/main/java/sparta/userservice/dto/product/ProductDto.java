package sparta.userservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private int productId;
    private String name;
    private int price;
    private String description;
    private String exposeYsno;
    private int quantity;
}
