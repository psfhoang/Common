package vn.siplab.medical.education.common.until;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateUtil {

  private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

  public static Locale getLocaleDefault() {
    return new Locale("vi", "VI");
  }

  public static ZoneId getDefaultZoneId() {
    return getDefaultTimeZone().toZoneId();
  }

  public static TimeZone getDefaultTimeZone() {
    return TimeZone.getDefault();
  }

  public static LocalDate getMinDate() {
    return truncateYear(LocalDate.now()).withYear(1870);
  }

  public static LocalDate getMaxDate() {
    return truncateYear(LocalDate.now()).plusYears(100);
  }

  public static Integer getDaysBetween(ZonedDateTime from, ZonedDateTime to) {
    if (from == null || to == null) {
      return null;
    }

    return (int) ChronoUnit.DAYS.between(from, to);
  }

  public static Integer getMonthsBetween(ZonedDateTime from, ZonedDateTime to) {
    if (from == null || to == null) {
      return null;
    }

    return (int) ChronoUnit.MONTHS.between(from, to);
  }

  public static Integer getAgeMonths(ZonedDateTime from, ZonedDateTime to) {
    if (from == null || to == null) {
      return null;
    }

    Integer months = (int) ChronoUnit.MONTHS.between(from, to);
    if (months == 0) {
      return 1;
    }

    return months;
  }

  public static Integer getYearsBetween(ZonedDateTime from, ZonedDateTime to) {
    if (from == null || to == null) {
      return null;
    }

    return (int) ChronoUnit.YEARS.between(from, to);
  }

  public static Integer getAge(ZonedDateTime from, ZonedDateTime to) {
    if (from == null || to == null) {
      return null;
    }

    Integer age = getYearsBetween(truncateYear(from), to);

    if (age == 0) {
      return 1;
    }

    return age;
  }

  public static LocalDate truncateYear(LocalDate date) {
    return truncateMonth(date).withMonth(1);
  }

  public static LocalDate truncateMonth(LocalDate date) {
    return date.withDayOfMonth(1);
  }

  public static ZonedDateTime truncateYear(ZonedDateTime date) {
    return truncateMonth(date).withMonth(1);
  }

  public static ZonedDateTime truncateMonth(ZonedDateTime date) {
    return truncateDate(date).withDayOfMonth(1);
  }

  public static ZonedDateTime truncateDate(ZonedDateTime date) {
    if (date == null) {
      date = getNow();
    }

    return date.truncatedTo(ChronoUnit.DAYS);
  }

  public static ZonedDateTime truncateHour(ZonedDateTime date) {
    return date.truncatedTo(ChronoUnit.HOURS);
  }

  public static ZonedDateTime getNow() {
    return truncateSecond(ZonedDateTime.now());
  }

  public static LocalDate getToday() {
    return LocalDate.now();
  }

  public static ZonedDateTime getZonedDateTimeToday() {
    return truncateDate(ZonedDateTime.now());
  }

  public static ZonedDateTime truncateMinute(ZonedDateTime date) {
    return date.truncatedTo(ChronoUnit.MINUTES);
  }

  public static ZonedDateTime truncateSecond(ZonedDateTime date) {
    return date.truncatedTo(ChronoUnit.SECONDS);
  }

  public static boolean isValidDate(ZonedDateTime date) {
    if (date == null) {
      return false;
    }

    return isValidDate(date.toLocalDate());
  }

  public static boolean isValidDate(LocalDateTime date) {
    if (date == null) {
      return false;
    }

    return isValidDate(date.toLocalDate());
  }

  public static boolean isValidDate(LocalDate date) {
    if (date == null) {
      return false;
    }

    LocalDate minDate = getMinDate();
    LocalDate maxDate = getMaxDate();
    return date.compareTo(minDate) >= 0 && date.compareTo(maxDate) <= 0;
  }

  public static int truncatedHourCompareTo(ZonedDateTime date1, ZonedDateTime date2) {
    return truncateHour(date1).compareTo(truncateHour(date2));
  }

  public static int compareTo(LocalDate date1, LocalDate date2) {
    return date1.compareTo(date2);
  }

  public static ZonedDateTime parseValidZonedDateTime(String dateStr) {
    return parseValidZonedDateTime(dateStr, null);
  }

  public static ZonedDateTime parseValidZonedDateTime(String dateStr, String parsePattern) {
    if (dateStr == null) {
      return null;
    }

    ZonedDateTime dateTime;

    if (parsePattern != null) {
      dateTime = parseZonedDateTime(dateStr, parsePattern);

      if (isValidDate(dateTime)) {
        return dateTime;
      }

      return null;
    }

    Map<String, String> datePatterns = getDatePattern();
    Map<String, String> timePatterns = getTimePattern();

    Map<String, String> zonePatterns = new HashMap<>();
    zonePatterns.put("[-+]([0-1][0-9]|2[0-4]):[0-5][0-9]", "XXX");// +07:00
    zonePatterns.put("[-+]([0-1][0-9]|2[0-4])[0-5][0-9]", "Z");// +0700

    for (String zonePattern : zonePatterns.keySet()) {
      for (String timePattern : timePatterns.keySet()) {
        for (String datePattern : datePatterns.keySet()) {
          String pattern = "^" + datePattern + timePattern + zonePattern + "$";

          if (!dateStr.matches(pattern)) {
            continue;
          }

          String format = datePatterns.get(datePattern) + timePatterns.get(timePattern) + zonePatterns.get(zonePattern);

          dateTime = parseZonedDateTime(dateStr, format);
          if (isValidDate(dateTime)) {
            return dateTime;
          }
        }
      }
    }

    return getZonedDateTime(parseValidDateTime(dateStr));
  }

  public static LocalDateTime parseValidDateTime(String dateStr) {
    return parseValidDateTime(dateStr, null);
  }

  public static LocalDateTime parseValidDateTime(String dateStr, String parsePattern) {
    if (dateStr == null) {
      return null;
    }

    LocalDateTime dateTime;

    if (parsePattern != null) {
      dateTime = parseDateTime(dateStr, parsePattern);

      if (isValidDate(dateTime)) {
        return dateTime;
      }

      return null;
    }

    Map<String, String> timePatterns = getTimePattern();
    Map<String, String> datePatterns = getDatePattern();

    for (String timePattern : timePatterns.keySet()) {
      for (String datePattern : datePatterns.keySet()) {
        String pattern = "^" + datePattern + timePattern + "$";

        if (!dateStr.matches(pattern)) {
          continue;
        }

        dateTime = parseDateTime(dateStr, datePatterns.get(datePattern) + timePatterns.get(timePattern));
        if (isValidDate(dateTime)) {
          return dateTime;
        }
      }
    }

    return getLocalDateTime(parseValidDate(dateStr));
  }

  public static LocalDate parseValidDate(String dateStr) {
    return parseValidDate(dateStr, null);
  }

  public static LocalDate parseValidDate(String dateStr, String parsePattern) {
    if (dateStr == null) {
      return null;
    }

    LocalDate date;

    if (parsePattern != null) {
      date = parse(dateStr, parsePattern);

      if (isValidDate(date)) {
        return date;
      }

      return null;
    }

    Map<String, String> datePatterns = getDatePattern();

    for (String datePattern : datePatterns.keySet()) {
      String pattern = "^" + datePattern + "$";

      if (!dateStr.matches(pattern)) {
        continue;
      }

      date = parse(dateStr, datePatterns.get(datePattern));
      if (isValidDate(date)) {
        return date;
      }
    }

    return null;
  }

  public static ZonedDateTime parseZonedDateTime(String d, String format) {
    if (d.equals("")|| format.equals("")) {
      return null;
    }

    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern(format)
        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
        .toFormatter();

    try {
      return ZonedDateTime.parse(d, formatter);
    } catch (DateTimeException e) {
      LocalDateTime dateTime = LocalDateTime.parse(d, formatter);
      return dateTime.atZone(getDefaultZoneId());
    }
  }

  public static LocalDateTime parseDateTime(String d, String format) {
    if (d == null || format == null) {
      return null;
    }

    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern(format)
        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
        .toFormatter();

    return LocalDateTime.parse(d, formatter);
  }

  public static LocalDate parse(String d, String format) {
    if (d == null || format == null) {
      return null;
    }

    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern(format)
        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
        .toFormatter();

    return LocalDate.parse(d, formatter);
  }

  public static String format(LocalDate d, String format) {
    if (d == null || format == null) {
      return null;
    }
    return DateTimeFormatter.ofPattern(format).format(d);
  }

  public static String format(LocalDateTime d, String format) {
    if (d == null || format == null) {
      return null;
    }
    return DateTimeFormatter.ofPattern(format).format(d);
  }

  public static String format(ZonedDateTime d, String format) {
    if (d == null || format == null) {
      return null;
    }
    return DateTimeFormatter.ofPattern(format).format(d);
  }

  public static String formatDateTime(LocalDateTime d) {
    return format(d, "yyyy-MM-dd'T'HH:mm:ss");
  }

  public static String formatDate(LocalDate d) {
    return format(d, "yyyy-MM-dd");
  }

  public static String formatDate(ZonedDateTime d) {
    return format(d, "yyyy-MM-dd");
  }

  public static String formatYear(LocalDate d) {
    return format(d, "yyyy");
  }

  public static Date getDate(ZonedDateTime dt) {
    if (dt == null) {
      return null;
    }

    return Date.from(dt.toInstant());
  }

  public static Date getDate(LocalDate ld) {
    if (ld == null) {
      return null;
    }

    return Date.from(ld.atStartOfDay(getDefaultZoneId()).toInstant());
  }

  public static LocalDate getLocalDate(Date dt) {
    if (dt == null) {
      return null;
    }

    return dt.toInstant().atZone(getDefaultZoneId()).toLocalDate();
  }

  public static ZonedDateTime getZonedDateTime(Date dt) {
    if (dt == null) {
      return null;
    }

    return dt.toInstant().atZone(getDefaultZoneId());
  }

  public static ZonedDateTime getZonedDateTimeWithZone(ZonedDateTime dt, ZoneId zoneId) {
    if (dt == null) {
      return null;
    }

    return dt.withZoneSameInstant(zoneId);
  }

  public static ZonedDateTime getZonedDateTimeWithDefaultZone(ZonedDateTime dt) {
    return getZonedDateTimeWithZone(dt, getDefaultZoneId());
  }

  public static ZonedDateTime getZonedDateTime(LocalDateTime ld) {
    if (ld == null) {
      return null;
    }

    return ld.atZone(getDefaultZoneId());
  }

  public static ZonedDateTime getZonedDateTime(LocalDate ld) {
    if (ld == null) {
      return null;
    }

    return ld.atStartOfDay(getDefaultZoneId());
  }

  public static ZonedDateTime getZonedDateTime(Long epochMilli) {
    if (epochMilli == null) {
      return null;
    }

    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), getDefaultZoneId());
  }

  public static LocalDateTime getLocalDateTime(LocalDate ld) {
    if (ld == null) {
      return null;
    }

    return ld.atStartOfDay();
  }

  public static XMLGregorianCalendar getXMLGregorianCalendar(ZonedDateTime dt, boolean timezone) {
    if (dt == null) {
      return null;
    }

    GregorianCalendar c = GregorianCalendar.from(dt);
    try {
      XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
      if (!timezone) {
        date.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
      }

      return date;
    } catch (DatatypeConfigurationException e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException();
    }
  }

  public static XMLGregorianCalendar getXMLGregorianCalendar(LocalDate ld) {
    return getXMLGregorianCalendar(getZonedDateTime(ld), false);
  }

  private static Map<String, String> getDatePattern() {
    String yyyyPattern = "[1-2][0-9]{3}";
    String yyyy = "yyyy";
    String ddPattern = "[0-3][0-9]";
    String dd = "dd";
    String dPattern = "[1-9]";
    String d = "d";
    String mmPattern = "(0[1-9]|1[0-2])";
    String mm = "MM";
    String mPattern = "[1-9]";
    String m = "M";
    String yyPattern = "[0-9]{2}";
    String yy = "yy";

    String[] separateChars = {"", "/", "-", "\\."};

    Map<String, String> parsePatterns = new HashMap<>();

    for (String separateChar : separateChars) {
      parsePatterns.put(yyyyPattern, yyyy);// yyyy
      parsePatterns.put(yyyyPattern + separateChar + mmPattern + separateChar + ddPattern,
          yyyy + separateChar + mm + separateChar + dd);// yyyy/MM/dd
      parsePatterns.put(yyyyPattern + separateChar + mmPattern, yyyy + separateChar + mm);// yyyy/MM
      parsePatterns.put(yyyyPattern + separateChar + mPattern, yyyy + separateChar + m);// yyyy/M

      parsePatterns.put(ddPattern + separateChar + mmPattern + separateChar + yyyyPattern,
          dd + separateChar + mm + separateChar + yyyy);// dd/MM/yyyy
      parsePatterns.put(dPattern + separateChar + mmPattern + separateChar + yyyyPattern,
          d + separateChar + mm + separateChar + yyyy);// d/MM/yyyy
      parsePatterns.put(ddPattern + separateChar + mPattern + separateChar + yyyyPattern,
          dd + separateChar + m + separateChar + yyyy);// dd/M/yyyy
      parsePatterns.put(dPattern + separateChar + mPattern + separateChar + yyyyPattern,
          d + separateChar + m + separateChar + yyyy);// d/M/yyyy
      parsePatterns.put(mmPattern + separateChar + yyyyPattern, mm + separateChar + yyyy);// MM/yyyy
      parsePatterns.put(mPattern + separateChar + yyyyPattern, m + separateChar + yyyy);// M/yyyy

      parsePatterns.put(ddPattern + separateChar + mmPattern + separateChar + yyPattern,
          dd + separateChar + mm + separateChar + yy);// dd/MM/yy
      parsePatterns.put(dPattern + separateChar + mmPattern + separateChar + yyPattern,
          d + separateChar + mm + separateChar + yy);// d/MM/yy
      parsePatterns.put(ddPattern + separateChar + mPattern + separateChar + yyPattern,
          dd + separateChar + m + separateChar + yy);// dd/M/yy
      parsePatterns.put(dPattern + separateChar + mPattern + separateChar + yyPattern,
          d + separateChar + m + separateChar + yy);// d/M/yy
      parsePatterns.put(mmPattern + separateChar + yyPattern, mm + separateChar + yy);// MM/yy
      parsePatterns.put(mPattern + separateChar + yyPattern, m + separateChar + yy);// M/yy
    }

    return parsePatterns;
  }

  private static Map<String, String> getTimePattern() {
    Map<String, String> timePatterns = new HashMap<>();
    timePatterns.put("T([0-1][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9]", "'T'HH:mm:ss");
    timePatterns.put("T([0-1][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9].[0-9]{1,3}", "'T'HH:mm:ss.SSS");
    timePatterns.put(" ([0-1][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9]", " HH:mm:ss");
    timePatterns.put(" ([0-1][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9].[0-9]{1,3}", " HH:mm:ss.SSS");
    timePatterns.put("T([0-1][0-9]|2[0-4]):[0-5][0-9]", "'T'HH:mm");
    timePatterns.put(" ([0-1][0-9]|2[0-4]):[0-5][0-9]", " HH:mm");

    return timePatterns;
  }
}