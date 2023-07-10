package com.junha.securemarshmallow.service;

import com.junha.securemarshmallow.domain.FileDto;
import com.junha.securemarshmallow.domain.UserAccount;
import com.junha.securemarshmallow.dto.UserAccountDto;
import com.junha.securemarshmallow.entity.File;
import com.junha.securemarshmallow.entity.FileData;
import com.junha.securemarshmallow.repository.FileDataRepository;
import com.junha.securemarshmallow.repository.FileRepository;
import com.junha.securemarshmallow.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.MediaType;
@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final FileDataRepository fileDataRepository;
    private final FileRepository fileRepository;
    private static final RateLimiter rateLimiter = RateLimiter.create(3);
    public List<FileDto> getUploadedFiles() {
        return fileRepository.findAll().stream().map(File::toDto).toList();
    }


    public File uploadFile(MultipartFile file, String userId, String Hashtag) {
        System.out.println(userId);
        if (!rateLimiter.tryAcquire()) {
            throw new RuntimeException("Too many requests. Please try again later.");
        }
        long maxSizeBytes = 8 * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 8MB.");
        }
        try {
            MediaType mediaType = MediaType.parseMediaType(file.getContentType());
            List<MediaType> allowedMediaTypes = Arrays.asList(
                    MediaType.IMAGE_JPEG,
                    MediaType.IMAGE_PNG,
                    MediaType.IMAGE_GIF,
                    MediaType.parseMediaType("image/webp"),
                    MediaType.parseMediaType("image/heic")
            );
            if (!allowedMediaTypes.contains(mediaType)) {
                throw new IllegalArgumentException("Invalid file content type. Only JPEG, PNG, GIF, WEBP, and HEIC files are allowed.");
            }
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("Invalid file name. File name cannot be empty.");
            }
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "webp", "heic");
            if (!allowedExtensions.contains(fileExtension)) {
                throw new IllegalArgumentException("Invalid file extension. Only JPG, JPEG, PNG, WEBP, and HEIC files are allowed.");
            }

            var entity = new File(UUID.randomUUID(), fileName, file.getSize(), LocalDateTime.now(), false, userId, Hashtag);
            var save = fileRepository.save(entity);
            fileDataRepository.save(new FileData(save.getId(), file.getBytes()));
            return save;
        } catch (IOException e) {
            throw new RuntimeException("Error processing the file.", e);
        }
    }

    public List<FileDto> findFilesByHashtag(String userId, String hashtag) {
        return fileRepository.findByCreatedByAndHashtag(userId, hashtag)
                .stream()
                .map(File::toDto)
                .collect(Collectors.toList());
    }


    public byte[] findFile(UUID uuid, String userId) {
        Optional<File> fileOptional = fileRepository.findById(uuid);
        File file = fileOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The file with the specified ID does not exist"));

        if (!file.getCreatedBy().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "createdBy and userId do not match");
        }

        return fileDataRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The file data with the specified ID does not exist"))
                .getData();
    }



    public void deleteFile(UUID id, String userId) {
        Optional<File> fileOptional = fileRepository.findById(id);
        File file = fileOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The file with the specified ID does not exist"));

        if (!file.getCreatedBy().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "createdBy and userId do not match");
        }

        fileRepository.deleteById(id);
        fileDataRepository.deleteById(id);
    }


}
