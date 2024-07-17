package sparta.orderservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sparta.orderservice.client.ProductServiceClient;
import sparta.orderservice.listener.OrderMessageListener;
import sparta.orderservice.listener.ProductUpdateListener;

@Configuration
public class RabbitMQConfig {

    // 큐 이름을 상수로 정의
    public static final String ORDER_QUEUE = "order-queue";

    public static final String PRODUCT_UPDATE_QUEUE = "product-update-queue";

    public static final String PRODUCT_PRICE_REQUEST_QUEUE = "product-price-request-queue";
    public static final String PRODUCT_PRICE_RESPONSE_QUEUE = "product-price-response-queue";

    // 큐를 생성하는 빈 정의
    @Bean
    public Queue queue() {
        return new Queue(ORDER_QUEUE, false); // 두 번째 매개변수는 durable 옵션으로, false이면 메시지가 메모리에만 저장됨
    }

    @Bean
    public Queue productUpdateQueue() {
        System.out.println("2222222222222");
        return new Queue(PRODUCT_UPDATE_QUEUE, false);
    }

//    @Bean
//    public Queue productPriceRequestQueue() {
//        return new Queue(PRODUCT_PRICE_REQUEST_QUEUE, false);
//    }
//
//    @Bean
//    public Queue productPriceResponseQueue() {
//        return new Queue(PRODUCT_PRICE_RESPONSE_QUEUE, false);
//    }

    // RabbitTemplate 빈 정의
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        System.out.println("3333333333333");
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter()); // 메시지 컨버터를 JSON으로 설정
        return rabbitTemplate;
    }

    // Jackson2JsonMessageConverter 빈 정의
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        System.out.println("444444444444");
        return new Jackson2JsonMessageConverter(); // 메시지를 JSON 형식으로 변환하는 컨버터
    }

    // 메시지 리스너 컨테이너 빈 정의
    @Bean
    public SimpleMessageListenerContainer orderContainer(ConnectionFactory connectionFactory,
                                                         @Qualifier("listenerAdapter") MessageListenerAdapter orderListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(ORDER_QUEUE);
        container.setMessageListener(orderListenerAdapter);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer productUpdateContainer(ConnectionFactory connectionFactory,
                                                                 @Qualifier("productUpdateListenerAdapter") MessageListenerAdapter productUpdateListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(PRODUCT_UPDATE_QUEUE);
        container.setMessageListener(productUpdateListenerAdapter);
        return container;
    }

//    @Bean
//    public SimpleMessageListenerContainer productPriceRequestContainer(ConnectionFactory connectionFactory,
//                                                                       @Qualifier("productPriceRequestListenerAdapter") MessageListenerAdapter productPriceRequestListenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(PRODUCT_PRICE_REQUEST_QUEUE);
//        container.setMessageListener(productPriceRequestListenerAdapter);
//        return container;
//    }

    // MessageListenerAdapter 빈 정의
    @Bean
    public MessageListenerAdapter listenerAdapter(OrderMessageListener receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage"); // OrderMessageListener의 receiveMessage 메서드를 호출하도록 설정
        adapter.setMessageConverter(jsonMessageConverter()); // 메시지 컨버터를 JSON으로 설정
        return adapter;
    }

    @Bean
    public MessageListenerAdapter productUpdateListenerAdapter(ProductUpdateListener productUpdateListener) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(productUpdateListener, "receiveMessage");
        adapter.setMessageConverter(jsonMessageConverter()); // 메시지 컨버터를 JSON으로 설정
        return adapter;
    }

    @Bean
    public MessageListenerAdapter getProductPriceListenerAdapter(ProductUpdateListener productUpdateListener) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(productUpdateListener, "receiveMessage2");
        adapter.setMessageConverter(jsonMessageConverter()); // 메시지 컨버터를 JSON으로 설정
        return adapter;
    }

}
