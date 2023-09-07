package com.app.findgwangmyeongserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    final static String UPLOAD_FILE_DIR = System.getProperty("user.home");

    public ResponseEntity<Resource> getDataFile(
            String lawdDir,
            String lawdCd,
            String type,
            String fileName,
            String fileInfo
    ) {
        if ("".equals(fileInfo))
            return ResponseEntity.ok().body(null);

        String uploadPath = UPLOAD_FILE_DIR + "/" + lawdDir;

        if (!"".equals(lawdCd)) {
            uploadPath = "".equals(type)
                            ? UPLOAD_FILE_DIR + "/" + lawdCd
                            : UPLOAD_FILE_DIR + "/" + lawdCd + "/" + type;
        }

        String uploadFile = uploadPath + "/" + fileName;

        checkDirectory(uploadPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(uploadFile, false))) {
            writer.write(fileInfo);

            Path filePath = Path.of(uploadPath, fileName);

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

    public void makeDataFile(
            String lawdCd,
            String type,
            List<Map<String, Object>> fileList
    ) throws IOException {
        String uploadPath = UPLOAD_FILE_DIR + "/" + lawdCd + "/" + type;

        if (CollectionUtils.isNotEmpty(fileList)) {
            for (Map<String, Object> file : fileList) {
                String fileName = (String) file.get("fileName");
                String fileInfo = (String) file.get("tradeInfo");

                String uploadFile = uploadPath + "/" + fileName;

                checkDirectory(uploadPath);

                BufferedWriter writer = new BufferedWriter(new FileWriter(uploadFile, false));
                writer.write(fileInfo);
                writer.close();
            }
        }
    }

    private void checkDirectory(String filePath) {
        Path path = Paths.get(filePath);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
