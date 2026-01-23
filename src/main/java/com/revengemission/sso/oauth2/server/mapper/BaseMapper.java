package com.revengemission.sso.oauth2.server.mapper;

import org.mapstruct.Named;

public interface BaseMapper {
    // 全局字符串转 Long 转换方法
    @Named("stringToLong")
    default Long stringToLong(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
