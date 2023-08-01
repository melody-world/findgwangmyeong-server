package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="GEOM_COLOR_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class GeomColorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String lineNm;
    private String colorValue;

}
