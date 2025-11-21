package com.satish.newsservice.service;

import com.satish.newsservice.data.dto.NewsResponseDto;
import com.satish.newsservice.data.dto.ResponseData;
import reactor.core.publisher.Mono;

public interface NewsService {
    Mono<ResponseData<NewsResponseDto>> getTopHeadlines(
            String sources,
            String country,
            Long tid,
            int page,
            int pageSize);

    Mono<ResponseData<NewsResponseDto>> getEveryNews(
            Long tid,
            String query,
            String from,
            String sortBy,
            int page,
            int pageSize);
}
