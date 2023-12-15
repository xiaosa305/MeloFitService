package com.melosound.fit.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {
	 public static Date offsetSecond(Date date, long seconds) {
		 LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		 LocalDateTime offsetDateTime = localDateTime.plusSeconds(seconds);
		 return Date.from(offsetDateTime.atZone(ZoneId.systemDefault()).toInstant());
	 }
}
