package co.istad.techco.techco.features.media;

import co.istad.techco.techco.features.media.dto.MediaRequest;
import co.istad.techco.techco.features.media.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {

    MediaResponse uploadSingle(MultipartFile multipartFile, String folderName);

    List<MediaResponse> uploadMultiple(List<MultipartFile> multipartFile, String folderName);

}
