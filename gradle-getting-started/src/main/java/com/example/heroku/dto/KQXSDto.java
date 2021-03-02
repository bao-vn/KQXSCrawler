package com.example.heroku.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
public class KQXSDto {
    private String title;
    private Map<String, String> description;
    private String link;
    private Date publishedDate;
}
