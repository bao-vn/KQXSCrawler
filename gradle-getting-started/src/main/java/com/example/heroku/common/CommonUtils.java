package com.example.heroku.common;

import com.example.heroku.dto.KQXSDto;
import com.example.heroku.dto.XoSoKienThiet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CommonUtils {
    public boolean isEmptyOrWhiteSpaces(String source) {
        return StringUtils.isEmpty(source) && StringUtils.isBlank(source);
    }

    /**
     * Convert KQXSDto to Map<String, Object>
     *
     * @param kqxsDto KQXSDto
     * @return Map<String, Object>
     */
    public Map<String, Object> convertToMap(KQXSDto kqxsDto) {
        // title, link, publishedDate, results[9]
        Map<String, Object> kqxs = new HashMap<>();
        Field[] fields = kqxsDto.getClass().getDeclaredFields();
        kqxs.put(fields[0].getName(), kqxsDto.getTitle());
        kqxs.put(fields[1].getName(), kqxsDto.getLink());
        kqxs.put(fields[2].getName(), kqxsDto.getPublishedDate());

        // result[9]
        List<String> results = new ArrayList<>();
        XoSoKienThiet xoSoKienThiet = kqxsDto.getResults();
        results.add(xoSoKienThiet.getGiaiDacBiet());
        results.add(xoSoKienThiet.getGiaiNhat());
        results.add(xoSoKienThiet.getGiaiNhi());
        results.add(xoSoKienThiet.getGiaiBa());
        results.add(xoSoKienThiet.getGiaiTu());
        results.add(xoSoKienThiet.getGiaiNam());
        results.add(xoSoKienThiet.getGiaiSau());
        results.add(xoSoKienThiet.getGiaiBay());
        results.add(xoSoKienThiet.getGiaiTam());
        kqxs.put("results", results);

        return kqxs;
    }

    /**
     * Parse to LocalDate from rss link
     * Example: https://xskt.com.vn/xsag/ngay-18-3-2021
     *
     * @param link: String from rss link
     * @return Date
     */
    public Date parseToLocalDateFromLink(String link) throws ParseException {
        String[] strLinks = link.split("/");
        String[] strDates = strLinks[strLinks.length - 1].split("-");
        String strDate = strDates[2] + "/" + strDates[1] + "/" + strDates[3].substring(2);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        LocalDate localDate = LocalDate.parse(strDate, dateTimeFormatter);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * Parse to LocalDate from rss link
     * Example: https://xskt.com.vn/xsag/ngay-18-3-2021
     *
     * @param link: String from rss link
     * @return Date
     */
    public String parseToStringDateFromLink(String link) throws ParseException {
        String[] strLinks = link.split("/");
        String[] strDates = strLinks[strLinks.length - 1].split("-");
        String strDate = strDates[2] + "/" + strDates[1] + "/" + strDates[3].substring(2);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        LocalDate localDate = LocalDate.parse(strDate, dateTimeFormatter);

        return localDate.toString();
    }

    /**
     * Parse rss data
     *
     * @param results String
     * @return XoSoKienThiet
     */
    public XoSoKienThiet string2KQXSDescription(String results) {
        // Reflection Description
        String[] prizeList = new String[9];

        // ↵ĐB: 55600↵1: 59302↵2: 78836 - 71711↵3: 57669 - 79931 - 24351 - 86322 - 54511 - 71826↵4: 6225 - 6043 - 3742 - 0666↵5: 0314 - 6945 - 0521 - 6066 - 8579 - 0910↵6: 203 - 330 - 633↵7: 04 - 70 - 40 - 37
        String[] split = results.split("\n");
        int indexPrizeName = 0;
        for (String item: split) {
            if (indexPrizeName > 8) {
                break;
            }

            if (StringUtils.isEmpty(item) || StringUtils.isBlank(item)) {
                continue;
            }

            // index of prizeName
            String[] resultWithTitlePrize = item.split(":");

            if (resultWithTitlePrize.length < 2
                    || ((StringUtils.isEmpty(resultWithTitlePrize[0]) || StringUtils.isBlank(resultWithTitlePrize[0]))
                    && (StringUtils.isEmpty(resultWithTitlePrize[1]) || StringUtils.isBlank(resultWithTitlePrize[1])))) {
                continue;
            }

            String prize = resultWithTitlePrize[1];
            prizeList[indexPrizeName] = prize.trim();

            // GiaiTam
            if (resultWithTitlePrize.length == 3 && indexPrizeName == 7
                    && !this.isEmptyOrWhiteSpaces(resultWithTitlePrize[2])) {
                prizeList[indexPrizeName + 1] = resultWithTitlePrize[2].trim();
            }
            indexPrizeName++;
        }

        return XoSoKienThiet.builder()
//                .maCongTy(companyName)
                .GiaiDacBiet(prizeList[0])
                .GiaiNhat(prizeList[1])
                .GiaiNhi(prizeList[2])
                .GiaiBa(prizeList[3])
                .GiaiTu(prizeList[4])
                .GiaiNam(prizeList[5])
                .GiaiSau(prizeList[6])
                .GiaiBay(prizeList[7])
                .GiaiTam(prizeList[8])
                .build();
    }

    /**
     * Parse rss data in case <description> tag contains multiple results.
     * Example: https://xskt.com.vn/rss-feed/mien-nam-xsmn.rss
     *
     * @param description contains multiple results
     * @return List<XoSoKienThiet> list of prize for each result
     */
    public List<XoSoKienThiet> multipleString2KQXSDescription(String description) {
        // Reflection Description
        List<XoSoKienThiet> companyWithPrizeList = new ArrayList<>();

        //  [Cần Thơ] ĐB: 414303 1: 51374 2: 50151 3: 51102 - 31421 4: 77132 - 16282 - 27680 - 24815 - 84724 - 87059 - 08557 5: 2523 6: 6215 - 4816 - 7933 7: 2228: 06 [Đồng Nai] ĐB: 279699 1: 13499 2: 07745 3: 05120 - 77404 4: 05993 - 53444 - 48080 - 89559 - 16888 - 23744 - 12345 5: 7193 6: 7558 - 6461 - 6842 7: 0658: 32 [Sóc Trăng] ĐB: 454847 1: 72330 2: 42590 3: 26544 - 70144 4: 86931 - 79675 - 09519 - 85255 - 58821 - 60418 - 11558 5: 2962 6: 6470 - 6472 - 0714 7: 1278: 47
        String[] splitByCompanyName = description.split("\\[");
        int indexPrizeName = 0;
        for (String item: splitByCompanyName) {
            // item = Cần Thơ] ĐB: 414303 1: 51374 2: 50151 3: 51102 - 31421 4: 77132 - 16282 - 27680 - 24815 - 84724 - 87059 - 08557 5: 2523 6: 6215 - 4816 - 7933 7: 2228: 06
            if (StringUtils.isEmpty(item) || StringUtils.isBlank(item)) {
                continue;
            }

            // result = ["Cần Thơ"," ĐB: 414303 1: 51374 2: 50151 3: 51102 - 31421 4: 77132 - 16282 - 27680 - 24815 - 84724 - 87059 - 08557 5: 2523 6: 6215 - 4816 - 7933 7: 2228: 06"]
            String[] parseIntoResult = item.split("\\]");
            if (parseIntoResult.length < 2
                    || (StringUtils.isEmpty(parseIntoResult[0]) || StringUtils.isBlank(parseIntoResult[0])
                    && StringUtils.isEmpty(parseIntoResult[1]) || StringUtils.isBlank(parseIntoResult[1]))
            ) {
                continue;
            }
            String companyName = parseIntoResult[0];
            String result = parseIntoResult[1];

            companyWithPrizeList.add(string2KQXSDescription(result));

            indexPrizeName++;
        }

        return companyWithPrizeList;
    }

    public String parseCompanyNameFromTitleLink(String title) {
        String name = title.split("RSS feed xổ số ")[1];
//        ByteArrayOutputStream arrayOutputStream = name.getBytes(StandardCharsets.UTF_8);
//        OutputStreamWriter out = new OutputStreamWriter(arrayOutputStream);
//        name = out.getEncoding();
        name = name.replace(" ", "");

        return name;
    }
}
