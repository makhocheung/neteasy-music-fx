package pub.cellebi.neteasyfx.utils;

import com.fasterxml.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Util {

    public static final JsonMapper MAPPER = new JsonMapper();

    public static String parseTime(long timeStamp) {
        var time = LocalDateTime.ofEpochSecond(timeStamp/1000, 0, ZoneOffset.ofHours(8)).toLocalDate();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(time);
    }
}
