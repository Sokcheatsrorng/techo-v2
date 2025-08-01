package co.istad.techco.techco.features.address;

import co.istad.techco.techco.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
