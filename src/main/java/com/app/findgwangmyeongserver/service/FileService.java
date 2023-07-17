package com.app.findgwangmyeongserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    public ResponseEntity<Resource> getDataFile(
            String fileName,
            String fileInfo
    ) {
        if ("".equals(fileInfo))
            return ResponseEntity.ok().body(null);

        //파일 경로 설정
        String uploadFile = "src/main/resources/files/" + fileName;
        HttpHeaders headers = new HttpHeaders();
        File file = null;
        InputStreamResource resource = null;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(uploadFile, false))) {
            writer.write(fileInfo);

            ClassPathResource classPathResource = new ClassPathResource("files/" + fileName);
            file = classPathResource.getFile();

            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            resource = new InputStreamResource(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
