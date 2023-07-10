package com.junha.securemarshmallow.repository;

import com.junha.securemarshmallow.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileDataRepository extends JpaRepository<FileData, UUID> {
}