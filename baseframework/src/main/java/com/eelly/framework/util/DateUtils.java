package com.eelly.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间的工具类
 * 
 * @author ldy
 */
public class DateUtils {

	final public static String FORMAT_STRING_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

	final public static String FORMAT_STRING_DATE_YEAR_MONTH = "yyyy-MM";

	final public static String FORMAT_STRING_DATE_DAY = "dd";

	final public static String FORMAT_STRING_DATE_ALL_TIME = "HH:mm:ss";

	final public static String FORMAT_STRING_DATE = "yyyy-MM-dd";

	final public static String FORMAT_STRING_DATE_CHINESE = "yyyy年MM月dd日";

	final public static String FORMAT_STRING_DATE_TIME_POINT = "yyyy.MM.dd HH:mm";

	final public static String FORMAT_STRING_DATE_MONTH_DAY = "MM月dd日";

	final public static String FORMAT_STRING_DATE_MONTH = "MM月";

	public static Date stringToDate(String dateString, String format) {
		try {
			return new SimpleDateFormat(format).parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date stringToDate(String date) {
		return stringToDate(date, FORMAT_STRING_DATE_TIME);
	}

	public static String dateToString(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	public static String dateToString(Date date) {
		return dateToString(date, FORMAT_STRING_DATE_TIME);
	}


	public static String calendarToString(Calendar calendar) {
		return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
	}

	public static Calendar stringToCalendar(String dateString) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtils.stringToDate(dateString));
		return calendar;
	}

	public static String toString(int year, int monthOfYear, int dayOfMonth) {
		String date = year + "-";
		if (monthOfYear < 9) {
			date += "0";
		}
		date += (monthOfYear + 1) + "-";
		if (dayOfMonth < 10) {
			date += "0";
		}
		return date + dayOfMonth;
	}

	public static String todayString() {
		return calendarToString(Calendar.getInstance());
	}

	/**
	 * 按指定的格式返回当前表示当前时间的字符串
	 * 
	 * @param format
	 * @return
	 */
	public static String nowString(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static String nowString() {
		return nowString(FORMAT_STRING_DATE_TIME);
	}

	/**
	 * 判断第一个日期是否晚于第二个日期
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return 晚于返回true，早于或等于返回false
	 */
	public static boolean isLaterThan(String firstDate, String secondDate) {
		return stringToCalendar(firstDate).getTimeInMillis() > stringToCalendar(secondDate).getTimeInMillis();
	}

	/**
	 * 获取时间戳（不带时区信息）
	 * 
	 * @return
	 */
	public static long getTimeStamp() {
		return System.currentTimeMillis();
	}

	/**
	 * 获取时间戳（不带时区信息）
	 * 
	 * @return
	 */
	public static long getTimeStamp(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day);
		return calendar.getTimeInMillis();
	}

	/**
	 * 将时间戳转化为可读的字符串
	 * 
	 * @param stamp
	 * @return
	 */
	public static String stampToString(long stamp) {
		return dateToString(new Date(stamp));
	}

	/**
	 * 将时间戳按照指定格式转化为可读的字符串
	 * 
	 * @param stamp
	 * @param format
	 * @return
	 */
	public static String stampToString(long stamp, String format) {
		return dateToString(new Date(stamp), format);
	}

	/**
	 * 返回当前是星期几。
	 * 
	 * @return 返回一个代表当期日期是星期几。
	 */
	public static String getDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		String day = "";
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				day = "星期一";
				break;
			case Calendar.TUESDAY:
				day = "星期二";
				break;
			case Calendar.WEDNESDAY:
				day = "星期三";
				break;
			case Calendar.THURSDAY:
				day = "星期四";
				break;
			case Calendar.FRIDAY:
				day = "星期五";
				break;
			case Calendar.SATURDAY:
				day = "星期六";
				break;
			case Calendar.SUNDAY:
				day = "星期日";
				break;

			default:
				break;
		}
		return day;
	}

	/**
	 * 返回当前是星期几。
	 * 
	 * @return 返回一个代表当期日期是星期几。
	 */
	public static String getDayOfWeekForPickerDialog(int year, int month, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, dayOfMonth);
		String day = "";
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				day = "周一";
				break;
			case Calendar.TUESDAY:
				day = "周二";
				break;
			case Calendar.WEDNESDAY:
				day = "周三";
				break;
			case Calendar.THURSDAY:
				day = "周四";
				break;
			case Calendar.FRIDAY:
				day = "周五";
				break;
			case Calendar.SATURDAY:
				day = "周六";
				break;
			case Calendar.SUNDAY:
				day = "周日";
				break;

			default:
				break;
		}
		return day;
	}

	/**
	 * 返回友好的时间格式,如果是今天，则返回今天，如果是昨天，就返回昨天，如果是昨天以前的，就返回yyyy-mm-dd
	 * 
	 * @param time
	 * @return
	 */
	public static String friendTime(Date time) {
		return friendTimeBase(time, FORMAT_STRING_DATE);
	}

	/**
	 * 返回友好的时间格式,如果是今天，则返回今天，如果是昨天，就返回昨天，如果是昨天以前的，就返回dd
	 * 
	 * @param time
	 * @return
	 */
	public static String friendTimeForDay(Date time) {
		return friendTimeBase(time, FORMAT_STRING_DATE_DAY);
	}

	public static String friendTimeBase(Date time, String type) {
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		target.setTime(time);
		int day_sub = Math.abs(now.get(Calendar.DAY_OF_MONTH) - target.get(Calendar.DAY_OF_MONTH));

		if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {// 如果年份一样
			if (now.get(Calendar.MONTH) == target.get(Calendar.MONTH)) {// 如果月份一样
				if (day_sub == 0) {// 如果是同一天
					// 返回今天
					return "今天";
				} else if (day_sub == 1) { //
					// 返回昨天
					return "昨天";
				}
			}
		}
		return dateToString(time, type.equals(FORMAT_STRING_DATE) ? FORMAT_STRING_DATE : FORMAT_STRING_DATE_DAY);
	}

	public static String chatFriendTime(Date time) {
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		if (null == time) {
			time = new Date();
		}
		target.setTime(time);
		int day_sub = Math.abs(now.get(Calendar.DAY_OF_MONTH) - target.get(Calendar.DAY_OF_MONTH));

		if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {// 如果年份一样
			if (now.get(Calendar.MONTH) == target.get(Calendar.MONTH)) {// 如果月份一样
				if (day_sub == 0) {// 如果是同一天
					// 返回今天
					return "今天 " + dateToString(time, "HH:mm");
				} else if (day_sub == 1) { //
					// 返回昨天
					return "昨天 " + dateToString(time, "HH:mm");
				}
			}
		}
		return dateToString(time, DateUtils.FORMAT_STRING_DATE_TIME_POINT);
	}

	/****
	 * 取得聊天的时间字符串：不是今天或昨天的时间，按formatStr格式显示
	 * @param time
	 * @param formatStr
	 * @return
	 */
	public static String chatFriendTime(Date time, String formatStr) {
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		if (null == time) {
			time = new Date();
		}
		target.setTime(time);
		int day_sub = Math.abs(now.get(Calendar.DAY_OF_MONTH) - target.get(Calendar.DAY_OF_MONTH));

		if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {// 如果年份一样
			if (now.get(Calendar.MONTH) == target.get(Calendar.MONTH)) {// 如果月份一样
				if (day_sub == 0) {// 如果是同一天
					// 返回今天
					return "今天 " + dateToString(time, "HH:mm");
				} else if (day_sub == 1) { //
					// 返回昨天
					return "昨天 " + dateToString(time, "HH:mm");
				}
			}
		}
		return dateToString(time, formatStr);
	}

	/**
	 * 如果看得消息是今年的则显示8月20日,如果看的消息是去年的则显示2013年8月20日
	 * 
	 * @param time
	 * @return
	 */
	public static String friendTimeMMDD(Date time) {
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		target.setTime(time);
		if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {
			return dateToString(time, FORMAT_STRING_DATE_MONTH_DAY);
		} else {
			return dateToString(time, FORMAT_STRING_DATE_CHINESE);
		}
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param time
	 * @return
	 */
	public static String friendly_time(Date time) {
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateToString(cal.getTime(), FORMAT_STRING_DATE);
		String paramDate = dateToString(time, FORMAT_STRING_DATE);
		if (curDate.equals(paramDate)) {
			long t = cal.getTimeInMillis() - time.getTime();
			int min = (int) (t / 60000);
			int hour = (int) (t / 3600000);
			if (min == 0)
				ftime = Math.max(t / 1000, 1) + "秒前";
			else if (hour == 0)
				ftime = Math.max(min, 1) + "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			long t = cal.getTimeInMillis() - time.getTime();
			int min = (int) (t / 60000);
			int hour = (int) (t / 3600000);
			if (min == 0)
				ftime = Math.max(t / 1000, 1) + "秒前";
			else if (hour == 0)
				ftime = Math.max(min, 1) + "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2) {
			ftime = dateToString(time, FORMAT_STRING_DATE);
		}
		return ftime;
	}

	/**
	 * 返回友好的时间格式,如果是本月，就返回本月，否则返回yyyy-MM格式
	 * 
	 * @param time 时间
	 * @param type 格式
	 * @return
	 */
	public static String friendlyMonth(Date time, String type) {
		Calendar now = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		if (null == time) {
			time = new Date();
		}
		target.setTime(time);
		if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {// 如果年份一样
			if (now.get(Calendar.MONTH) == target.get(Calendar.MONTH)) {// 如果月份一样
				return "本月";
			}
		}
		return dateToString(time, type == null ? FORMAT_STRING_DATE_MONTH : type);
	}

	public static boolean isSameMonth(Date firstTime, Date secondTime) {
		Calendar first = Calendar.getInstance();
		Calendar second = Calendar.getInstance();
		if (null == firstTime) {
			firstTime = new Date();
		}
		if (null == secondTime) {
			secondTime = new Date();
		}
		first.setTime(firstTime);
		second.setTime(secondTime);
		if (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) {// 如果年份一样
			if (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) {// 如果月份一样
				return true;
			}
		}
		return false;
	}

	/**
	 * @see TimeDuring
	 * @param timestamp 要转换的毫秒数
	 * @return
	 */
	public static TimeDuring getTimeDuring(long timestamp) {
		return new TimeDuring(timestamp);
	}

	/**
	 * 将毫秒转为天、小时、分钟、秒,并可进行加减计算可以用于计时和倒数
	 * 
	 * @see DateUtils#getTimeDuring(long)
	 * @author 李欣
	 */
	public static class TimeDuring {

		/**
		 * 时间戳
		 */
		private long mTimestamp;

		private TimeDuring(long timestamp) {
			mTimestamp = timestamp;
		}

		/**
		 * 将给定时间加入(可通过正负数修改时间)
		 * 
		 * @param timestamp 毫秒
		 */
		public void applyTime(long timestamp) {
			mTimestamp += timestamp;
		}

		/**
		 * 返回当前时间戳
		 * 
		 * @return
		 */
		public long getTime() {
			return mTimestamp;
		}

		/**
		 * 天
		 * 
		 * @see #days
		 * @return
		 */
		public long days() {
			return mTimestamp / (1000 * 60 * 60 * 24);
		}

		/**
		 * 小时
		 * 
		 * @see #hours
		 * @return
		 */
		public int hours() {
			return (int) (mTimestamp % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		}

		/**
		 * 分钟
		 * 
		 * @see #minutes
		 * @return
		 */
		public int minutes() {
			return (int) (mTimestamp % (1000 * 60 * 60)) / (1000 * 60);
		}

		/**
		 * 秒
		 * 
		 * @see #seconds
		 * @return
		 */
		public int seconds() {
			return (int) (mTimestamp % (1000 * 60)) / 1000;
		}

	}

}
