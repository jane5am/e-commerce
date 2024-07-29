package sparta.orderservice.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sparta.common.messages.ProductPriceResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Log4j2
public class ProductPriceResponseListener {

    private int price;
    private boolean isPriceAvailable;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    @RabbitListener(queues = "product-price-response-queue")
    public void onMessage(ProductPriceResponse response) {
        lock.lock();
        try {
            this.price = response.getPrice();
            this.isPriceAvailable = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getPrice(long timeout) throws InterruptedException, TimeoutException {
        lock.lock();
        try {
            if (!isPriceAvailable) {
                boolean isSignaled = condition.await(timeout, TimeUnit.MILLISECONDS);
                if (!isSignaled) {
                    log.error("Timeout while waiting for product price response");
                    throw new TimeoutException("Timeout while waiting for product price response");
                }
            }
            return price;
        } finally {
            isPriceAvailable = false; // 상태 리셋
            lock.unlock();
        }
    }
}
