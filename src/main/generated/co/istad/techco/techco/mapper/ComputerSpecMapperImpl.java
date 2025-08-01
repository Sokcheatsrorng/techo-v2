package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.ComputerSpec;
import co.istad.techco.techco.features.product.spec.ComputerSpecRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class ComputerSpecMapperImpl implements ComputerSpecMapper {

    @Override
    public ComputerSpec fromComputerSpecRequest(ComputerSpecRequest request) {
        if ( request == null ) {
            return null;
        }

        ComputerSpec computerSpec = new ComputerSpec();

        computerSpec.setProcessor( request.processor() );
        computerSpec.setRam( request.ram() );
        computerSpec.setStorage( request.storage() );
        computerSpec.setGpu( request.gpu() );
        computerSpec.setOs( request.os() );
        computerSpec.setScreenSize( request.screenSize() );
        computerSpec.setBattery( request.battery() );

        return computerSpec;
    }

    @Override
    public ComputerSpecRequest toComputerSpecResponse(ComputerSpec computerSpec) {
        if ( computerSpec == null ) {
            return null;
        }

        String processor = null;
        String ram = null;
        String storage = null;
        String gpu = null;
        String os = null;
        String screenSize = null;
        String battery = null;

        processor = computerSpec.getProcessor();
        ram = computerSpec.getRam();
        storage = computerSpec.getStorage();
        gpu = computerSpec.getGpu();
        os = computerSpec.getOs();
        screenSize = computerSpec.getScreenSize();
        battery = computerSpec.getBattery();

        ComputerSpecRequest computerSpecRequest = new ComputerSpecRequest( processor, ram, storage, gpu, os, screenSize, battery );

        return computerSpecRequest;
    }
}
