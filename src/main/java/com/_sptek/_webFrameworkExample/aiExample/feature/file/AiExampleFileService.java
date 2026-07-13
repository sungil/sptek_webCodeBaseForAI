package com._sptek._webFrameworkExample.aiExample.feature.file;

import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek._webFrameworkExample.aiExample.common.code.AiExampleServiceErrorCode;
import com._sptek._webFrameworkExample.aiExample.common.dto.AiExampleUploadedFileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AiExampleFileService {

    public List<AiExampleUploadedFileDto> validateAndDescribe(List<MultipartFile> multipartFiles) {
        List<MultipartFile> files = multipartFiles == null ? List.of() : multipartFiles;
        long maxTotalSize = 5L * 1024L * 1024L;
        long totalSize = files.stream().mapToLong(MultipartFile::getSize).sum();

        if (totalSize > maxTotalSize) {
            throw new ServiceException(AiExampleServiceErrorCode.PAYLOAD_TOO_LARGE);
        }

        List<AiExampleUploadedFileDto> result = new ArrayList<>();
        for (int index = 0; index < files.size(); index++) {
            MultipartFile file = files.get(index);
            String contentType = Objects.toString(file.getContentType(), "");
            if (!contentType.startsWith("image/")) {
                throw new ServiceException(AiExampleServiceErrorCode.FILE_UPLOAD_DENIED, "Only image files are allowed.");
            }

            AiExampleUploadedFileDto fileDto = new AiExampleUploadedFileDto();
            fileDto.setFileName(file.getOriginalFilename());
            fileDto.setFileOrder(index + 1);
            fileDto.setFilePath("sample/not-persisted");
            result.add(fileDto);
        }
        return result;
    }
}
