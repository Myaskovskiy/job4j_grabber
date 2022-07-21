package ru.job4j;

import java.time.LocalDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        String str = parse;
        int y = Integer.parseInt(str.substring(0, 4));
        int m = Integer.parseInt(str.substring(5, 7));
        int d = Integer.parseInt(str.substring(8, 10));
        int h = Integer.parseInt(str.substring(11, 13));
        int min = Integer.parseInt(str.substring(14, 16));
        int sec = Integer.parseInt(str.substring(17, 19));
        return LocalDateTime.of(y, m, d, h, min, sec);
    }
}
