package co.istad.techco.techco.features.supplier;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Address;
import co.istad.techco.techco.domain.Supplier;
import co.istad.techco.techco.features.address.AddressRepository;
import co.istad.techco.techco.features.supplier.dto.SupplierRequest;
import co.istad.techco.techco.features.supplier.dto.SupplierResponse;
import co.istad.techco.techco.mapper.AddressMapper;
import co.istad.techco.techco.mapper.SupplierMapper;
import co.istad.techco.techco.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void createSupplier(SupplierRequest request) {

        Address address = addressMapper.fromAddressRequest(request.address());
        address.setUuid(Utils.generateUuid());

        if (supplierRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Supplier with name " + request.name() + " already exists"
            );
        }

        Supplier supplier = supplierMapper.fromSupplierRequest(request);
        supplier.setUuid(Utils.generateUuid());

        // 🔹 Set bidirectional relationship
        supplier.setAddress(address);
        address.setSupplier(supplier);

        // 🔹 Save supplier (cascade will handle address)
        supplierRepository.save(supplier);
        addressRepository.save(address);
    }

    @Override
    public Page<SupplierResponse> getAllSuppliers(int page, int size, String name) {

        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page cannot be negative");
        }

        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size cannot be negative");
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Supplier> suppliers;

        if (name != null) {
            suppliers = supplierRepository.findByNameContainingIgnoreCase(name, pageRequest);
        } else {
            suppliers = supplierRepository.findAll(pageRequest);
        }

        return suppliers.map(supplierMapper::tpSupplierResponse);
    }

    @Override
    public SupplierResponse getByUuid(String uuid) {
        return supplierMapper.tpSupplierResponse(
                supplierRepository.findByUuid(uuid)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier with uuid " + uuid)
                        )
        );
    }

    @Override
    public BasedMessage deleteByUuid(String uuid) {

        Supplier supplier = supplierRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier with uuid " + uuid)
                );

        supplierRepository.delete(supplier);

        return new BasedMessage("Supplier has been deleted");

    }

    @Override
    public BasedMessage updateByUuid(String uuid, SupplierRequest request) {

        Supplier supplier = supplierRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Supplier with uuid " + uuid
                        )
                );

        Address address = addressMapper.fromAddressRequest(request.address());

        supplier.setAddress(address);
        supplier.setName(request.name());
        supplier.setEmail(request.email());
        supplier.setPhone(request.phone());
        supplierRepository.save(supplier);

        return new BasedMessage("Supplier has been updated");
    }
}
