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
public class ApartListEntity {

    @Id
    private String apartCode;
    private String apartName;
    private String apartAddress;
    @Column(name="CONV_X")
    private double convX;
    @Column(name="CONV_Y")
    private double convY;
    private String lawdCd;
}
