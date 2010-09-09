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
import jp.co.hybitz.timetable.model.Prefecture;

public class PrefectureEx extends Entity {
    private long areaId;
    private Prefecture prefecture;
    private List<LineEx> lines = new ArrayList<LineEx>();
    
    public PrefectureEx() {
        this(new Prefecture());
    }
    
    public PrefectureEx(Prefecture prefecture) {
        this.prefecture = prefecture;
    }

    public Prefecture getPrefecture() {
        return prefecture;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public List<LineEx> getLines() {
        return lines;
    }

    public String getName() {
        return prefecture.getName();
    }

    public String getUrl() {
        return prefecture.getUrl();
    }

    public void addLine(LineEx line) {
        lines.add(line);
    }
    
    public void setLines(List<LineEx> lines) {
        this.lines = lines;
    }

    public void setName(String name) {
        prefecture.setName(name);
    }

    public void setUrl(String url) {
        prefecture.setUrl(url);
    }
    
}
