package com.app.findgwangmyeongserver.service;

import com.app.findgwangmyeongserver.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FgmService {

    @Value("${openapi.url}")
    private String API_URL;

    @Value("${openapi.service.key}")
    private String SERVICE_KEY;

    private final static String LAWD_CD = "41210"; //광명지역코드

    public void saveData(
            int pageNo,
            int numOfRows,
            String dealYmd
    ) throws Exception {
        String uri = API_URL + "?serviceKey=" + SERVICE_KEY +
                "&pageNo=" + pageNo +
                "&numOfRows=" + numOfRows +
                "&LAWD_CD=" + LAWD_CD +
                "&DEAL_YMD=" + dealYmd;

        RestTemplate restTemplate = new RestTemplate();

        ResponseDTO memberList = restTemplate.getForObject(uri, ResponseDTO.class);


//        JSONParser jsonParser = new JSONParser();


    }


}
