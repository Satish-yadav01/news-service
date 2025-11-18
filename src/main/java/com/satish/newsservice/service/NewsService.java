package com.satish.newsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satish.newsservice.constant.Constants;
import com.satish.newsservice.data.dto.NewsResponseDto;
import com.satish.newsservice.data.dto.ResponseData;
import com.satish.newsservice.data.entity.AuditLog;
import com.satish.newsservice.data.repo.AuditLogRepository;
import com.satish.newsservice.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.LocalDateTime;


import static com.satish.newsservice.constant.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private static final String NEWS_URL = "https://newsapi.org/v2/top-headlines";
    private static final String API_KEY = "3fb19034e70b441b8e6fecf5228f23b4";

    private final WebClient webClient;
    private final AuditLogRepository auditLogRepo;
    private final ObjectMapper mapper;
    private final Environment env;

    public Mono<ResponseData<NewsResponseDto>> getTopHeadlines(
            String sources,
            String country,
            Long tid,
            int page,
            int pageSize) {
        ResponseData<NewsResponseDto> responseData = initilizeResponseData(tid);
        log.info("Initial ResponseData : {}", responseData);

        if(Strings.isNotEmpty(sources) && Strings.isNotEmpty(country)){
            throw new ApiException(tid, "Please pass any one of the query Parameter source %s and country %s".formatted(sources,country));
        }


        return webClient.get()
                .uri(NEWS_URL, builder -> builder
                        .queryParam("sources", sources)
                        .queryParam("country", country)
                        .queryParam("page", page)
                        .queryParam("pageSize", pageSize)
                        .queryParam("apiKey", API_KEY)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, error ->
                        error.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(new ApiException(tid, "API Error: " + msg)))
                )
                .bodyToMono(NewsResponseDto.class)
                .map(resp -> getNewsResponseDtoResponseData(tid, page, pageSize, resp, responseData))
                .publishOn(Schedulers.boundedElastic())
                .doFinally(p-> auditLogRepo.findByTid(tid)
                        .ifPresentOrElse(
                                auditLog -> saveAuditLog(tid, auditLog, responseData),
                                () -> {throw new ApiException(tid,"Audit log not found for tid: ".concat(String.valueOf(tid)));}
                        ));
    }

    private void saveAuditLog(Long tid, AuditLog auditLog, ResponseData<NewsResponseDto> responseData) {
        log.info("Audit log found: {}",auditLog);
        try {
            auditLog.setTid(tid);
            auditLog.setResponse(mapper.writeValueAsString(responseData));
            auditLog.setResponseTime(LocalDateTime.now());
            auditLog.setStatusCode(SUCCESS_STATUS_CODE);
            auditLog.setStatusMsg(SUCCESS_STATUS_MSG);
            auditLogRepo.save(auditLog);
        } catch (JsonProcessingException e) {
            throw new ApiException(tid, e.getMessage());
        }
    }

    private static ResponseData<NewsResponseDto> getNewsResponseDtoResponseData(Long tid, int page,
                int pageSize, NewsResponseDto resp, ResponseData<NewsResponseDto> responseData) {

        // enrich the response
        resp.setPage(page);
        resp.setPageSize(pageSize);

        // fill ResponseData
        responseData.setTid(tid);
        responseData.setData(resp);
        responseData.setStatusCode(SUCCESS_STATUS_CODE);
        responseData.setStatusMsg(SUCCESS_STATUS_MSG);
        responseData.setTxnTime(LocalDateTime.now());

        log.info("responseData : {}",responseData);

        return responseData;
    }


    private static ResponseData<NewsResponseDto> initilizeResponseData(Long tid) {
        return ResponseData.<NewsResponseDto>builder()
                .tid(tid)
                .statusCode(PENDING_STATUS_CODE)
                .statusMsg(PENDING_STATUS_MSG)
                .build();
    }
}

