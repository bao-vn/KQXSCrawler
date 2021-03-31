package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {
    /** */
    private List<String> companyName;

    /** */
    private String strDate;

    /** publishedDate */
    private Date date;
}
