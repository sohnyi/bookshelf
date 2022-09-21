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

    public static void main(String[] args) {
        String info = "作者:蔡崇达出版社:浙江文艺出版社/广州出版社出品方:果麦文化出版年:2022-9-5页数:368定价:59.80元装帧:精装ISBN:9787533969608,";
        System.out.println(removeCharEndWith(info, ","));
    }
}
