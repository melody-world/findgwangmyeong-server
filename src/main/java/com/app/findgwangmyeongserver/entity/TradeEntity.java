package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="TRADE_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity {

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
    private String tradeType;
    private String lawdCd;
    private String apartDong;
    private String apartStreet;
    private String address;

}
