package com.app.findgwangmyeongserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    final static String UPLOAD_FILE_DIR = System.getProperty("user.home") + "/fgm";

    public ResponseEntity<Resource> getDataFile(
            String fileName,
            String fileInfo
    ) {
        if ("".equals(fileInfo))
            return ResponseEntity.ok().body(null);

        String uploadFile = UPLOAD_FILE_DIR + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(uploadFile, false))) {
            writer.write(fileInfo);

            Path filePath = Path.of(UPLOAD_FILE_DIR, fileName);

            if (Files.exists(filePath)) {
                Resource fileResource = new FileSystemResource(filePath.toFile());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(fileResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(null);
    }

}
