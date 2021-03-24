package com.example.heroku.common;

import com.example.heroku.dto.KQXSDto;
import com.example.heroku.dto.XoSoKienThiet;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
