package sparta.orderservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.client.ProductServiceClient;
import sparta.orderservice.dto.CreateOrderDto;

@Service
public class ProductUpdateListener {

    @Autowired
    private ProductServiceClient productServiceClient;

    public void receiveMessage(CreateOrderDto orderItem) {
        System.out.println("Updating stock for product: " + orderItem.getProductId());
        System.out.println("ProductUpdateListener 실행!! " );
        productServiceClient.updateStock(orderItem);
    }

    public void receiveMessage2(CreateOrderDto orderItem) {
        System.out.println("Updating stock for product: " + orderItem.getProductId());
        System.out.println("receiveMessage22 실행!! " );
        productServiceClient.updateStock(orderItem);
    }
}
