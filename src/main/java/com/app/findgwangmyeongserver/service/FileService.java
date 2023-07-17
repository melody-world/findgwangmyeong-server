package com.app.findgwangmyeongserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final static String FILE_PATH = "src/main/resources/files";

    public boolean createFile(
            String fileName,
            String fileInfo
    ) {
        boolean isCreate = false;

        try {
            //파일 내용이 존재하는 경우에만 저장
            if (!"".equals(fileInfo)) {
                //파일 경로 설정
                String uploadFile = FILE_PATH + "/" + fileName;

                //파일 생성 및 내용 작성
                BufferedWriter writer = new BufferedWriter(new FileWriter(uploadFile));
                writer.write(fileInfo);
                writer.close();

                isCreate = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isCreate;
    }

    public ResponseEntity<Resource> downloadFile(
            String fileName
    ) {
        ClassPathResource resource = new ClassPathResource("files/" + fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

}
