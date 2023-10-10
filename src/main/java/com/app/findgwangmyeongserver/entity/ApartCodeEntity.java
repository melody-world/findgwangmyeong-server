package com.app.findgwangmyeongserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="APART_CODE")
@NoArgsConstructor
@AllArgsConstructor
public class ApartCodeEntity {

    @Id
    private String apartCode;

    private String apartName;
    private String address;
    private String lawdCd;
}
