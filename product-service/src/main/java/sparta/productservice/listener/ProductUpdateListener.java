package sparta.productservice.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparta.common.messages.CreateOrderDto;
import sparta.productservice.product.ProductService;

@Component
@Log4j2
public class ProductUpdateListener {

    @Autowired
    private ProductService productService;

    public void receiveMessage(CreateOrderDto createOrderDto) {
        log.info("Received update for product: " + createOrderDto.getProductId() + ", quantity: " + createOrderDto.getQuantity());
        productService.updateStock(createOrderDto);
    }
}
