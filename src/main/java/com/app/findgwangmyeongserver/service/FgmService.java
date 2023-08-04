package com.app.findgwangmyeongserver.service;

import com.app.findgwangmyeongserver.dto.ResponseDTO;
import com.app.findgwangmyeongserver.dto.TradeDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FgmService {

    @Value("${openapi.url}")
    private String API_URL;

    @Value("${openapi.rent.url}")
    private String API_RENT_URL;

    @Value("${openapi.service.key}")
    private String SERVICE_KEY;

    @Value("${openapi.geom.key}")
    private String GEOM_API_KEY;

    @Value("${openapi.geom.url}")
    private String GEOM_API_URL;

    private final TradeRepository tradeRepository;
    private final TradeRentRepository tradeRentRepository;
    private final GeomRepository geomRepository;
    private final GeomColorRepository geomColorRepository;
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

    public String getTradeInfo(
            String lawdCd,
            String type,
            String year,
            String month
    ) {
        return "deal".equals(type) ? getTradeListToString(lawdCd, year, month)
                                    : getTradeRentListToString(lawdCd, year, month);
    }

    private String getTradeListToString(
            String lawdCd,
            String year,
            String month
    ) {
        List<TradeEntity> tradeList = tradeRepository.findByLawdCdAndYearAndMonth(lawdCd, year, month);

        if (CollectionUtils.isEmpty(tradeList)) return "";

        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();

        for (TradeEntity tradeInfo : tradeList) {
            JSONObject tradeObj = new JSONObject();

            tradeObj.put("tradeSeq"   , tradeInfo.getTradeSeq());
            tradeObj.put("year"       , tradeInfo.getYear());
            tradeObj.put("month"      , tradeInfo.getMonth());
            tradeObj.put("day"        , tradeInfo.getDay());
            tradeObj.put("apartName"  , tradeInfo.getApartName());
            tradeObj.put("apartArea"  , tradeInfo.getApartArea());
            tradeObj.put("apartFloor" , tradeInfo.getApartFloor());
            tradeObj.put("tradeMoney" , tradeInfo.getTradeMoney());
            tradeObj.put("tradeType"  , tradeInfo.getTradeType());
            tradeObj.put("lawdCd"     , tradeInfo.getLawdCd());
            tradeObj.put("apartDong"  , tradeInfo.getApartDong());
            tradeObj.put("apartStreet", tradeInfo.getApartStreet());
            tradeObj.put("address"    , tradeInfo.getAddress());
            tradeObj.put("confirmYmd"  , tradeInfo.getConfirmYmd());

            array.add(tradeObj);
        }

        obj.put("tradeYm"   , year + month);
        obj.put("totalCount", tradeList.size());
        obj.put("data"      , array);

        return obj.toString();
    }

    private String getTradeRentListToString(
            String lawdCd,
            String year,
            String month
    ) {
        List<TradeRentEntity> tradeRentList = tradeRentRepository.findByLawdCdAndYearAndMonth(lawdCd, year, month);

        if (CollectionUtils.isEmpty(tradeRentList)) return "";

        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();

        for (TradeRentEntity tradeInfo : tradeRentList) {
            JSONObject tradeObj = new JSONObject();

            tradeObj.put("tradeSeq"    , tradeInfo.getTradeSeq());
            tradeObj.put("year"        , tradeInfo.getYear());
            tradeObj.put("month"       , tradeInfo.getMonth());
            tradeObj.put("day"         , tradeInfo.getDay());
            tradeObj.put("apartName"   , tradeInfo.getApartName());
            tradeObj.put("apartArea"   , tradeInfo.getApartArea());
            tradeObj.put("apartFloor"  , tradeInfo.getApartFloor());
            tradeObj.put("tradeMoney"  , tradeInfo.getTradeMoney());
            tradeObj.put("rentMoney"   , tradeInfo.getRentMoney());
            tradeObj.put("bfTradeMoney", tradeInfo.getBfTradeMoney());
            tradeObj.put("bfRentMoney" , tradeInfo.getBfRentMoney());
            tradeObj.put("tradeType"   , tradeInfo.getTradeType());
            tradeObj.put("rentDate"    , tradeInfo.getRentDate());
            tradeObj.put("lawdCd"      , tradeInfo.getLawdCd());
            tradeObj.put("apartDong"   , tradeInfo.getApartDong());
            tradeObj.put("address"     , tradeInfo.getAddress());

            array.add(tradeObj);
        }

        obj.put("tradeYm"   , year + month);
        obj.put("totalCount", tradeRentList.size());
        obj.put("data"      , array);

        return obj.toString();
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

            //현재까지 기록된 거래내역 수
            long count = "deal".equals(type) ? tradeRepository.countByLawdCdAndYearAndMonth(lawdCd, year, month) :
                                                tradeRentRepository.countByLawdCdAndYearAndMonth(lawdCd, year, month);
            int currentCount = Optional.of(count).orElse(0L).intValue();

            int numOfRows = totalCount - currentCount;

            //2) 데이터 비교 후 저장
            if (numOfRows > 0) {
                ResponseDTO response = callOpenApi(type, lawdCd, 1, numOfRows, deelYmd);

                if ("OK".equals(response.getMessage())) {
                    body = response.getBody();
                    Element items = body.getChild("items");
                    List<Element> itemList = items.getChildren("item");

                    if ("deal".equals(type)) {
                        saveTradeDeal(itemList);
                    } else {
                        tradeRentRepository.deleteByLawdCdAndYearAndMonth(lawdCd, year, month);

                        saveTradeRent(itemList);
                    }
                }

                result = 1;
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

    public void saveGeom() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(GEOM_API_URL + "?apikey=" + GEOM_API_KEY, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(response.getBody());

            geomRepository.deleteAll();

            for (Object obj : jsonArray) {
                JSONObject json = (JSONObject) obj;
                log.info("info : {}", json);

                geomRepository.save(GeomEntity.builder()
                        .lineNm(String.valueOf(json.get("lineNm")))
                        .convX(Double.valueOf(String.valueOf(json.get("convX"))).doubleValue())
                        .convY(Double.valueOf(String.valueOf(json.get("convY"))).doubleValue())
                        .stnKrNm(String.valueOf(json.get("stnKrNm")))
                        .outStnNum(String.valueOf(json.get("outStnNum")))
                        .build());
            }
        }
    }

    public String geomInfo(String name) {
        List<String> nameList = new ArrayList<>();
        String[] nameArray = name.split(",");

        for (String stn : nameArray) {
            nameList.add(stn);
        }

        List<GeomEntity> geomList = geomRepository.findByStnKrNmIn(nameList);

        if (CollectionUtils.isEmpty(geomList)) return "";

        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();

        for (GeomEntity geomEntity : geomList) {
            JSONObject tradeObj = new JSONObject();

            tradeObj.put("lineNm" , geomEntity.getLineNm());
            tradeObj.put("stnKrNm", geomEntity.getStnKrNm());
            tradeObj.put("convX"  , geomEntity.getConvX());
            tradeObj.put("convY"  , geomEntity.getConvY());

            array.add(tradeObj);
        }

        obj.put("data", array);

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

            lawdObj.put("lawdCd", lawdEntity.getLawdCd());
            lawdObj.put("lawdNm", lawdEntity.getLawdNm());

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

}
