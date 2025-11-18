package com.satish.newsservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseData<T> {
    private Long tid;
    private int statusCode;
    private String statusMsg;
    private T data;
    private LocalDateTime txnTime;
}
