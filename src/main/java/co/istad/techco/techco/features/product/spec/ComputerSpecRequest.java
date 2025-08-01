package co.istad.techco.techco.features.product.spec;


public record ComputerSpecRequest(

        String processor,

        String ram,

        String storage,

        String gpu,

        String os,

        String screenSize,

        String battery

) {
}
