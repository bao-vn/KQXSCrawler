package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private String companyName;
    private String strPublishedDate;
    private Date publishedDate;
}
