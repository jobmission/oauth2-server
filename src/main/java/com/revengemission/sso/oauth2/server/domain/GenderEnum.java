package com.revengemission.sso.oauth2.server.domain;

public enum GenderEnum {

    /**
     * 男
     */
    MALE("男"),
    /**
     * 女
     */
    FEMALE("女"),
    /**
     * 未知
     */
    UNKNOWN("未知");

    private String meaning;

    public String getMeaning() {
        return meaning;
    }

    GenderEnum() {
    }

    GenderEnum(String meaning) {
        this.meaning = meaning;
    }
}
