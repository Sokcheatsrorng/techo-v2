package co.istad.techco.techco.features.media;

import co.istad.techco.techco.features.media.dto.MediaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medias")
public class MediaController {

    private final MediaService mediaService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping( "/upload-single")
    MediaResponse uploadSingle(@Valid @RequestBody MultipartFile file) {
        return mediaService.uploadSingle(file, "");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value ="/upload-multiple", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    List<MediaResponse> uploadMultiple(@RequestPart("files") List<MultipartFile> files) {

        return mediaService.uploadMultiple(files, "");

    }

}
