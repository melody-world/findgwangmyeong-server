package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="APART_LIST")
@NoArgsConstructor
@AllArgsConstructor
public class ApartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    private String apartName;
    private String apartDong;
    private String address;
    @Column(name="CONV_X")
    private double convX;
    @Column(name="CONV_Y")
    private double convY;
    private String lawdCd;
    private String apartCode;
    private String masterCd;
}
