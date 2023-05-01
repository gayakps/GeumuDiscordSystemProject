package gaya.pe.kr.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static long getTimeDiffTwoDay(Date before, Date after) {
        return ( ( ( ( after.getTime() - before.getTime()) / 1000 ) / 60 ) / 60 ) / 24 ;
    }

    public static long getTimeDiffDay(Date date) {
        Date now = new Date();
        return ( ( ( ( now.getTime() - date.getTime()) / 1000 ) / 60 ) / 60 ) / 24 ;
    }

    public static long getTimeDiffHour(Date date) {
        Date now = new Date();
        return ( ( ( now.getTime() - date.getTime()) / 1000 ) / 60 ) / 60 ;
    }

    public static long getTimeDiffMinute(Date date) {
        Date now = new Date();
        return ( ( ( now.getTime() - date.getTime()) / 1000 ) / 60 ) ;
    }

    /**
     *
     * @param date 정해진 시간
     * @return 현재 시간과 파라미터 값을 대조해서 시간 차를 알려줌
     * 만일 음수 값일 경우 parameter 값이 현재 시간보다 뒤에있음
     * ex) -101 @param date 까지 101초 남았다는 뜻
     * 양수 값이면 이미 date 날짜보다 지났다는 뜻
     *
     */
    public static long getTimeDiffSec(Date date) {
        Date now = new Date();
        return (  ( now.getTime() - date.getTime()) / 1000 ) ;
    }

    public static String getTimeMinSec(int time) {
        if ( time >= 60 ) {
            int min = time/60;
            int sec = time%60;
            if ( sec != 0 ) {
                return String.format("%d:%d", min, sec);
            } else {
                return String.format("%d:00", min);
            }
        } else {
            return String.format("00:%d", time);
        }
    }

    public static Date getAfterMinTime(int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    public static Date getAfterSecTime(int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, sec);
        return calendar.getTime();
    }

    public static Date getModifyDayTime(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
}
