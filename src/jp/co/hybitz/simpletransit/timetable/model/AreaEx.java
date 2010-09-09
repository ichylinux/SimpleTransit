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
import jp.co.hybitz.timetable.model.Area;
import jp.co.hybitz.timetable.model.Prefecture;

public class AreaEx extends Entity {
    private Area area;
    private List<PrefectureEx> prefectures = new ArrayList<PrefectureEx>();

    public AreaEx() {
        this(new Area());
    }
    
    public AreaEx(Area area) {
        this.area = area;
        for(Prefecture p : area.getPrefectures()) {
            addPrefecture(new PrefectureEx(p));
        }
    }

    public Area getArea() {
        return area;
    }

    public String getName() {
        return area.getName();
    }

    public List<PrefectureEx> getPrefectures() {
        return prefectures;
    }
    
    public void addPrefecture(PrefectureEx prefecture) {
        prefectures.add(prefecture);
    }

    public void setPrefectures(List<PrefectureEx> prefectures) {
        this.prefectures = prefectures;
    }


    public String getUrl() {
        return area.getUrl();
    }

    public void setName(String name) {
        area.setName(name);
    }

    public void setUrl(String url) {
        area.setUrl(url);
    }
}
