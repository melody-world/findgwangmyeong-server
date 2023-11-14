package com.app.findgwangmyeongserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final static String UPLOAD_FILE_DIR = System.getProperty("user.home");

    public ResponseEntity<Resource> getDataFile(
            String filePath,
            String lawdCd,
            String fileName,
            String fileInfo
    ) {
        if ("".equals(fileInfo))
            return ResponseEntity.ok().body(null);

        String uploadPath = UPLOAD_FILE_DIR + "/" + filePath;

        if (!"".equals(lawdCd)) {
            uploadPath = uploadPath + "/" + lawdCd;
        }

        String uploadFile = uploadPath + "/" + fileName;

        checkDirectory(uploadPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(uploadFile, false))) {
            writer.write(fileInfo);

            Path path = Path.of(uploadPath, fileName);

            if (Files.exists(path)) {
                Resource fileResource = new FileSystemResource(path.toFile());

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
            String masterCd,
            List<Map<String, Object>> fileList
    ) throws IOException {
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (Map<String, Object> file : fileList) {
                String lawdCd = (String) file.get("lawdCd");
                String fileInfo = (String) file.get("tradeInfo");
                String uploadPath = UPLOAD_FILE_DIR + "/fgm/" + masterCd + "/" + lawdCd;
                String uploadFile = uploadPath + "/apart-code.json";

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

    public List<String> getApartFileFromJson(String masterCd) throws Exception {
        List<String> resultList = new ArrayList<>();
        File dir = new File(UPLOAD_FILE_DIR + "/fgm/" + masterCd);
		File[] files = dir.listFiles();

        if (files != null && files.length > 0) {
            JSONParser parser = new JSONParser();

            for (File file : files) {
                Reader reader = new FileReader(file.toString() + "/apart-code.json");
                JSONObject jsonObject = (JSONObject) parser.parse(reader);

                String data = jsonObject.get("data").toString();
                resultList.add(data);
            }
        }

        return resultList;
    }

}
