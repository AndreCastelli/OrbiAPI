package base.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateTimeUtil {

    LocalDate date;
    LocalTime time;
    String dockConnectorDateFormat = "yyyy-MM-dd";

    public DateTimeUtil() {
        date = LocalDate.now();
        time = LocalTime.now();
    }

    public String getDateFormattedToDockConnector() {
        date = LocalDate.now().minusDays(1);

        return date.format(formatDate(dockConnectorDateFormat));
    }

    public String getCurrentDateFormattedToDockConnector() {
        return date.format(formatDate(dockConnectorDateFormat));
    }

    public String getDateFormatted() {
        return date.format(formatDate("yyyyMMdd"));
    }

    public String getDateFormattedToValidationDateField() {
        Date dateNow = new Date(System.currentTimeMillis());
        Instant instant = dateNow.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return localDateTime + "Z";
    }

    public String getDateFormattedIso8601() {
        DateFormat dateNow = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dateNow.format(new Date());
    }

    public String getTImeFormatted() {
        return time.format(formatDate("HHmm"));
    }

    public String getPreviousDateFormatted() {
        date = LocalDate.now().minusDays(1);

        return date.format(formatDate("ddMMyyyy"));
    }

    public String getPreviousDateFormatted(String methodOfSubtract, int timeToSubtract) {
        LocalDateTime localDate = LocalDateTime.now();
        if (methodOfSubtract.equalsIgnoreCase("days")){
            localDate = localDate.minusDays(timeToSubtract);
        } else {
            localDate = localDate.minusHours(timeToSubtract);
        }
        return localDate.toString();
    }

    public String getPreviousDateFormatted(int daysToSubtract) {
        LocalDateTime localDate = LocalDateTime.now();
        localDate = localDate.minusHours(daysToSubtract);
        return localDate.toString();
    }

    public String addOrSubtractDaysInADate(int numberOfDays, String formatter) {
        date = LocalDate.now().plusDays(numberOfDays);

        return date.format(formatDate(formatter));
    }

    public LocalDate addOrSubtractLocalDate(int numberOfDays, String formatter) {
        date = LocalDate.now().plusDays(numberOfDays);

        return LocalDate.parse(date.format(DateTimeFormatter.ofPattern(formatter)));
    }

    public String addOrSubtractMonthsInADate(int numberOfMonths, String formatter) {
        date = LocalDate.now().plusMonths(numberOfMonths);

        return date.format(formatDate(formatter));
    }

    public String addOrSubtractYearsInADate(int numberOfYears, String formatter) {
        date = LocalDate.now().plusYears(numberOfYears);

        return date.format(formatDate(formatter));
    }

    public String getCurrentDate(String formatter) {
        return date.format(formatDate(formatter));
    }

    public DateTimeFormatter formatDate(String formatter) {
        return DateTimeFormatter.ofPattern(formatter);
    }

    public String formatAdate(String dateString, String currentFormat, String desiredFormat) {
        DateTimeFormatter currformatter = DateTimeFormatter.ofPattern(currentFormat);
        date = LocalDate.parse(dateString, currformatter);

        return date.format(formatDate(desiredFormat));
    }

    public String randomBirthday(int startYear, int endYear) {
        int day = MathUtil.minAndMaxRandomNumber(MathUtil.ONE, MathUtil.TWENTY_EIGHT);
        int month = MathUtil.minAndMaxRandomNumber(MathUtil.ONE, MathUtil.TWELVE);
        int year = MathUtil.minAndMaxRandomNumber(startYear, endYear);
        date = LocalDate.of(year, month, day);

        return date.format(formatDate(dockConnectorDateFormat));
    }

    public String getTheLastDayOfTheMonth(int month, String formatter) {
        LocalDate localDate = LocalDate.now().withMonth(month).with(TemporalAdjusters.lastDayOfMonth());

        return localDate.format(DateTimeFormatter.ofPattern(formatter));
    }

    public LocalDate getLocalDateByStringDate(String stringDate) {
        return LocalDate.parse(stringDate, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
