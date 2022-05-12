package com.example.natour21.chat.room.typeconverters;

import androidx.room.TypeConverter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @TypeConverter
    public static OffsetDateTime offsetDateTimeFromString(String string){
        return OffsetDateTime.from(formatter.parse(string));
    }

    @TypeConverter
    public static String stringFromOffsetDateTime(OffsetDateTime offsetDateTime){
        return offsetDateTime.format(formatter);
    }
}
