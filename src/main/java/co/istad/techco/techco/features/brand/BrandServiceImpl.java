package co.istad.techco.techco.features.brand;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Brand;
import co.istad.techco.techco.features.brand.dto.BrandRequest;
import co.istad.techco.techco.features.brand.dto.BrandResponse;
import co.istad.techco.techco.mapper.BrandMapper;
import co.istad.techco.techco.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder;

    @Override
    public void createBrand(BrandRequest request) {

        if (brandRepository.existsByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Brand name already exists");
        }

        Brand brand = brandMapper.fromBrandRequest(request);

        brand.setUuid(Utils.generateUuid());

        brandRepository.save(brand);

    }

    @Override
    public BrandResponse getByUuid(String uuid) {
        return brandMapper.toBrandResponse(
                brandRepository.findByUuid(uuid)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Brand has not been found"
                                )
                        )
        );
    }

    @Override
    public Page<BrandResponse> getAll(int page, int size, String name) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be greater than 0"
            );
        }

        if (size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be greater than 0"
            );
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Brand> brands;

        if (name != null) {
            brands = brandRepository.findByNameContaining(name, pageRequest);
        } else {
            brands = brandRepository.findAll(pageRequest);
        }

        return brands.map(brandMapper::toBrandResponse);
    }

    @Override
    public BasedMessage deleteByUuid(String uuid) {

        Brand brand = brandRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Brand has not been found"
                        )
                );

        brandRepository.delete(brand);

        return new BasedMessage("Brand has been deleted");

    }

    @Override
    public BasedMessage updateByUuid(String uuid, BrandRequest request) {

        Brand brand = brandRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Brand has not been found"
                        )
                );

        brand.setName(request.name());
        brand.setBrandLogo(request.brandLogo());
        brand.setDescription(request.description());
        brandRepository.save(brand);

        return new BasedMessage("Brand has been updated");

    }

}
