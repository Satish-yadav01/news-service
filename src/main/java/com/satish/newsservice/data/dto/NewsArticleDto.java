package com.satish.newsservice.data.dto;

import lombok.Data;

@Data
public class NewsArticleDto {
    private SourceDto source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;
}

