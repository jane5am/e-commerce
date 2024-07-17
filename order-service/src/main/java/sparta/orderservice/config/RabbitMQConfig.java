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

@Configuration
public class RabbitMQConfig {

    // 큐 이름을 상수로 정의
    public static final String ORDER_QUEUE = "order-queue";

    // 큐를 생성하는 빈 정의
    @Bean
    public Queue queue() {
        return new Queue(ORDER_QUEUE, false); // 두 번째 매개변수는 durable 옵션으로, false이면 메시지가 메모리에만 저장됨
    }

    // RabbitTemplate 빈 정의
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter()); // 메시지 컨버터를 JSON으로 설정
        return rabbitTemplate;
    }

    // Jackson2JsonMessageConverter 빈 정의
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(); // 메시지를 JSON 형식으로 변환하는 컨버터
    }

    // 메시지 리스너 컨테이너 빈 정의
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory); // 연결 팩토리 설정
        container.setQueueNames(ORDER_QUEUE); // 리스닝할 큐 설정
        container.setMessageListener(listenerAdapter); // 메시지 리스너 설정
        return container;
    }

    // MessageListenerAdapter 빈 정의
    @Bean
    public MessageListenerAdapter listenerAdapter(OrderMessageListener receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage"); // OrderMessageListener의 receiveMessage 메서드를 호출하도록 설정
        adapter.setMessageConverter(jsonMessageConverter()); // 메시지 컨버터를 JSON으로 설정
        return adapter;
    }
}
