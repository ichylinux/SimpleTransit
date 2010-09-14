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
import jp.co.hybitz.timetable.model.Station;

public class StationEx extends Entity {
    private long lineId;
    private LineEx line;
    private Station station;
    private List<TimeTableEx> timeTables = new ArrayList<TimeTableEx>();

    public StationEx() {
        this(new Station());
    }
    
    public StationEx(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    public LineEx getLine() {
        return line;
    }

    public void setLine(LineEx line) {
        this.line = line;
    }

    public String getName() {
        return station.getName();
    }

    public String getUrl() {
        return station.getUrl();
    }

    public void setName(String name) {
        station.setName(name);
    }

    public void setUrl(String url) {
        station.setUrl(url);
    }

    public List<TimeTableEx> getTimeTables() {
        return timeTables;
    }
    
    public void setTimeTables(List<TimeTableEx> timeTables) {
        this.timeTables = timeTables;
    }
}
