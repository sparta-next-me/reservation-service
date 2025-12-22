package org.nextme.reservation_service.reservation.infrastructure;

import org.nextme.reservation_service.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findByPaymentKey(String paymentKey);

    Optional<Reservation> findByUserId(UUID userId);
    
    Optional<Reservation> findBySagaId(String sagaId);

    List<Reservation> findAllByUserId(UUID userId);

    List<Reservation> findAllByAdvisorId(UUID advisorId);

    List<Reservation> findByProductIdAndReservationDateAndStatusIn(
            UUID productId,
            LocalDate reservationDate,
            List<String> statuses
    );
}
