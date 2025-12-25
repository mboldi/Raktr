package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RentRepository extends JpaRepository<Rent, Long> {

    Optional<Rent> findByRentItemsId(Long id);

    @Override
    @Query(value = "SELECT rent FROM Rent rent where rent.isDeleted = false")
    List<Rent> findAll();

    @Query(value = "SELECT rent FROM Rent rent where rent.isDeleted = true")
    List<Rent> findAllDeleted();

}
