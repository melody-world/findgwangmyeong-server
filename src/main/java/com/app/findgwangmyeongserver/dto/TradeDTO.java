package com.app.findgwangmyeongserver.dto;

import lombok.Data;

@Data
public class TradeDTO {

    private String year;        //거래년도
    private String month;       //거래월
    private String day;         //거래일
    private String apartName;   //아파트명
    private String apartArea;   //전용면적
    private int apartFloor;     //층
    private int tradeMoney;     //거래금액|보증금액
    private int rentMoney;      //월세금액
    private int bfTradeMoney;   //종전계약보증금
    private int bfRentMoney;    //종전계약월세
    private String tradeType;   //계약구분
    private String rentDate;    //계약기간
    private String lawdCd;      //지역코드
    private String apartDong;   //법정동
    private String apartStreet; //도로명
    private String address;     //지번

}
