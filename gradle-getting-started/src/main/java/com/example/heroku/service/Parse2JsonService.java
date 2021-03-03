package com.example.heroku.service;

import com.example.heroku.dto.XoSoKienThiet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Parse2JsonService {
    private final String[] PRIZE_NAME = {"Đặc biệt", "Giải nhất", "Giải nhì", "Giải ba", "Giải tư", "Giải năm", "Giải sáu", "Giải bảy", "Giải tám"};

    public XoSoKienThiet string2KQXSDescription(String companyName, String results) {
        // Reflection Description
        String[] prizeList = new String[9];

        // ↵ĐB: 55600↵1: 59302↵2: 78836 - 71711↵3: 57669 - 79931 - 24351 - 86322 - 54511 - 71826↵4: 6225 - 6043 - 3742 - 0666↵5: 0314 - 6945 - 0521 - 6066 - 8579 - 0910↵6: 203 - 330 - 633↵7: 04 - 70 - 40 - 37
        String[] split = results.split("\n");
        int indexPrizeName = 0;
        for (String item: split) {
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            // index of prizeName
            String[] resultWithTitlePrize = item.split(":");

            if (resultWithTitlePrize.length != 2
                    || (StringUtils.isEmpty(resultWithTitlePrize[0])
                    && StringUtils.isEmpty(resultWithTitlePrize[1]))) {
                continue;
            }

            String prize = resultWithTitlePrize[1];
            prizeList[indexPrizeName]=prize;
            indexPrizeName++;
        }

        return XoSoKienThiet.builder()
                .maCongTy(companyName)
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

    public List<XoSoKienThiet> multipleString2KQXSDescription(String description) {
        // Reflection Description
        List<XoSoKienThiet> companyWithPrizeList = new ArrayList<>();

        //  [Cần Thơ] ĐB: 414303 1: 51374 2: 50151 3: 51102 - 31421 4: 77132 - 16282 - 27680 - 24815 - 84724 - 87059 - 08557 5: 2523 6: 6215 - 4816 - 7933 7: 2228: 06 [Đồng Nai] ĐB: 279699 1: 13499 2: 07745 3: 05120 - 77404 4: 05993 - 53444 - 48080 - 89559 - 16888 - 23744 - 12345 5: 7193 6: 7558 - 6461 - 6842 7: 0658: 32 [Sóc Trăng] ĐB: 454847 1: 72330 2: 42590 3: 26544 - 70144 4: 86931 - 79675 - 09519 - 85255 - 58821 - 60418 - 11558 5: 2962 6: 6470 - 6472 - 0714 7: 1278: 47
        String[] splitByCompanyName = description.split("\\[");
        int indexPrizeName = 0;
        for (String item: splitByCompanyName) {
            // item = Cần Thơ] ĐB: 414303 1: 51374 2: 50151 3: 51102 - 31421 4: 77132 - 16282 - 27680 - 24815 - 84724 - 87059 - 08557 5: 2523 6: 6215 - 4816 - 7933 7: 2228: 06
            if (StringUtils.isEmpty(item)) {
                continue;
            }

            // result = ["Cần Thơ"," ĐB: 414303 1: 51374 2: 50151 3: 51102 - 31421 4: 77132 - 16282 - 27680 - 24815 - 84724 - 87059 - 08557 5: 2523 6: 6215 - 4816 - 7933 7: 2228: 06"]
            String[] parseIntoResult = item.split("\\]");
            if (parseIntoResult.length != 2
                    || (StringUtils.isEmpty(parseIntoResult[0])
                        && StringUtils.isEmpty(parseIntoResult[1]))
            ) {
                continue;
            }
            String companyName = parseIntoResult[0];
            String result = parseIntoResult[1];

            companyWithPrizeList.add(string2KQXSDescription(companyName, result));

            indexPrizeName++;
        }

        return companyWithPrizeList;
    }
}