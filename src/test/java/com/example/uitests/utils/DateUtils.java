package com.example.uitests.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
  public static final LocalDate CURRENT_DATE = LocalDate.now();
  public static final LocalDate ONE_WEEK_FROM_NOW_DATE = CURRENT_DATE.plusWeeks(1);
  public static final String DAY_PATTERN = "d";
  public static final String MONTH_AND_DAY_PATTERN = "MMM d";

  public static String formatDateAsPattern(LocalDate date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return date.format(formatter);
  }

}
