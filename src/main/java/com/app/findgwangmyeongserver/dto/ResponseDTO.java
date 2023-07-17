package com.app.findgwangmyeongserver.dto;

import lombok.Builder;
import lombok.Data;
import org.jdom2.Element;

@Data
@Builder
public class ResponseDTO {

    private String message;
    private Element header;
    private Element body;

}
