/**
 * Copyright (C) 2010 Hybitz.co.ltd
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 */
package jp.co.hybitz.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class DateUtils {

    public static Long toLong(Date date) {
        if (date == null) {
            return null;
        }
        
        return Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(date));
    }
    
    public static Date toDate(Long dateLong) {
        if (dateLong == null) {
            return null;
        }
        
        try {
            return new SimpleDateFormat("yyyyMMddHHmmss").parse(String.valueOf(dateLong));
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static String format(Long dateLong, String pattern) {
        if (dateLong == null) {
            return null;
        }
        
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(String.valueOf(dateLong));
            return new SimpleDateFormat(pattern).format(date);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static boolean isToday(Date date) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        
        c.setTime(date);
        return c.get(Calendar.YEAR) == year
                && c.get(Calendar.MONTH) == month
                && c.get(Calendar.DATE) == day;
    }
}
