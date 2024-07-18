package sparta.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceResponse {
    private int productId;
    private int price;
}