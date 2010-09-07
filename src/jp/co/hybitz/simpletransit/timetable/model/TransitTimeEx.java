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

import java.io.Serializable;

import jp.co.hybitz.timetable.model.TransitTime;

public class TransitTimeEx implements Serializable {
    private long id;
    private long timeLineId;
    private TransitTime transitTime;

    public TransitTimeEx(TransitTime transitTime) {
        this.transitTime = transitTime;
    }

    public TransitTime getTransitTime() {
        return transitTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeLineId() {
        return timeLineId;
    }

    public void setTimeLineId(long timeLineId) {
        this.timeLineId = timeLineId;
    }

    public String getBoundFor() {
        return transitTime.getBoundFor();
    }

    public int getHour() {
        return transitTime.getHour();
    }

    public String getHourAsString() {
        return transitTime.getHourAsString();
    }

    public int getMinute() {
        return transitTime.getMinute();
    }

    public String getMinuteAsString() {
        return transitTime.getMinuteAsString();
    }

    public String getTimeAsString() {
        return transitTime.getTimeAsString();
    }

    public String getTimeAsString(boolean withColon) {
        return transitTime.getTimeAsString(withColon);
    }

    public String getTransitClass() {
        return transitTime.getTransitClass();
    }

    public void setBoundFor(String boundFor) {
        transitTime.setBoundFor(boundFor);
    }

    public void setTransitClass(String transitClass) {
        transitTime.setTransitClass(transitClass);
    }

}
