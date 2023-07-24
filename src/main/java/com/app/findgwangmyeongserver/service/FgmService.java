package com.app.findgwangmyeongserver.service;

import com.app.findgwangmyeongserver.dto.ResponseDTO;
import com.app.findgwangmyeongserver.dto.TradeDTO;
import com.app.findgwangmyeongserver.entity.TradeEntity;
import com.app.findgwangmyeongserver.entity.TradeRentEntity;
import com.app.findgwangmyeongserver.repo.TradeRentRepository;
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

    private final TradeRepository tradeRepository;
    private final TradeRentRepository tradeRentRepository;

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
            tradeObj.put("tradeMoney"  , tradeInfo.getRentMoney());
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

    public void saveLatestTradeData(
            String lawdCd,
            String type,
            String year,
            String month
    ) throws Exception {
        String deelYmd = year + month;

        ResponseDTO response = callOpenApi(type, lawdCd,0, 0, deelYmd);

        if ("OK".equals(response.getMessage())) {
            Element body = response.getBody();

            //총 매매 거래내역 수(최신)
            int totalCount = Integer.parseInt(body.getChild("totalCount").getContent(0).getValue());

            //현재까지 기록된 거래내역 수
            long count = "deal".equals(type) ? tradeRepository.countByLawdCdAndYearAndMonth(lawdCd, year, month) :
                                                tradeRentRepository.countByLawdCdAndYearAndMonth(lawdCd, year, month);
            int currentCount = Optional.of(count).orElse(0L).intValue();

            int numOfRows = totalCount - currentCount;

            if (numOfRows > 0) {
                if (currentCount == 0) {   //등록된 거래내역이 없는 경우
                    saveTrade(type, lawdCd, 1, numOfRows, deelYmd);
                } else {
                    saveTrade(type, lawdCd, 2, numOfRows, deelYmd);
                }
            }
        }
    }

    public void saveTrade(
            String type,
            String lawdCd,
            int pageNo,
            int numOfRows,
            String dealYmd
    ) throws Exception {
        ResponseDTO response = callOpenApi(type, lawdCd, pageNo, numOfRows, dealYmd);

        if ("OK".equals(response.getMessage())) {
            Element body = response.getBody();
            Element items = body.getChild("items");
            List<Element> itemList = items.getChildren("item");

            if ("deal".equals(type)) {
                saveTradeDeal(itemList);
            } else {
                saveTradeRent(itemList);
            }
        }
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

    private static String nullToStr(Object str, String strDefault) {
		if (str == null || str == "null" || "null".equals(str.toString()) || "undefined".equals(str.toString()) || str.toString().length() == 0) {
			return strDefault;
		} else {
			return str.toString();
		}
	}

}
