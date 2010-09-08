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

import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeLineEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;

public class ParentBackItem extends TimeTableItem {

    public ParentBackItem() {
        super();
    }

    public ParentBackItem(AreaEx area) {
        super(area);
    }

    public ParentBackItem(AreaEx area, PrefectureEx prefecture) {
        super(area, prefecture);
    }

    public ParentBackItem(AreaEx area, PrefectureEx prefecture, LineEx line) {
        super(area, prefecture, line);
    }

    public ParentBackItem(AreaEx area, PrefectureEx prefecture, LineEx line, StationEx station) {
        super(area, prefecture, line, station);
    }

    public ParentBackItem(AreaEx area, PrefectureEx prefecture, LineEx line, StationEx station, TimeTableEx timeTable, TimeLineEx timeLine) {
        super(area, prefecture, line, station, timeTable, timeLine);
    }

    @Override
    public String getHour() {
        return null;
    }
    
    @Override
    public String getTitle() {
        return "..";
    }
}
