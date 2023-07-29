package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="GEOM_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class GeomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long geomSeq;

    private String lineNm;
    private String stnKrNm;
    @Column(name = "CONV_X")
    private double convX;
    @Column(name = "CONV_Y")
    private double convY;
    private String outStnNum;
}
