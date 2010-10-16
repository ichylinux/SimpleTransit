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
package jp.co.hybitz.simpletransit;

import jp.co.hybitz.transit.model.Station;

public class StationItem {
    public static final int STATION_TYPE_FROM = 1;
    public static final int STATION_TYPE_TO = 2;
    public static final int STATION_TYPE_STOPOVER = 3;
    
    private int stationType;
    private Station station;

    public StationItem() {
    }

    public StationItem(int stationType, Station station) {
        this.stationType = stationType;
        this.station = station;
    }
    
    public int getStationType() {
        return stationType;
    }
    
    public Station getStation() {
        return station;
    }
}
