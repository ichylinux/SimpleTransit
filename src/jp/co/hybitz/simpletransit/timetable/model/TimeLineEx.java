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
package jp.co.hybitz.simpletransit.timetable.model;

import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.simpletransit.common.model.Entity;
import jp.co.hybitz.timetable.model.TimeLine;
import jp.co.hybitz.timetable.model.TransitTime;

public class TimeLineEx extends Entity {
    private long timeTableId;
    private TimeLine timeLine;
    private List<TransitTimeEx> transitTimes = new ArrayList<TransitTimeEx>();

    public TimeLineEx() {
        this(new TimeLine());
    }
    
    public TimeLineEx(TimeLine timeLine) {
        this.timeLine = timeLine;
        for (TransitTime t : timeLine.getTimes()) {
            addTime(new TransitTimeEx(t));
        }
    }

    public TimeLine getTimeLine() {
        return timeLine;
    }

    public long getTimeTableId() {
        return timeTableId;
    }

    public void setTimeTableId(long timeTableId) {
        this.timeTableId = timeTableId;
    }

    public void addTime(TransitTimeEx transitTime) {
        transitTimes.add(transitTime);
    }

    public void setTimes(List<TransitTimeEx> transitTimes) {
        this.transitTimes = transitTimes;
    }

    public List<TransitTimeEx> getTimes() {
        return transitTimes;
    }

    public int getHour() {
        return timeLine.getHour();
    }

    public void setHour(int hour) {
        timeLine.setHour(hour);
    }

}
