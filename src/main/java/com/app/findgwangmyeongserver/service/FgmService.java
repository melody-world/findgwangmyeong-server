package com.app.findgwangmyeongserver.service;

import com.app.findgwangmyeongserver.dto.ResponseDTO;
import com.app.findgwangmyeongserver.dto.TradeDTO;
import com.app.findgwangmyeongserver.dto.inter.Apart;
import com.app.findgwangmyeongserver.dto.inter.ApartCode;
import com.app.findgwangmyeongserver.entity.*;
import com.app.findgwangmyeongserver.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FgmService {

    @Value("${openapi.url}")
    private String API_URL;

    @Value("${openapi.rent.url}")
    private String API_RENT_URL;

    @Value("${openapi.apart.list.url}")
    private String API_APART_LIST_URL;

    @Value("${openapi.apart.info.url}")
    private String API_APART_INFO_URL;

    @Value("${naver.geocode.url}")
    private String NAVER_GEOCODE_URL;

    @Value("${naver.geocode.key.id}")
    private String NAVER_GEOCODE_KEY_ID;

    @Value("${naver.geocode.key}")
    private String NAVER_GEOCODE_KEY;

    @Value("${openapi.service.key}")
    private String SERVICE_KEY;

    private final TradeRepository tradeRepository;
    private final TradeRentRepository tradeRentRepository;
    private final ApartRepository apartRepository;
    private final ApartCodeRepository apartCodeRepository;
    private final GeomColorRepository geomColorRepository;
    private final LawdMasterRepository lawdMasterRepository;
    private final LawdRepository lawdRepository;

    private static String nullToStr(Object str, String strDefault) {
        if (str == null || str == "null" || "null".equals(str.toString()) || "undefined".equals(str.toString()) || str.toString().length() == 0) {
            return strDefault;
        } else {
            return str.toString();
        }
    }

    private ResponseDTO callOpenApi(
            String type,
            String lawdCd,
            int pageNo,
            int numOfRows,
            String dealYmd
    ) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("deal".equals(type) ? API_URL : API_RENT_URL);
        sb.append("?serviceKey=" + SERVICE_KEY);
        sb.append("&LAWD_CD=" + lawdCd);
        sb.append("&DEAL_YMD=" + dealYmd);

        // 페이지 번호는 매매 자료에서만 존재한다.
        if ("deal".equals(type)) {
            if (pageNo != 0) sb.append("&pageNo=" + pageNo);
            if (numOfRows != 0) sb.append("&numOfRows=" + numOfRows);
        }

        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type","application/xml");
        conn.setRequestMethod("GET");
        conn.connect();

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(conn.getInputStream());

        Element root = document.getRootElement();
        Element header = root.getChild("header");
        Element body = null;
        String message = "";

        if (header.getContent(0) != null && "00".equals(header.getContent(0).getValue())) {
            message = "OK";
            body = root.getChild("body");
        }

        return ResponseDTO.builder()
                .message(message)
                .header(header)
                .body(body).build();
    }

    @Transactional
    public int saveLatestTradeData(
            String lawdCd,
            String type,
            String year,
            String month
    ) throws Exception {
        int result = 0;
        String deelYmd = year + month;

        ResponseDTO checkResponse = callOpenApi(type, lawdCd,0, 0, deelYmd);

        //1) 최신 데이터 개수 체크
        if ("OK".equals(checkResponse.getMessage())) {
            Element body = checkResponse.getBody();

            //총 매매 거래내역 수(최신)
            int totalCount = Integer.parseInt(body.getChild("totalCount").getContent(0).getValue());

            if (totalCount > 0) {
                //현재까지 기록된 거래내역 수
                long count = "deal".equals(type) ? tradeRepository.countByLawdCdAndYearAndMonth(lawdCd, year, month) :
                                                    tradeRentRepository.countByLawdCdAndYearAndMonth(lawdCd, year, month);
                int currentCount = Optional.of(count).orElse(0L).intValue();

                //2) 데이터 변경이 있는 경우 저장
                if (totalCount != currentCount) {
                    ResponseDTO response = callOpenApi(type, lawdCd, 1, totalCount, deelYmd);

                    if ("OK".equals(response.getMessage())) {
                        body = response.getBody();
                        Element items = body.getChild("items");
                        List<Element> itemList = items.getChildren("item");

                        if ("deal".equals(type)) {
                            tradeRepository.deleteByLawdCdAndYearAndMonth(lawdCd, year, month);

                            saveTradeDeal(itemList);
                        } else {
                            tradeRentRepository.deleteByLawdCdAndYearAndMonth(lawdCd, year, month);

                            saveTradeRent(itemList);
                        }
                    }

                    result++;
                }
            }
        }

        return result;
    }

    private void saveTradeDeal(List<Element> itemList) {
        for (Element item : itemList) {
            List<Element> tradeInfoList = item.getChildren();
            TradeDTO tradeDTO = new TradeDTO();

            for (Element info : tradeInfoList) {
                String value = nullToStr(info.getContent(0).getValue(), "").trim();

                switch (info.getName()) {
                    case "년":
                        tradeDTO.setYear(value);
                        break;
                    case "월":
                        tradeDTO.setMonth(String.format("%02d", Integer.parseInt(value)));
                        break;
                    case "일":
                        tradeDTO.setDay(String.format("%02d", Integer.parseInt(value)));
                        break;
                    case "아파트":
                        tradeDTO.setApartName(value);
                        break;
                    case "전용면적":
                        tradeDTO.setApartArea(value);
                        break;
                    case "층":
                        tradeDTO.setApartFloor(Integer.parseInt(value));
                        break;
                    case "거래금액":
                        tradeDTO.setTradeMoney("".equals(value) ? 0 : Integer.parseInt(value.replaceAll(",", "")));
                        break;
                    case "거래유형":
                        tradeDTO.setTradeType(value);
                        break;
                    case "지역코드":
                        tradeDTO.setLawdCd(value);
                        break;
                    case "법정동":
                        tradeDTO.setApartDong(value);
                        break;
                    case "도로명":
                        tradeDTO.setApartStreet(value);
                        break;
                    case "지번":
                        tradeDTO.setAddress(value);
                        break;
                    case "등기일자":
                        tradeDTO.setConfirmYmd(value);
                        break;
                }
            }

            log.info("아파트 매매 거래자료 {}", tradeDTO);
            ModelMapper modelMapper = new ModelMapper();
            TradeEntity tradeEntity = modelMapper.map(tradeDTO, TradeEntity.class);

            tradeRepository.save(tradeEntity);
        }
    }

    private void saveTradeRent(List<Element> itemList) {
        for (Element item : itemList) {
            List<Element> tradeInfoList = item.getChildren();
            TradeDTO tradeDTO = new TradeDTO();

            for (Element info : tradeInfoList) {
                String value = nullToStr(info.getContent(0).getValue(), "").trim();

                switch (info.getName()) {
                    case "년":
                        tradeDTO.setYear(value);
                        break;
                    case "월":
                        tradeDTO.setMonth(String.format("%02d", Integer.parseInt(value)));
                        break;
                    case "일":
                        tradeDTO.setDay(String.format("%02d", Integer.parseInt(value)));
                        break;
                    case "아파트":
                        tradeDTO.setApartName(value);
                        break;
                    case "전용면적":
                        tradeDTO.setApartArea(value);
                        break;
                    case "층":
                        tradeDTO.setApartFloor("".equals(value) ? 0 : Integer.parseInt(value));
                        break;
                    case "보증금액":
                        tradeDTO.setTradeMoney("".equals(value) ? 0 : Integer.parseInt(value.replaceAll(",", "")));
                        break;
                    case "월세금액":
                        tradeDTO.setRentMoney("".equals(value) ? 0 : Integer.parseInt(value.replaceAll(",", "")));
                        break;
                    case "종전계약보증금":
                        tradeDTO.setBfTradeMoney("".equals(value) ? 0 : Integer.parseInt(value.replaceAll(",", "")));
                        break;
                    case "종전계약월세":
                        tradeDTO.setBfRentMoney("".equals(value) ? 0 : Integer.parseInt(value.replaceAll(",", "")));
                        break;
                    case "계약구분":
                        tradeDTO.setTradeType(value);
                        break;
                    case "계약기간":
                        tradeDTO.setRentDate(value);
                        break;
                    case "지역코드":
                        tradeDTO.setLawdCd(value);
                        break;
                    case "법정동":
                        tradeDTO.setApartDong(value);
                        break;
                    case "지번":
                        tradeDTO.setAddress(value);
                        break;
                }
            }

            log.info("아파트 전/월세 거래자료 {}", tradeDTO);
            ModelMapper modelMapper = new ModelMapper();
            TradeRentEntity tradeRentEntity = modelMapper.map(tradeDTO, TradeRentEntity.class);

            tradeRentRepository.save(tradeRentEntity);
        }
    }

    public String masterList() {
        List<LawdMasterEntity> masterList = lawdMasterRepository.findAll();

        JSONObject obj = new JSONObject();
        JSONArray lawdArray = new JSONArray();

        for (LawdMasterEntity lawdEntity : masterList) {
            JSONObject lawdObj = new JSONObject();

            lawdObj.put("masterCd", lawdEntity.getMasterCd());
            lawdObj.put("lawdNm"  , lawdEntity.getLawdNm());

            lawdArray.add(lawdObj);
        }

        obj.put("data", lawdArray);

        return obj.toString();
    }

    public String lawdList() {
        List<LawdEntity> lawdList = lawdRepository.findAll();
        List<GeomColorEntity> geomList = geomColorRepository.findAll();

        JSONObject obj = new JSONObject();
        JSONArray lawdArray = new JSONArray();
        JSONArray geomArray = new JSONArray();

        for (LawdEntity lawdEntity : lawdList) {
            JSONObject lawdObj = new JSONObject();

            lawdObj.put("lawdCd"  , lawdEntity.getLawdCd());
            lawdObj.put("lawdNm"  , lawdEntity.getLawdNm());
            lawdObj.put("masterCd", lawdEntity.getMasterCd());
            lawdObj.put("activeYn", lawdEntity.getActiveYn());

            lawdArray.add(lawdObj);
        }

        for (GeomColorEntity geomColorEntity : geomList) {
            JSONObject lawdObj = new JSONObject();

            lawdObj.put("lineNm"    , geomColorEntity.getLineNm());
            lawdObj.put("colorValue", geomColorEntity.getColorValue());

            geomArray.add(lawdObj);
        }

        obj.put("data"    , lawdArray);
        obj.put("geomList", geomArray);

        return obj.toString();
    }

    public String getSubwayList() {
        List<GeomColorEntity> geomList = geomColorRepository.findAll();

        JSONObject obj = new JSONObject();
        JSONArray geomArray = new JSONArray();

        for (GeomColorEntity geomColorEntity : geomList) {
            JSONObject lawdObj = new JSONObject();

            lawdObj.put("lineNm"    , geomColorEntity.getLineNm());
            lawdObj.put("colorValue", geomColorEntity.getColorValue());

            geomArray.add(lawdObj);
        }

        obj.put("data", geomArray);

        return obj.toString();
    }

    @Transactional
    public void saveApart(String lawdCd) throws Exception {
        List<Apart> apartList = tradeRepository.findByLawdCd(lawdCd);

        for (Apart apart : apartList) {
            apartRepository.save(ApartEntity.builder()
                    .apartName(apart.getApartName())
                    .apartDong(apart.getApartDong())
                    .address(apart.getAddress())
                    .lawdCd(apart.getLawdCd())
                    .build());
        }
    }

    @Transactional
    public void saveApartConv(String lawdCd) throws Exception {
        List<ApartEntity> apartList = apartRepository.findByLawdCd(lawdCd);
        apartList = apartList.stream()
                        .filter(a -> a.getConvX() == 0 || a.getConvY() ==0)
                        .collect(Collectors.toList());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", NAVER_GEOCODE_KEY_ID);
        headers.set("X-NCP-APIGW-API-KEY", NAVER_GEOCODE_KEY);

        HttpEntity request = new HttpEntity(headers);

        for (ApartEntity apartEntity : apartList) {
            String address = apartEntity.getApartDong() + " " + apartEntity.getAddress();
            ResponseEntity<String> response = restTemplate.exchange(
                    NAVER_GEOCODE_URL + "?query=" + address + "&count=1",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
                JSONArray jsonArray = (JSONArray) jsonObject.get("addresses");

                for (Object obj : jsonArray) {
                    JSONObject object = (JSONObject) obj;

                    apartEntity.setConvX(Double.parseDouble(String.valueOf(object.get("x"))));
                    apartEntity.setConvY(Double.parseDouble(String.valueOf(object.get("y"))));
                }

                apartRepository.save(apartEntity);

                log.info(apartEntity.toString());
            }
        }
    }

    private ResponseDTO callApartListApi(String lawdCd) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(API_APART_LIST_URL);
        sb.append("?serviceKey=" + SERVICE_KEY);
        sb.append("&sigunguCode=" + lawdCd);
        sb.append("&pageNo=1");
        sb.append("&numOfRows=100");

        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type","application/xml");
        conn.setRequestMethod("GET");
        conn.connect();

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(conn.getInputStream());

        Element root = document.getRootElement();
        Element header = root.getChild("header");
        Element body = null;
        String message = "";

        if (header.getContent(0) != null && "00".equals(header.getContent(0).getValue())) {
            message = "OK";
            body = root.getChild("body");
        }

        return ResponseDTO.builder()
                .message(message)
                .header(header)
                .body(body).build();
    }

    @Transactional
    public void saveApartCode(String lawdCd) throws Exception {
         ResponseDTO responseDTO = callApartListApi(lawdCd);

        if ("OK".equals(responseDTO.getMessage())) {
            Element body = responseDTO.getBody();
            Element items = body.getChild("items");

            int totalCount = Integer.parseInt(body.getChild("totalCount").getContent(0).getValue());

            if (totalCount > 0) {
                apartCodeRepository.deleteByLawdCd(lawdCd);

                List<Element> itemList = items.getChildren("item");

                for (Element item : itemList) {
                    List<Element> tradeInfoList = item.getChildren();
                    ApartCodeEntity apartCodeEntity = new ApartCodeEntity();
                    apartCodeEntity.setLawdCd(lawdCd);

                    for (Element info : tradeInfoList) {
                        String value = nullToStr(info.getContent(0).getValue(), "").trim();

                        switch (info.getName()) {
                            case "kaptCode":
                                apartCodeEntity.setApartCode(value);
                                break;
                            case "kaptName":
                                apartCodeEntity.setApartName(value);
                                break;
                        }
                    }

                    apartCodeRepository.save(apartCodeEntity);
                }
            }
        }
    }

    private ResponseDTO callApartInfoApi(String apartCode) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(API_APART_INFO_URL);
        sb.append("?serviceKey=" + SERVICE_KEY);
        sb.append("&kaptCode=" + apartCode);

        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type","application/xml");
        conn.setRequestMethod("GET");
        conn.connect();

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(conn.getInputStream());

        Element root = document.getRootElement();
        Element header = root.getChild("header");
        Element body = null;
        String message = "";

        if (header.getContent(0) != null && "00".equals(header.getContent(0).getValue())) {
            message = "OK";
            body = root.getChild("body");
        }

        return ResponseDTO.builder()
                .message(message)
                .header(header)
                .body(body).build();
    }

    @Transactional
    public void saveApartAddress(String lawdCd) throws Exception {
        List<ApartCodeEntity> apartList = apartCodeRepository.findByLawdCd(lawdCd);

        for (ApartCodeEntity apartEntity : apartList) {
            log.info(apartEntity.toString());

            ResponseDTO responseDTO = callApartInfoApi(apartEntity.getApartCode());

            if ("OK".equals(responseDTO.getMessage())) {
                Element body = responseDTO.getBody();
                Element item = body.getChild("item");
                List<Element> itemList = item.getChildren();

                for (Element element : itemList) {
                    String value = nullToStr(element.getContent(0).getValue(), "").trim();

                    if ("kaptAddr".equals(element.getName())) {
                        apartEntity.setAddress(value);
                    }
                }

                apartCodeRepository.save(apartEntity);
            }
        }
    }

    @Transactional
    public void saveApartCompare1(String lawdCd) throws Exception {
        /**
         * 아파트 리스트와 거래 내역의 아파트들을 그룹화해 주소를 대조한다.
         */
        List<ApartCode> apartList = tradeRepository.findByLawdCd2(lawdCd);

        for (ApartCode apartCode : apartList) {
            ApartEntity apartEntity = apartRepository.findBySeq(apartCode.getSeq());
            apartEntity.setApartCode(apartCode.getApartCode());

            apartRepository.save(apartEntity);
        }
    }

    @Transactional
    public void saveApartCompare2(String lawdCd) throws Exception {
        /**
         * 주소를 포함한 아파트를 조회했을 때 아파트코드가 없는 경우
         * 아파트명으로 다시 비교해본다
         */
        List<ApartCode> apartList2 = tradeRepository.findByLawdCd3(lawdCd);

        for (ApartCode apartCode : apartList2) {
            ApartEntity apartEntity = apartRepository.findBySeq(apartCode.getSeq());
            apartEntity.setApartCode(apartCode.getApartCode());

            apartRepository.save(apartEntity);
        }
    }

    public String getApartList(String lawdCd) {
        List<ApartEntity> apartList = apartRepository.findByLawdCd(lawdCd);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        for (ApartEntity apartEntity : apartList) {
            JSONObject obj = new JSONObject();

            obj.put("seq"         , apartEntity.getSeq());
            obj.put("apartName"   , apartEntity.getApartName());
            obj.put("apartDong"   , apartEntity.getApartDong());
            obj.put("address"     , apartEntity.getAddress());
            obj.put("convX"       , apartEntity.getConvX());
            obj.put("convY"       , apartEntity.getConvY());
            obj.put("lawdCd"      , apartEntity.getLawdCd());
            obj.put("apartCode"   , apartEntity.getApartCode());

            jsonArray.add(obj);
        }

        jsonObject.put("data", jsonArray);

        return jsonObject.toString();
    }

    @Transactional
    public void saveApartList(
            String masterCd,
            List<String> dataList
    ) throws Exception {
        JSONParser jsonParser = new JSONParser();

        if (!CollectionUtils.isEmpty(dataList)) {
            apartRepository.deleteByMasterCd(masterCd);

            for (String data : dataList) {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(data);

                for (Object obj : jsonArray) {
                    JSONObject jsonData = (JSONObject) obj;
                    ApartEntity apartEntity = ApartEntity.builder()
                            .seq(Integer.parseInt((String) jsonData.get("seq")))
                            .apartName(String.valueOf(jsonData.get("apartName")))
                            .apartDong(String.valueOf(jsonData.get("apartDong")))
                            .address(String.valueOf(jsonData.get("address")))
                            .convX(Double.parseDouble(String.valueOf(jsonData.get("convX"))))
                            .convY(Double.parseDouble(String.valueOf(jsonData.get("convY"))))
                            .lawdCd(String.valueOf(jsonData.get("lawdCd")))
                            .apartCode(jsonData.get("apartCode") == null ? "" : String.valueOf(jsonData.get("apartCode")))
                            .masterCd(masterCd).build();

                    apartRepository.save(apartEntity);
                }
            }
        }
    }

}
