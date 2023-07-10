package com.junha.securemarshmallow.repository;

import com.junha.securemarshmallow.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> findByCreatedByAndHashtag(String createdBy, String hashtag);
}
