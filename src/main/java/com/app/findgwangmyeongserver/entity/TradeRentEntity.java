package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="TRADE_RENT_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class TradeRentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tradeSeq;
    private String year;
    private String month;
    private String day;
    private String apartName;
    private String apartArea;
    private int apartFloor;
    private int tradeMoney;
    private int rentMoney;
    private int bfTradeMoney;
    private int bfRentMoney;
    private String tradeType;
    private String rentDate;
    private String lawdCd;
    private String apartDong;
    private String address;

}