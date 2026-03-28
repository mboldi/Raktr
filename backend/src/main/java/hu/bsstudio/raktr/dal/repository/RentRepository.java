package hu.bsstudio.raktr.dal.repository;

import hu.bsstudio.raktr.dal.entity.Rent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentRepository extends JpaRepository<Rent, Long> {

    List<Rent> findAllByDeleted(boolean deleted);

    List<Rent> findAllByRentItemsScannableId(Long scannableId);

}
