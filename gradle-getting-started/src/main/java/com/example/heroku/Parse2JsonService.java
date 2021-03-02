package com.example.heroku;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Parse2JsonService {
    private final String[] PRIZE_NAME = {"Đặc biệt", "Giải nhất", "Giải nhì", "Giải ba", "Giải tư", "Giải năm", "Giải sáu", "Giải bảy", "Giải tám"};

    public Map<String, String> string2KQXSDescription(String description) {
        // Reflection Description
        Map<String, String> prizeList = new HashMap<>();

        // ↵ĐB: 55600↵1: 59302↵2: 78836 - 71711↵3: 57669 - 79931 - 24351 - 86322 - 54511 - 71826↵4: 6225 - 6043 - 3742 - 0666↵5: 0314 - 6945 - 0521 - 6066 - 8579 - 0910↵6: 203 - 330 - 633↵7: 04 - 70 - 40 - 37
        String[] split = description.split("\n");
        int indexPrizeName = 0;
        for (String item: split) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            // index of prizeName
            String prize = item.split(":")[1];
            prizeList.put(PRIZE_NAME[indexPrizeName], prize);

            indexPrizeName++;
        }

        return prizeList;
    }
}