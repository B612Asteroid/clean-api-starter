package org.starter.api.core.util

import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * 날짜 처리 유틸 클래스
 */
object DateUtil {
    private const val DEFAULT_DATE_FORMAT = "yyyy.MM.dd"

    /**
     * yyyy.MM.dd 포멧에 맞춘 날짜 문자열을 Date 객체로 변경한다.
     * @param dateStr
     * @return
     * @throws ParseException
     */
    @Throws(ParseException::class)
    fun getDateStringToDate(dateStr: String?): Date? {
        return getDateStringToDate(dateStr, DEFAULT_DATE_FORMAT)
    }

    /**
     * 데이트 포멧에 맞춘 날짜 문자열을 날짜 객체로 변경해준다.
     * @param dateStr
     * @param formatStr
     * @return
     * @throws ParseException
     */
    @Throws(ParseException::class)
    fun getDateStringToDate(dateStr: String?, formatStr: String): Date? {
        val dateFormat: DateFormat = SimpleDateFormat(formatStr)
        return dateFormat.parse(dateStr)
    }

    /**
     * 날짜 객체를 날짜 포멧에 맞춘 String으로 변경한다.
     * @param date
     * @param formatStr
     * @return
     */
    fun getDateToDateString(date: Date?, formatStr: String): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        if (date == null) {
            return ""
        }
        cal.setTime(date)
        val dateFormat: DateFormat = SimpleDateFormat(formatStr)
        return dateFormat.format(date)
    }

    /**
     * 날짜 객체를 yyyy.MM.dd 형 String으로 변환한다.
     * @param date
     * @return
     */
    fun getDateToDateString(date: Date?): String {
        return getDateToDateString(date, DEFAULT_DATE_FORMAT)
    }

    val currentDateString: String
        get() = getDateToDateString(Date())

    val currentTimeStamp: Timestamp
        get() {
            val zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
            return Timestamp.valueOf(zonedDateTime.toLocalDateTime())
        }

    /**
     * 초를 입력받아서 시간으로 바꾼다.
     * @param sec
     * @return
     */
    fun getSecendIntToTime(sec: Int): String {
        val hour = sec / (60 * 60)
        val minute = sec / 60 - (hour * 60)
        val second = sec % 60

        var hourStr = ""
        var minuteStr = "00"
        var secondStr = "00"
        if (hour > 0) {
            hourStr = hour.toString() + ":"
        }
        if (minute > 0) {
            minuteStr = TypeUtil.stringValue(minute)
            if (minute < 10) {
                minuteStr = "0" + minuteStr
            }
        }

        if (second > 0) {
            secondStr = TypeUtil.stringValue(second)
            if (second < 10) {
                secondStr = "0" + secondStr
            }
        }
        return hourStr + minuteStr + ":" + secondStr
    }

    val currentDate: String
        /**
         * 현재 날짜를 yyyy-MM-dd String 형으로 변환한다.
         * @param
         * @return
         */
        get() {
            val date = LocalDate.now()

            val year = date.getYear() // YYYY
            val month = date.getMonthValue() // MM
            val day = date.getDayOfMonth() // DD

            val currentDate = String.format("%d-%02d-%02d", year, month, day) // ex) 2023-03-25

            return currentDate
        }
}
