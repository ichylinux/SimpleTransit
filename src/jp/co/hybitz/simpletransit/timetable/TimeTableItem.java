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
package jp.co.hybitz.simpletransit.timetable;

import java.io.Serializable;
import java.text.DecimalFormat;

import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeLineEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.timetable.model.TransitTimeEx;

class TimeTableItem implements Serializable {
    private AreaEx area;
    private PrefectureEx prefecture;
    private LineEx line;
    private StationEx station;
    private TimeTableEx timeTable;
    private TimeLineEx timeLine;

    public TimeTableItem() {
        this(null);
    }

    public TimeTableItem(AreaEx area) {
        this(area, null);
    }

    public TimeTableItem(AreaEx area, PrefectureEx prefecture) {
        this(area, prefecture, null);
    }

    public TimeTableItem(AreaEx area, PrefectureEx prefecture, LineEx line) {
        this(area, prefecture, line, null);
    }

    public TimeTableItem(AreaEx area, PrefectureEx prefecture, LineEx line, StationEx station) {
        this(area, prefecture, line, station, null);
    }

    public TimeTableItem(AreaEx area, PrefectureEx prefecture, LineEx line, StationEx station, TimeTableEx timeTable) {
        this(area, prefecture, line, station, timeTable, null);
    }

    public TimeTableItem(AreaEx area, PrefectureEx prefecture, LineEx line, StationEx station, TimeTableEx timeTable, TimeLineEx timeLine) {
        this.area = area;
        this.prefecture = prefecture;
        this.line = line;
        this.station = station;
        this.timeTable = timeTable;
        this.timeLine = timeLine;
    }
    
    public AreaEx getArea() {
        return area;
    }
    
    public PrefectureEx getPrefecture() {
        return prefecture;
    }
    
    public LineEx getLine() {
        return line;
    }
    
    public StationEx getStation() {
        return station;
    }
    
    public TimeTableEx getTimeTable() {
        return timeTable;
    }

    public TimeLineEx getTimeLine() {
        return timeLine;
    }
    
    public String getHour() {
        if (timeLine != null) {
            return new DecimalFormat("00").format(timeLine.getHour()) + "時　";
        }
        
        return null;
    }

    public String getTitle() {
        if (timeLine != null) {
            DecimalFormat df = new DecimalFormat("00");
            StringBuilder sb = new StringBuilder();
            for (TransitTimeEx t : timeLine.getTimes()) {
                if (sb.length() > 0) {
                    sb.append("  ");
                }
                sb.append(df.format(t.getMinute()));
                if (StringUtils.isNotEmpty(t.getTransitClass())) {
                    sb.append(t.getTransitClass());
                }
            }
            return sb.toString();
        }
        
        if (timeTable != null) {
            return timeTable.getDirection() + "　" + timeTable.getTypeString();
        }
        
        if (station != null) {
            return station.getName();
        }
        
        if (line != null) {
            return line.getCompany() + "　" + line.getName();
        }
        
        if (prefecture != null) {
            return prefecture.getName();
        }
        
        if (area != null) {
            return area.getName();
        }
        
        return null;
    }
}
