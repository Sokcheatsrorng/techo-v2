package co.istad.techco.techco.features.product.dto;

import lombok.Builder;

@Builder
public record ComputerSpecResponse(
        String processor,
        String ram,
        String storage,
        String gpu,
        String os,
        String screenSize,
        String battery
) { }
