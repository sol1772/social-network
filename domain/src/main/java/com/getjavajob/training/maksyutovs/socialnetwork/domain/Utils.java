package com.getjavajob.training.maksyutovs.socialnetwork.domain;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Utils extends XmlAdapter<String, LocalDate> {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Override
    public LocalDate unmarshal(String s) {
        return LocalDate.parse(s);
    }

    @Override
    public String marshal(LocalDate localDate) {
        return localDate.toString();
    }

}
