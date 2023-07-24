package com.app.findgwangmyeongserver.service;

import com.app.findgwangmyeongserver.dto.ResponseDTO;
import com.app.findgwangmyeongserver.dto.TradeDTO;
import com.app.findgwangmyeongserver.entity.TradeEntity;
import com.app.findgwangmyeongserver.repo.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.HttpURLConnection;
import java.net.URL;
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

    private final static String LAWD_CD = "41210"; //광명지역코드

    private final TradeRepository tradeRepository;

    private ResponseDTO callOpenApi(
            int pageNo,
            int numOfRows,
            String dealYmd
    ) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(API_URL);
        sb.append("?serviceKey=" + SERVICE_KEY);
        sb.append("&LAWD_CD=" + LAWD_CD);
        sb.append("&DEAL_YMD=" + dealYmd);

        if (pageNo != 0) sb.append("&pageNo=" + pageNo);
        if (numOfRows != 0) sb.append("&numOfRows=" + numOfRows);

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
            String year,
            String month
    ) throws Exception {
        List<TradeEntity> tradeList = tradeRepository.findByYearAndMonth(year, month);

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

            array.add(tradeObj);
        }

        obj.put("tradeYm"   , year + month);
        obj.put("totalCount", tradeList.size());
        obj.put("data"      , array);

        return obj.toString();
    }

    public void saveLatestTradeData(
            String year,
            String month
    ) throws Exception {
        String deelYmd = year + month;

        ResponseDTO response = callOpenApi(0, 0, deelYmd);

        if ("OK".equals(response.getMessage())) {
            Element body = response.getBody();

            //총 매매 거래내역 수(최신)
            int totalCount = Integer.parseInt(body.getChild("totalCount").getContent(0).getValue());

            //현재까지 기록된 거래내역 수
            long count = tradeRepository.countByYearAndMonth(year, month);
            int currentCount = Optional.of(count).orElse(0L).intValue();

            int numOfRows = totalCount - currentCount;

            if (numOfRows > 0) {
                if (currentCount == 0) {   //등록된 거래내역이 없는 경우
                    saveTrade(1, numOfRows, deelYmd);
                } else {
                    saveTrade(2, numOfRows, deelYmd);
                }
            }
        }
    }

    public void saveTrade(
            int pageNo,
            int numOfRows,
            String dealYmd
    ) throws Exception {
        ResponseDTO response = callOpenApi(pageNo, numOfRows, dealYmd);

        if ("OK".equals(response.getMessage())) {
            Element body = response.getBody();
            Element items = body.getChild("items");
            List<Element> itemList = items.getChildren("item");

            for (Element item : itemList) {
                List<Element> tradeInfoList = item.getChildren();
                TradeDTO tradeDTO = new TradeDTO();

                for (Element info : tradeInfoList) {
                    switch (info.getName()) {
                        case "년":
                            tradeDTO.setYear(info.getContent(0).getValue().trim());
                            break;
                        case "월":
                            tradeDTO.setMonth(String.format("%02d", Integer.parseInt(info.getContent(0).getValue().trim())));
                            break;
                        case "일":
                            tradeDTO.setDay(String.format("%02d", Integer.parseInt(info.getContent(0).getValue().trim())));
                            break;
                        case "아파트":
                            tradeDTO.setApartName(info.getContent(0).getValue().trim());
                            break;
                        case "전용면적":
                            tradeDTO.setApartArea(info.getContent(0).getValue().trim());
                            break;
                        case "층":
                            tradeDTO.setApartFloor(Integer.parseInt(info.getContent(0).getValue().trim()));
                            break;
                        case "거래금액":
                            tradeDTO.setTradeMoney(Integer.parseInt(info.getContent(0).getValue().trim().replaceAll(",", "")));
                            break;
                        case "거래유형":
                            tradeDTO.setTradeType(info.getContent(0).getValue().trim());
                            break;
                        case "지역코드":
                            tradeDTO.setLawdCd(info.getContent(0).getValue().trim());
                            break;
                        case "법정동":
                            tradeDTO.setApartDong(info.getContent(0).getValue().trim());
                            break;
                        case "도로명":
                            tradeDTO.setApartStreet(info.getContent(0).getValue().trim());
                            break;
                        case "지번":
                            tradeDTO.setAddress(info.getContent(0).getValue().trim());
                            break;
                    }
                }

                log.info("apart {}", tradeDTO);
                ModelMapper modelMapper = new ModelMapper();
                TradeEntity tradeEntity = modelMapper.map(tradeDTO, TradeEntity.class);

                tradeRepository.save(tradeEntity);
            }
        }
    }
}
