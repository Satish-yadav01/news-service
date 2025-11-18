package com.satish.newsservice.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewsResponseDto {
    private int totalResults;
    private List<NewsArticleDto> articles;
    private int page;
    private int pageSize;
}

