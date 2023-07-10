package com.junha.securemarshmallow.controller;

import com.junha.securemarshmallow.domain.FileDto;
import com.junha.securemarshmallow.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getFile(@PathVariable String id, @RequestParam String UserId) {
        var uuid = UUID.fromString(id);
        System.out.println(uuid);
        return fileService.findFile(uuid,UserId);
    }

    @PostMapping("/upload")
    public FileDto upload(@RequestPart MultipartFile file, String UserId, String Hashtag) {
        return fileService.uploadFile(file,UserId,Hashtag).toDto();
    }

    @PostMapping("/hashtag")
    public List<FileDto> getHashtagFile(@RequestPart String UserId,String Hashtag) {
        return fileService.findFilesByHashtag(UserId, Hashtag);
    }

    @GetMapping
    public List<FileDto> allFiles() {
        return fileService.getUploadedFiles();
    } //올바른 유저 아이디

    @DeleteMapping("/{uuid}")
    public void deleteFile(
            @PathVariable String uuid,
            @RequestParam String UserId
    ){
        fileService.deleteFile(UUID.fromString(uuid), UserId);
    }
}