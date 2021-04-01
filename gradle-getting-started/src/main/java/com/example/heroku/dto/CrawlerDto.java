package com.example.heroku.dto;

import com.google.cloud.firestore.FieldValue;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrawlerDto {
    /** KẾT QUẢ XỔ SỐ AN GIANG NGÀY 18/03 (Thứ Năm) */
    private String title;

    /** https://xskt.com.vn/xsag/ngay-18-3-2021 */
    private String link;

    /** Wed, 24 Mar 2021 10:36:31 GMT */
    private Date publishedDate;

    /** 2021-03-24 */
    private String strPublishedDate;

    /** ĐB: 707018 1: 54311 2: 41652 3: 01202 - 78423 4: 53677 - 58657 - 75149 - 51452 - 24755 - 26234 - 63484 5: 6262 6: 9036 - 1564 - 7961 7: 7198: 65 */
    private List<String> results;

    private FieldValue updatedTime;
}
