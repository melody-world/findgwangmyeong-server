package com.app.findgwangmyeongserver.entity;

import lombok.Data;

@Data
public class MsgEntity {

    private String message;
    private Object result;

    public MsgEntity(String message, Object result) {
        this.message = message;
        this.result	 = result;
    }
}