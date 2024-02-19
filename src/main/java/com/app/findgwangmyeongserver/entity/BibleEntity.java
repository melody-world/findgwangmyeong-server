package com.app.findgwangmyeongserver.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name="BIBLE_INFO")
@NoArgsConstructor
@AllArgsConstructor
public class BibleEntity {
    @Id
    private long seq;
    private String book;
    private String chapter;
    private String verse;
    private String content;
}
