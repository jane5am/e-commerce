package sparta.orderservice.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.orderservice.domain.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
}
