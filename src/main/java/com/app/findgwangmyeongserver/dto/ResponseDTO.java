package com.app.findgwangmyeongserver.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "response")
public class ResponseDTO {

    private List<ItemDTO> itemInfo;

    @XmlElementWrapper(name="items")
    @XmlElement(name="item")
    public List<ItemDTO> getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(List<ItemDTO> memberInfo) {
        this.itemInfo = itemInfo;
    }


}
