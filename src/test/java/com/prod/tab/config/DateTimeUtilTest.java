package com.prod.tab.config;

import com.prod.tap.config.DateTimeUtil;
import com.prod.tap.config.DateTimeUtil;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class DateTimeUtilTest {

    @Test
    public void testTryParseDate() {
        LocalDate date = DateTimeUtil.tryParseDate("1/31/2017");
        assertNotNull(date);
        assertEquals(2017, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(31, date.getDayOfMonth());

        date = DateTimeUtil.tryParseDate("2017-09-01");
        assertNull(date);
    }

    @Test
    public void testDateToString() {
        LocalDate date = DateTimeUtil.tryParseDate("1/31/2017");
        assertNotNull(date);

        assertEquals("1/31/2017", DateTimeUtil.toString(date));
        assertEquals("2/1/2017", DateTimeUtil.toString(date.plusDays(1)));
        assertEquals("2017-1-31", DateTimeUtil.toString("yyyy-M-d", date));
    }

    @Test
    public void testTryParseDateTime() {

        LocalDateTime date = DateTimeUtil.tryParseDateTime("1/31/2019 1:56:51.023");
        assertNotNull(date);
        assertEquals(2019, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(31, date.getDayOfMonth());
        assertEquals(1, date.getHour());
        assertEquals(56, date.getMinute());
        assertEquals(51, date.getSecond());
        assertEquals(23000000, date.getNano());

        date = DateTimeUtil.tryParseDateTime("2017-09-01");
        assertNull(date);

        date = DateTimeUtil.tryParseDateTime("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'+00:00'", "2019-09-10T02:22:37.000000+00:00");
        assertEquals(date, DateTimeUtil.tryParseDateTime("yyyy-MM-dd'T'HH:mm:ss", "2019-09-10T02:22:37"));
    }

    @Test
    public void testDateTimeToString() {
        LocalDateTime date = DateTimeUtil.tryParseDateTime("1/31/2019 1:56:51.023");
        assertNotNull(date);

        assertEquals("1/31/2019 1:56:51.023", DateTimeUtil.toString(date));
        assertEquals("2019-1-31 1:56:51.023", DateTimeUtil.toString("yyyy-M-d H:m:s.SSS", date));
    }

    @Test
    public void testLocalDateTimeToUnixTimestamp() {
        LocalDateTime time = LocalDateTime.now();
        long timestamp = DateTimeUtil.getTimeStamp(time);
        LocalDateTime convertedTime = DateTimeUtil.getDateTime(timestamp);
        assertEquals(time, convertedTime);
    }
}
