package com.satish.newsservice.controller;

import com.satish.newsservice.data.dto.NewsResponseDto;
import com.satish.newsservice.data.dto.ResponseData;
import com.satish.newsservice.service.impl.NewsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
            @RequestParam(required = false/*, defaultValue = DEFAULT_SRC_BBC_NEWS*/) String sources,
            @RequestParam(required = false/*, defaultValue = DEFAULT_COUNTRY*/) String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest req
    ) {
        Long tid = (Long) req.getAttribute("tid");
        log.info("Inside getTopHeadlines methods, TID for this request: {} \n red : {}", tid, req);
        return newsService.getTopHeadlines(sources, country, tid, page, pageSize)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/everything")
    public Mono<ResponseEntity<ResponseData<NewsResponseDto>>> getEveryNews(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest req,
            Sort sort) {
        Long tid = (Long) req.getAttribute("tid");
        log.info("Inside getEveryNews method, TID for this request: {} \n red : {}", tid, req);
        return newsService.getEveryNews(tid, q,from,sortBy, page, pageSize)
                .map(ResponseEntity::ok);
    }

}

