package sparta.productservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sparta.productservice.listener.ProductPriceRequestListener;
import sparta.productservice.listener.ProductUpdateListener;

@Configuration
public class RabbitMQConfig {

    public static final String PRODUCT_PRICE_REQUEST_QUEUE = "product-price-request-queue";
    public static final String PRODUCT_PRICE_RESPONSE_QUEUE = "product-price-response-queue";
    public static final String PRODUCT_UPDATE_QUEUE = "product-update-queue";

    @Bean
    public Queue productPriceRequestQueue() {
        return new Queue(PRODUCT_PRICE_REQUEST_QUEUE, true); // durable=true
    }

    @Bean
    public Queue productPriceResponseQueue() {
        return new Queue(PRODUCT_PRICE_RESPONSE_QUEUE, true); // durable=true
    }

    @Bean
    public Queue productUpdateQueue() {
        return new Queue(PRODUCT_UPDATE_QUEUE, true); // durable=true
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer productPriceRequestContainer(ConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter productPriceRequestListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PRODUCT_PRICE_REQUEST_QUEUE);
        container.setMessageListener(productPriceRequestListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter productPriceRequestListenerAdapter(ProductPriceRequestListener listener) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener, "handleMessage");
        adapter.setMessageConverter(jsonMessageConverter());
        return adapter;
    }

    @Bean
    public SimpleMessageListenerContainer productUpdateContainer(ConnectionFactory connectionFactory,
                                                                 MessageListenerAdapter productUpdateListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PRODUCT_UPDATE_QUEUE);
        container.setMessageListener(productUpdateListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter productUpdateListenerAdapter(ProductUpdateListener listener) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener, "receiveMessage");
        adapter.setMessageConverter(jsonMessageConverter());
        return adapter;
    }
}
