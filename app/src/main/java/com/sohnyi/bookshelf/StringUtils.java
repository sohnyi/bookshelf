package com.sohnyi.bookshelf;

public class StringUtils {
    public static boolean lastCharIs(StringBuilder sb, char c) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == c;
    }

    public static boolean lastCharIsWhitespace(StringBuilder sb) {
        return lastCharIs(sb, ' ');
    }

    public static String removeCharEndWith(String s, String suffix) {
        if (s.endsWith(suffix)) {
            return s.substring(0, s.lastIndexOf(suffix));
        } else {
            return s;
        }
    }
}
