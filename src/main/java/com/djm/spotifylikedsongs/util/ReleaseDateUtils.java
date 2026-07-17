package com.djm.spotifylikedsongs.util;

import java.time.LocalDate;
import java.time.YearMonth;

public class ReleaseDateUtils {
    public static String trimReleaseDate(String relDate) {
        if (relDate == null || relDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Release date cannot be empty");
        }
        String relDateTrim = relDate.trim();

        if (relDateTrim.length() == 10) {
            if (relDateTrim.substring(5, 10).equals("01-01")) {
                return relDateTrim.substring(0, 4);
            }
        }
        return relDateTrim;
    }

    public static LocalDate convertStringToDate(String d){
        if (d.length() == 4){
            return LocalDate.of(Integer.parseInt(d), 1, 1);
        } else if (d.length() == 7){
            return LocalDate.of(
                    Integer.parseInt(d.substring(0,4)),
                    Integer.parseInt(d.substring(5,7)),
                    1
            );
        } else {
            return LocalDate.of(
                    Integer.parseInt(d.substring(0,4)),
                    Integer.parseInt(d.substring(5,7)),
                    Integer.parseInt(d.substring(8,10))
            );
        }
    }

    public static String makeDateStringReadable(String d){
        if (d.length() >= 10){
            LocalDate date = LocalDate.parse(d);
            return formatFullDate(date);
        } else if (d.length() >= 7){
            YearMonth ym = YearMonth.parse(d);
            return formatYearMonth(ym);
        } else {
            return d;
        }
    }

    private static String formatFullDate(LocalDate date) {
        int day = date.getDayOfMonth();
        String month = date.getMonth().name();
        month = month.charAt(0) + month.substring(1).toLowerCase();
        return getOrdinal(day) + " " + month + " " + date.getYear();
    }

    private static String formatYearMonth(YearMonth ym){
        String month = ym.getMonth().name();
        month = month.charAt(0) + month.substring(1).toLowerCase();
        return month + " " + ym.getYear();
    }

    private static String getOrdinal(int n){
        if (n >= 11 && n <= 13){
            return n + "th";
        }
        return switch (n % 10){
            case 1 -> n + "st";
            case 2 -> n + "nd";
            case 3 -> n + "rd";
            default -> n + "th";
        };
    }

}
