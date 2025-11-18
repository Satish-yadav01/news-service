package com.satish.newsservice.exception;

import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private Long tid;
    private String message;

    public ApiException(Long tid, String message) {
        super(message);
        this.tid = tid;
        this.message = message;
    }
}

