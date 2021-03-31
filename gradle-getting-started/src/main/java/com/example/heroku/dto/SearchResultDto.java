package com.example.heroku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    /** All results of prizes */
    private XoSoKienThiet result;

    /** prize won */
    private String winPrizeName;

    /** result won */
    private String winResult;

    /** WIN, LOSE, OUT_DATE, NOT_PUBLISHED */
    private String status;
}
