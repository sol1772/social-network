package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import java.time.format.DateTimeFormatter;

public class Utils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private Utils() {
        throw new IllegalStateException("Default constructor for utility class is non instantiable");
    }

}
