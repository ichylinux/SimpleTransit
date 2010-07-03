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
package jp.co.hybitz.simpletransit.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.co.hybitz.googletransit.model.TimeType;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TimeTypeAndDate implements Serializable {
    private TimeType timeType;
    private Date date;
    
    public TimeTypeAndDate(TimeType timeType, Date date) {
        this.timeType = timeType;
        this.date = date;
    }
    
    public TimeType getTimeType() {
        return timeType;
    }
    
    public Calendar getCalendar() {
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	return c;
    }

    public Date getDate() {
        return date;
    }
    
    public int getYear() {
    	return getCalendar().get(Calendar.YEAR);
    }

    public int getMonth() {
    	return getCalendar().get(Calendar.MONTH);
    }

    public int getDay() {
    	return getCalendar().get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
    	return getCalendar().get(Calendar.HOUR_OF_DAY);
    }
    
    public int getMinute() {
    	return getCalendar().get(Calendar.MINUTE);
    }
    
    public String toString() {
    	return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date) + (timeType == TimeType.DEPARTURE ? "発" : "着");
    }
}
