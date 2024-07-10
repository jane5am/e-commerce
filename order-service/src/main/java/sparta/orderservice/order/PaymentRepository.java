package sparta.orderservice.order;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.orderservice.domain.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByOrderId(int orderId);

}