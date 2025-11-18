package com.satish.newsservice.controller;

import com.satish.newsservice.data.dto.NewsResponseDto;
import com.satish.newsservice.data.dto.ResponseData;
import com.satish.newsservice.service.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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


    private final NewsService newsService;

    public NewsController(NewsService newsService) {
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
        log.info("TID for this request: {} \n red : {}", tid, req);
        return newsService.getTopHeadlines(sources, country, tid, page, pageSize)
                .map(ResponseEntity::ok);
    }

}

