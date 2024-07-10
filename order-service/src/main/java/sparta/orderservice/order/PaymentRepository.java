package sparta.orderservice.order;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.orderservice.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
