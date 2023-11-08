package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="LAWD_MASTER")
@NoArgsConstructor
@AllArgsConstructor
public class LawdMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String masterCd;
    private String lawdNm;
    private String activeYn;

}
