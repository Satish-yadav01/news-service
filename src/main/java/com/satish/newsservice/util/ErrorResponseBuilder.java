package com.satish.newsservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satish.newsservice.data.dto.ResponseData;

import java.time.LocalDateTime;

public class ErrorResponseBuilder {

    public static String build(Long tid, int statusCode, String statusMsg) {
        ResponseData<Object> response = new ResponseData<>();
        response.setTid(tid);
        response.setStatusCode(statusCode);
        response.setStatusMsg(statusMsg);
        response.setData(null);
        response.setTxnTime(LocalDateTime.now());

        try {
            return new ObjectMapper().writeValueAsString(response);
        } catch (Exception e) {
            return """
            {
              "tid": %d,
              "statusCode": %d,
              "statusMsg": "%s",
              "data": null,
              "txnTime": "%s"
            }
            """.formatted(tid, statusCode, statusMsg, LocalDateTime.now());
        }
    }
}

