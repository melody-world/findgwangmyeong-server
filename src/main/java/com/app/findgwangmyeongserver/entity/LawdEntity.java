package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="LAWD_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class LawdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String lawdCd;
    private String lawdNm;
    private String masterCd;
    private String activeYn;

}
