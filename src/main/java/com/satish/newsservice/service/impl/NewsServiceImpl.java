package com.satish.newsservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satish.newsservice.data.dto.NewsResponseDto;
import com.satish.newsservice.data.dto.ResponseData;
import com.satish.newsservice.data.entity.AuditLog;
import com.satish.newsservice.data.repo.AuditLogRepository;
import com.satish.newsservice.exception.ApiException;
import com.satish.newsservice.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;


import static com.satish.newsservice.constant.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private String NEWS_URL_TOP_HEADLINE;
    private String API_KEY;

    private final WebClient webClient;
    private final AuditLogRepository auditLogRepo;
    private final ObjectMapper mapper;
    private final Environment env;

    @PostConstruct
    void init(){
        NEWS_URL_TOP_HEADLINE = env.getProperty("NEWS_URL_TOP_HEADLINE");
        API_KEY = env.getProperty("API_KEY");
    }

    @Override
    public Mono<ResponseData<NewsResponseDto>> getTopHeadlines(
            String sources,
            String country,
            Long tid,
            int page,
            int pageSize) {
        ResponseData<NewsResponseDto> responseData = initializeResponseData(tid);
        log.info("getTopHeadlines Initial ResponseData : {}", responseData);

        if(Strings.isNotEmpty(sources) && Strings.isNotEmpty(country)){
            throw new ApiException(tid, "Please pass any one of the query Parameter source %s and country %s".formatted(sources,country));
        }

        Function<UriBuilder, URI> uriBuilder = builder -> builder
                .queryParam("sources", sources)
                .queryParam("country", country)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParam("apiKey", API_KEY)
                .build();

        return getResponseDataMono(NEWS_URL_TOP_HEADLINE,tid,page,pageSize,uriBuilder, responseData);
    }


    @Override
    public Mono<ResponseData<NewsResponseDto>> getEveryNews(Long tid,
                                String query,
                                String language,
                                String country,
                                String category,
                                String from,
                                String sortBy,
                                int page,
                                int pageSize) {
        ResponseData<NewsResponseDto> responseData = initializeResponseData(tid);
        log.info("getEveryNews Initial ResponseData : {} with q : {} ,from : {}, sortBy : {}, page : {}, pagesize : {} ",  responseData,query,from,sortBy,page,pageSize);

        Function<UriBuilder, URI> uriBuilder = builder -> builder
                .queryParam("page", page)
                .queryParam("language", language)
                .queryParam("country", country)
                .queryParam("category", category)
                .queryParam("q", query)
                .queryParam("from", from)
                .queryParam("sortBy", sortBy)
                .queryParam("pageSize", pageSize)
                .queryParam("apiKey", API_KEY)
                .build();

        log.info("URI Builder : {}",uriBuilder);

        Mono<ResponseData<NewsResponseDto>> responseDataMono = getResponseDataMono(NEWS_URL_TOP_HEADLINE, tid, page, pageSize, uriBuilder, responseData);
        log.info("responseDataMono : {}",responseDataMono);
        return responseDataMono;
    }

    private Mono<ResponseData<NewsResponseDto>> getResponseDataMono(String url,Long tid, int page, int pageSize, Function<UriBuilder, URI> uriBuilder, ResponseData<NewsResponseDto> responseData) {
        return webClient.get()
                .uri(url, uriBuilder)
                .retrieve()
                .onStatus(status -> status.isError(), error ->
                        error.bodyToMono(String.class)
                                .flatMap(errorMsg -> {
                                    log.error("Error Response from News API: {}", errorMsg);
                                    return Mono.error(new ApiException(tid, "Error from News API: ".concat(errorMsg)));
                                })
                )
                .bodyToMono(NewsResponseDto.class)
                .map(resp -> getNewsResponseDtoResponseData(tid, page, pageSize, resp, responseData))
                .publishOn(Schedulers.boundedElastic())
                .doFinally(p -> auditLogRepo.findByTid(tid)
                        .ifPresentOrElse(
                                auditLog -> saveAuditLog(tid, auditLog, responseData),
                                () -> {
                                    throw new ApiException(tid, "Audit log not found for tid: ".concat(String.valueOf(tid)));
                                }
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


    private static ResponseData<NewsResponseDto> initializeResponseData(Long tid) {
        return ResponseData.<NewsResponseDto>builder()
                .tid(tid)
                .statusCode(PENDING_STATUS_CODE)
                .statusMsg(PENDING_STATUS_MSG)
                .build();
    }
}

