package sparta.productservice.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparta.common.messages.ProductPriceRequest;
import sparta.common.messages.ProductPriceResponse;
import sparta.productservice.product.ProductService;

@Component
@Log4j2
public class ProductPriceRequestListener {

    @Autowired
    private ProductService productService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void handleMessage(ProductPriceRequest request) {
        log.info("Handling ProductPriceRequest for productId: " + request.getProductId());
        int price = productService.getProductPrice(request.getProductId());
        ProductPriceResponse response = new ProductPriceResponse();
        response.setProductId(request.getProductId());
        response.setPrice(price);
        log.info("Sending ProductPriceResponse for productId: " + request.getProductId() + ", price: " + price);
        rabbitTemplate.convertAndSend("product-price-response-queue", response);
    }
}
