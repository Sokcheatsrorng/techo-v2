package co.istad.techco.techco.features.stock;

import co.istad.techco.techco.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

    // if quantityDelta = 0 -> availability = false
    @Modifying
    @Query("UPDATE Stock s SET s.availability = false WHERE s.quantityDelta = 0")
    void markUnavailableStocks();

}
