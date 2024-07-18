package sparta.orderservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sparta.orderservice.listener.OrderMessageListener;
import sparta.orderservice.listener.ProductPriceResponseListener;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_QUEUE = "order-queue";
    public static final String PRODUCT_UPDATE_QUEUE = "product-update-queue";
    public static final String PRODUCT_PRICE_REQUEST_QUEUE = "product-price-request-queue";
    public static final String PRODUCT_PRICE_RESPONSE_QUEUE = "product-price-response-queue";

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public Queue productUpdateQueue() {
        return new Queue(PRODUCT_UPDATE_QUEUE, true);
    }

    @Bean
    public Queue productPriceRequestQueue() {
        return new Queue(PRODUCT_PRICE_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue productPriceResponseQueue() {
        return new Queue(PRODUCT_PRICE_RESPONSE_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer orderContainer(ConnectionFactory connectionFactory,
                                                         MessageListenerAdapter orderListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(ORDER_QUEUE);
        container.setMessageListener(orderListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter orderListenerAdapter(OrderMessageListener listener, Jackson2JsonMessageConverter converter) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener, "receiveOrderMessage");
        adapter.setMessageConverter(converter);
        return adapter;
    }

    @Bean
    public SimpleMessageListenerContainer productPriceResponseContainer(ConnectionFactory connectionFactory,
                                                                        MessageListenerAdapter productPriceResponseListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PRODUCT_PRICE_RESPONSE_QUEUE);
        container.setMessageListener(productPriceResponseListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter productPriceResponseListenerAdapter(ProductPriceResponseListener listener) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener, "onMessage");
        adapter.setMessageConverter(jsonMessageConverter());
        return adapter;
    }
}
