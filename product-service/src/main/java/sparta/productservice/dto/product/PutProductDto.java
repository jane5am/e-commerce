package sparta.productservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutProductDto {

    private String name;
    private int price;
    private int quantity;
    private String description;
    private String exposeYsno;

}
