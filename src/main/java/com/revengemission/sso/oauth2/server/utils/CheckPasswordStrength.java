package com.revengemission.sso.oauth2.server.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 检测密码强度
 * refer
 * https://github.com/venshine/CheckPasswordStrength
 * Level（级别）
 * 0-3 : [easy]
 * 4-6 : [medium]
 * 7-9 : [strong]
 * 10-12 : [very strong]
 * >12 : [extremely strong]
 */
public class CheckPasswordStrength {

    public enum LEVEL {
        /**
         * 0-3
         */
        EASY,
        /**
         * 4-6
         */
        MEDIUM,
        /**
         * 7-9
         */
        STRONG,
        /**
         * 10-12
         */
        VERY_STRONG,
        /**
         * >12
         */
        EXTREMELY_STRONG
    }

    private static final int NUM = 1;
    private static final int SMALL_LETTER = 2;
    private static final int CAPITAL_LETTER = 3;
    private static final int OTHER_CHAR = 4;

    /**
     * Simple password dictionary
     */
    private final static String[] DICTIONARY = {"123456", "password", "123456789", "12345678",
        "12345", "111111", "1234567", "sunshine", "qwerty",
        "iloveyou", "princess", "admin", "welcome", "666666", "abc123",
        "football", "123123", "monkey", "654321", "！@#$%^&*", "charlie",
        "aa123456", "aa123456", "donald", "password1", "qwerty123", "147258369",
        "987654321", "1qaz2wsx", "asdfghjkl", "1q2w3e4r", "1234abcd", "3.1415926"
    };

    /**
     * Check character's type, includes num, capital letter, small letter and other character.
     *
     * @param c
     * @return
     */
    private static int checkCharacterType(char c) {
        if (c >= 48 && c <= 57) {
            return NUM;
        }
        if (c >= 65 && c <= 90) {
            return CAPITAL_LETTER;
        }
        if (c >= 97 && c <= 122) {
            return SMALL_LETTER;
        }
        return OTHER_CHAR;
    }

    /**
     * Count password's number by different type
     *
     * @param passwd
     * @param type
     * @return
     */
    private static int countLetter(String passwd, int type) {
        int count = 0;
        if (null != passwd && passwd.length() > 0) {
            for (char c : passwd.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Check password's strength
     *
     * @param passwd
     * @return strength level
     */
    public static int check(String passwd) {
        if (StringUtils.isBlank(passwd)) {
            throw new IllegalArgumentException("password is empty");
        }
        passwd = StringUtils.trimToEmpty(passwd);
        int len = passwd.length();
        int level = 0;

        // increase points
        if (len >= 6) {
            level++;
        }
        if (countLetter(passwd, NUM) > 0) {
            level++;
        }
        if (countLetter(passwd, SMALL_LETTER) > 0) {
            level++;
        }
        if (len > 4 && countLetter(passwd, CAPITAL_LETTER) > 0) {
            level++;
        }
        if (len > 6 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }

        if (len > 4 && countLetter(passwd, NUM) > 0 && countLetter(passwd, SMALL_LETTER) > 0
            || countLetter(passwd, NUM) > 0 && countLetter(passwd, CAPITAL_LETTER) > 0
            || countLetter(passwd, NUM) > 0 && countLetter(passwd, OTHER_CHAR) > 0
            || countLetter(passwd, SMALL_LETTER) > 0 && countLetter(passwd, CAPITAL_LETTER) > 0
            || countLetter(passwd, SMALL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0
            || countLetter(passwd, CAPITAL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }

        if (len > 6 && countLetter(passwd, NUM) > 0 && countLetter(passwd, SMALL_LETTER) > 0
            && countLetter(passwd, CAPITAL_LETTER) > 0 || countLetter(passwd, NUM) > 0
            && countLetter(passwd, SMALL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0
            || countLetter(passwd, NUM) > 0 && countLetter(passwd, CAPITAL_LETTER) > 0
            && countLetter(passwd, OTHER_CHAR) > 0 || countLetter(passwd, SMALL_LETTER) > 0
            && countLetter(passwd, CAPITAL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }

        if (len > 8 && countLetter(passwd, NUM) > 0 && countLetter(passwd, SMALL_LETTER) > 0
            && countLetter(passwd, CAPITAL_LETTER) > 0 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }

        if (len > 6 && countLetter(passwd, NUM) >= 3 && countLetter(passwd, SMALL_LETTER) >= 3
            || countLetter(passwd, NUM) >= 3 && countLetter(passwd, CAPITAL_LETTER) >= 3
            || countLetter(passwd, NUM) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
            || countLetter(passwd, SMALL_LETTER) >= 3 && countLetter(passwd, CAPITAL_LETTER) >= 3
            || countLetter(passwd, SMALL_LETTER) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
            || countLetter(passwd, CAPITAL_LETTER) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2) {
            level++;
        }

        if (len > 8 && countLetter(passwd, NUM) >= 2 && countLetter(passwd, SMALL_LETTER) >= 2
            && countLetter(passwd, CAPITAL_LETTER) >= 2 || countLetter(passwd, NUM) >= 2
            && countLetter(passwd, SMALL_LETTER) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2
            || countLetter(passwd, NUM) >= 2 && countLetter(passwd, CAPITAL_LETTER) >= 2
            && countLetter(passwd, OTHER_CHAR) >= 2 || countLetter(passwd, SMALL_LETTER) >= 2
            && countLetter(passwd, CAPITAL_LETTER) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2) {
            level++;
        }

        if (len > 10 && countLetter(passwd, NUM) >= 2 && countLetter(passwd, SMALL_LETTER) >= 2
            && countLetter(passwd, CAPITAL_LETTER) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2) {
            level++;
        }

        if (countLetter(passwd, OTHER_CHAR) >= 3) {
            level++;
        }
        if (countLetter(passwd, OTHER_CHAR) >= 6) {
            level++;
        }

        if (len > 12) {
            level++;
            if (len >= 16) {
                level++;
            }
        }

        // decrease points
        if (countLetter(passwd, NUM) == len || countLetter(passwd, SMALL_LETTER) == len
            || countLetter(passwd, CAPITAL_LETTER) == len) {
            level--;
        }

        // ababab
        if (len % 3 == 0) {
            String part1 = passwd.substring(0, len / 3);
            String part2 = passwd.substring(len / 3, len / 3 * 2);
            String part3 = passwd.substring(len / 3 * 2);
            if (part1.equals(part2) && part2.equals(part3)) {
                level--;
            }
        }

        // dictionary
        if (null != DICTIONARY && DICTIONARY.length > 0) {
            for (int i = 0; i < DICTIONARY.length; i++) {
                if (DICTIONARY[i].indexOf(passwd.toLowerCase()) >= 0) {
                    level--;
                    break;
                }
            }
        }

        if (len < 6) {
            level--;
            if (len <= 4) {
                level--;
                if (len <= 3) {
                    level = 0;
                }
            }
        }

        if (passwd.replace(passwd.charAt(0), ' ').trim().length() == 0) {
            level = 0;
        }

        if (level < 0) {
            level = 0;
        }

        return level;
    }

    /**
     * Get password strength level, includes easy, midium, strong, very strong, extremely strong
     *
     * @param passwd
     * @return
     */
    public static LEVEL getPasswordLevel(String passwd) {
        int level = check(passwd);
        switch (level) {
            case 0:
            case 1:
            case 2:
            case 3:
                return LEVEL.EASY;
            case 4:
            case 5:
            case 6:
                return LEVEL.MEDIUM;
            case 7:
            case 8:
            case 9:
                return LEVEL.STRONG;
            case 10:
            case 11:
            case 12:
                return LEVEL.VERY_STRONG;
            default:
                return LEVEL.EXTREMELY_STRONG;
        }
    }

}
