package com.example.heroku.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CommonUtils {
    public boolean isEmptyOrWhiteSpaces(String source) {
        return StringUtils.isEmpty(source) && StringUtils.isBlank(source);
    }
}
