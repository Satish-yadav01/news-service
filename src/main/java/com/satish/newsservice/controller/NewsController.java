package com.satish.newsservice.controller;

import com.satish.newsservice.data.dto.NewsResponseDto;
import com.satish.newsservice.data.dto.ResponseData;
import com.satish.newsservice.service.impl.NewsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/news")
@Slf4j
public class NewsController {

    private final NewsServiceImpl newsService;

    public NewsController(NewsServiceImpl newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/top-headlines")
    public Mono<ResponseEntity<ResponseData<NewsResponseDto>>> getTopHeadlines(
            @RequestParam(required = false) String sources,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            ServerWebExchange exchange
    ) {
        Long tid = exchange.getAttribute("TID");  // <-- Correct for WebFlux
        log.info("Inside getTopHeadlines methods, TID for this request: {} \n exchange : {}", tid, exchange);
        return newsService.getTopHeadlines(sources, country, tid, page, pageSize)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/everything")
    public Mono<ResponseEntity<ResponseData<NewsResponseDto>>> getEveryNews(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            ServerWebExchange exchange
    ) {
        Long tid = exchange.getAttribute("TID");  // <-- Correct for WebFlux
        log.info("Inside getEveryNews method, TID for this request: {} \n exchange : {}", tid, exchange);
        return newsService.getEveryNews(tid, q,language,country,category,from,sortBy, page, pageSize)
                .map(ResponseEntity::ok);
    }

}

