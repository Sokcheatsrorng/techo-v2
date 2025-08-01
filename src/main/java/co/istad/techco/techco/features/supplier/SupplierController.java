package co.istad.techco.techco.features.supplier;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Supplier;
import co.istad.techco.techco.features.supplier.dto.SupplierRequest;
import co.istad.techco.techco.features.supplier.dto.SupplierResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public void createSupplier(@Valid @RequestBody SupplierRequest request) {
        supplierService.createSupplier(request);
    }

    @GetMapping
    public Page<SupplierResponse> getAllSuppliers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(required = false, defaultValue = "") String name) {

        return supplierService.getAllSuppliers(page, size, name);

    }

    @GetMapping("/{uuid}")
    public SupplierResponse getSupplier(@PathVariable(name = "uuid") String uuid) {

        return supplierService.getByUuid(uuid);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public BasedMessage deleteByUuid(@PathVariable(name = "uuid") String uuid) {

        return supplierService.deleteByUuid(uuid);

    }

    @PutMapping("/{uuid}")
    public BasedMessage updateSupplier(@PathVariable(name = "uuid") String uuid,
                                       @Valid @RequestBody SupplierRequest request) {

        return supplierService.updateByUuid(uuid, request);

    }

}
