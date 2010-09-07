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
import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.timetable.model.TimeLine;
import jp.co.hybitz.timetable.model.TimeTable;
import jp.co.hybitz.timetable.model.TimeTable.Type;

public class TimeTableEx implements Serializable {
    private long id;
    private long stationId;
    private TimeTable timeTable;
    private List<TimeLineEx> timeLines = new ArrayList<TimeLineEx>();

    public TimeTableEx() {
        this(new TimeTable());
    }
    
    public TimeTableEx(TimeTable timeTable) {
        this.timeTable = timeTable;
        for (TimeLine tl : timeTable.getTimeLines()) {
            addTimeLine(new TimeLineEx(tl));
        }
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public void addTimeLine(TimeLineEx timeLine) {
        timeLines.add(timeLine);
    }

    public void setTimeLines(List<TimeLineEx> timeLines) {
        this.timeLines = timeLines;
    }

    public String getDirection() {
        return timeTable.getDirection();
    }

    public List<TimeLineEx> getTimeLines() {
        return timeLines;
    }

    public Type getType() {
        return timeTable.getType();
    }

    public String getTypeString() {
        return timeTable.getTypeString();
    }

    public void setDirection(String direction) {
        timeTable.setDirection(direction);
    }

    public void setType(Type type) {
        timeTable.setType(type);
    }

}
