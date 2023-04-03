package com.engineer.yt.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class Response <T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -2010077110826408295L;

    private Long code;

    private String message;

    private T items;

    public Response(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(Long code, String message, T items) {
        this.code = code;
        this.message = message;
        this.items = items;
    }
}
