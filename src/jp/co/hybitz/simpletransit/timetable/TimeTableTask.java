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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.simpletransit.SimpleTransit;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableResultEx;
import jp.co.hybitz.timetable.TimeTableSearcherFactory;
import jp.co.hybitz.timetable.model.Area;
import jp.co.hybitz.timetable.model.Line;
import jp.co.hybitz.timetable.model.Prefecture;
import jp.co.hybitz.timetable.model.Station;
import jp.co.hybitz.timetable.model.TimeTable;
import jp.co.hybitz.timetable.model.TimeTableQuery;
import jp.co.hybitz.timetable.model.TimeTableResult;
import android.app.Activity;
import android.content.Intent;

public class TimeTableTask extends WebSearchTask<TimeTableQuery, TimeTableResult> implements SimpleTransitConst {
    private TimeTableItem item;
    
    public TimeTableTask(Activity activity) {
        super(activity);
    }

    public TimeTableTask(Activity activity, TimeTableItem item) {
        super(activity);
        this.item = item;
    }
    
    @Override
    protected TimeTableResult search(TimeTableQuery query) throws HttpSearchException {
        TimeTableResult result = TimeTableSearcherFactory.createSearcher(Platform.HTML).search(query);

        if (isCancelled()) {
            return null;
        }
        
        if (!result.isOK()) {
            return null;
        }

        if (getActivity() instanceof SimpleTransit) {
            // 検索結果を保存
            TimeTableResultDao dao = new TimeTableResultDao(getActivity());
            if (dao.getAreas().isEmpty()) {
                // 検索結果を拡張モデルに変換
                List<AreaEx> areas = new ArrayList<AreaEx>();
                for (Area a : result.getAreas()) {
                    areas.add(new AreaEx(a));
                }
                dao.insertAreas(areas);
            }

            // 時刻表アクティビティ開始
            TimeTableResultEx resultEx = new TimeTableResultEx();
            resultEx.setAreas(dao.getAreas());
            Intent intent = new Intent(getActivity(), TimeTableActivity.class);
            intent.putExtra(EXTRA_KEY_TIME_TABLE_RESULT, resultEx);
            getActivity().startActivity(intent);
            
            return null;
        }
        else if (getActivity() instanceof TimeTableActivity) {
            save(result);
            return result;
        }

        return null;
    }
    
    private void save(TimeTableResult result) {
        if (item == null) {
            saveAreas(result);
        }
        else if (item.getPrefecture() == null) {
            savePrefectures(result);
        }
        else if (item.getLine() == null) {
            saveLines(result);
        }
        else if (item.getStation() == null) {
            saveStations(result);
        }
        else if (item.getTimeTable() == null) {
            saveTimeTables(result);
        }
    }
    
    private void saveAreas(TimeTableResult result) {
        TimeTableResultDao dao = new TimeTableResultDao(getActivity());

        List<AreaEx> newAreas = new ArrayList<AreaEx>();
        for (int i = 0; i < result.getAreas().size(); i ++) {
            Area a = result.getAreas().get(i);
            AreaEx area = new AreaEx(a);
            newAreas.add(area);
        }

        // 検索結果を保存
        List<AreaEx> currentAreas = dao.getAreas();
        if (currentAreas.isEmpty()) {
            dao.insertAreas(newAreas);
        }
        else {
            Map<String, AreaEx> currentAreaMap = new HashMap<String, AreaEx>();
            for (Iterator<AreaEx> it = currentAreas.iterator(); it.hasNext();) {
                AreaEx area = it.next();
                currentAreaMap.put(area.getName(), area);
            }
            
            // 既存レコードは更新して新規リストから削除
            for (Iterator<AreaEx> it = newAreas.iterator(); it.hasNext();) {
                AreaEx newArea = it.next();
                AreaEx currentArea = currentAreaMap.get(newArea.getName());
                if (currentArea != null) {
                    currentArea.setUrl(newArea.getUrl());
                    dao.updateArea(currentArea);
                    it.remove();
                }
            }
            
            // 存在しなくなった地域を削除
            for (Iterator<AreaEx> it = currentAreas.iterator(); it.hasNext();) {
                AreaEx currentArea = it.next();
                Area newArea = result.getArea(currentArea.getName());
                if (newArea == null) {
                    dao.deleteArea(currentArea.getId());
                }
            }
            
            // 新規の地域を登録
            if (! newAreas.isEmpty()) {
                dao.insertAreas(newAreas);
            }
        }
    }

    private void savePrefectures(TimeTableResult result) {
        TimeTableResultDao dao = new TimeTableResultDao(getActivity());
        Area a = result.getArea(item.getArea().getName());
        if (a == null) {
            return;
        }

        AreaEx newArea = new AreaEx(a);
        for (int i = 0; i < a.getPrefectures().size(); i ++) {
            Prefecture p = a.getPrefectures().get(i);
            PrefectureEx prefecture = new PrefectureEx(p);
            newArea.addPrefecture(prefecture);
        }

        // 検索結果を保存
        List<PrefectureEx> currentPrefectures = dao.getPrefectures(item.getArea().getId());
        if (currentPrefectures.isEmpty()) {
            dao.insertPrefectures(item.getArea().getId(), newArea.getPrefectures());
        }
        else {
            AreaEx currentArea = dao.getArea(item.getArea().getId(), false);
            currentArea.setPrefectures(currentPrefectures);
            
            // 既存レコードは更新して新規リストから削除
            for (Iterator<PrefectureEx> it = newArea.getPrefectures().iterator(); it.hasNext();) {
                PrefectureEx newPrefecture = it.next();
                PrefectureEx currentPrefecture = currentArea.getPrefecture(newPrefecture.getName());
                if (currentPrefecture != null) {
                    currentPrefecture.setUrl(newPrefecture.getUrl());
                    dao.updatePrefecture(currentPrefecture);
                    it.remove();
                }
            }
            
            // 存在しなくなった都道府県を削除
            for (Iterator<PrefectureEx> it = currentArea.getPrefectures().iterator(); it.hasNext();) {
                PrefectureEx currentPrefecture = it.next();
                Prefecture newPrefecture = a.getPrefecture(currentPrefecture.getName());
                if (newPrefecture == null) {
                    dao.deletePrefecture(currentPrefecture.getId());
                }
            }
            
            // 新規の都道府県を登録
            if (! newArea.getPrefectures().isEmpty()) {
                dao.insertPrefectures(item.getArea().getId(), newArea.getPrefectures());
            }
        }
        
        item.getArea().setPrefectures(dao.getPrefectures(item.getArea().getId()));
    }

    private void saveLines(TimeTableResult result) {
        TimeTableResultDao dao = new TimeTableResultDao(getActivity());
        Prefecture p = result.getAreas().get(0).getPrefectures().get(0);

        PrefectureEx newPrefecture = new PrefectureEx(p);
        for (int i = 0; i < p.getLines().size(); i ++) {
            Line l = p.getLines().get(i);
            LineEx line = new LineEx(l);
            line.setDiaplayOrder(i+1);
            newPrefecture.addLine(line);
        }

        // 検索結果を保存
        List<LineEx> currentLines = dao.getLines(item.getPrefecture().getId());
        if (currentLines.isEmpty()) {
            dao.insertLines(item.getPrefecture().getId(), newPrefecture.getLines());
        }
        else {
            PrefectureEx currentPrefecture = dao.getPrefecture(item.getPrefecture().getId());
            currentPrefecture.setLines(currentLines);
            
            // 既存レコードは更新して新規リストから削除
            for (Iterator<LineEx> it = newPrefecture.getLines().iterator(); it.hasNext();) {
                LineEx newLine = it.next();
                LineEx currentLine = currentPrefecture.getLine(newLine.getCompany(), newLine.getName());
                if (currentLine != null) {
                    currentLine.setUrl(newLine.getUrl());
                    currentLine.setDiaplayOrder(newLine.getDiaplayOrder());
                    dao.updateLine(currentLine);
                    it.remove();
                }
            }
            
            // 存在しなくなった路線を削除
            for (Iterator<LineEx> it = currentPrefecture.getLines().iterator(); it.hasNext();) {
                LineEx currentLine = it.next();
                Line newLine = p.getLine(currentLine.getCompany(), currentLine.getName());
                if (newLine == null) {
                    dao.deleteLine(currentLine.getId());
                }
            }
            
            // 新規の時刻表を登録
            if (! newPrefecture.getLines().isEmpty()) {
                dao.insertLines(item.getPrefecture().getId(), newPrefecture.getLines());
            }
        }
        
        item.getPrefecture().setLines(dao.getLines(item.getPrefecture().getId()));
    }

    private void saveStations(TimeTableResult result) {
        TimeTableResultDao dao = new TimeTableResultDao(getActivity());
        Line l = result.getAreas().get(0).getPrefectures().get(0).getLines().get(0);

        LineEx newLine = new LineEx(l);
        for (Station s : l.getStations()) {
            newLine.addStation(new StationEx(s));
        }

        // 検索結果を保存
        List<StationEx> currentStations = dao.getStations(item.getLine().getId());
        if (currentStations.isEmpty()) {
            dao.insertStations(item.getLine().getId(), newLine.getStations());
        }
        else {
            LineEx currentLine = dao.getLine(item.getLine().getId());
            currentLine.setStations(currentStations);
            
            // 既存レコードは更新して新規リストから削除
            for (Iterator<StationEx> it = newLine.getStations().iterator(); it.hasNext();) {
                StationEx newStation = it.next();
                StationEx currentStation = currentLine.getStation(newStation.getName());
                if (currentStation != null) {
                    currentStation.setUrl(newStation.getUrl());
                    dao.updateStation(currentStation);
                    it.remove();
                }
            }
            
            // 存在しなくなった路線を削除
            for (Iterator<StationEx> it = currentLine.getStations().iterator(); it.hasNext();) {
                StationEx currentStation = it.next();
                Station newStation = l.getStation(currentStation.getName());
                if (newStation == null) {
                    dao.deleteStation(currentStation.getId());
                }
            }
            
            // 新規の時刻表を登録
            if (! newLine.getStations().isEmpty()) {
                dao.insertStations(item.getLine().getId(), newLine.getStations());
            }
        }
        
        item.getLine().setStations(dao.getStations(item.getLine().getId()));
    }

    private void saveTimeTables(TimeTableResult result) {
        TimeTableResultDao dao = new TimeTableResultDao(getActivity());
        Area a = result.getAreas().get(0);
        Prefecture p = a.getPrefectures().get(0);

        Station newStation = p.getLines().get(0).getStations().get(0);
        List<TimeTableEx> newTimeTables = new ArrayList<TimeTableEx>();
        for (TimeTable tt : newStation.getTimeTables()) {
            newTimeTables.add(new TimeTableEx(tt));
        }

        // 検索結果を保存
        StationEx s = dao.getStation(item.getStation().getId(), true);
        if (s.getTimeTables().isEmpty()) {
            dao.insertTimeTables(item.getStation().getId(), newTimeTables);
        }
        else {
            // 既存レコードは更新して新規リストから削除
            for (Iterator<TimeTableEx> it = newTimeTables.iterator(); it.hasNext();) {
                TimeTableEx tt = it.next();
                TimeTableEx current = s.getTimeTable(tt.getDirection(), tt.getType());
                if (current != null) {
                    current.setLineName(tt.getLineName());
                    current.setTimeLines(tt.getTimeLines());
                    dao.updateTimeTable(current);
                    it.remove();
                }
            }
            
            // 存在しなくなった時刻表を削除
            for (Iterator<TimeTableEx> it = s.getTimeTables().iterator(); it.hasNext();) {
                TimeTableEx current = it.next();
                TimeTable newTimeTable = newStation.getTimeTable(current.getDirection(), current.getType());
                if (newTimeTable == null) {
                    dao.deleteTimeTable(current.getId());
                }
            }
            
            // 新規の時刻表を登録
            dao.insertTimeTables(item.getStation().getId(), newTimeTables);
        }
        
        item.getStation().setTimeTables(dao.getTimeTables(item.getStation().getId(), false));
    }

    @Override
    protected void updateView(TimeTableResult out) {
        if (out == null) {
            return;
        }
        
        TimeTableActivity ttla = (TimeTableActivity) getActivity();
        
        if (item == null) {
            TimeTableResultEx result = new TimeTableResultEx(out);
            result.setAreas(new TimeTableResultDao(getActivity()).getAreas());
            ttla.showAreas(result);
        }
        else if (item.getPrefecture() == null) {
            ttla.showPrefectures(item.getArea());
        }
        else if (item.getLine() == null) {
            ttla.showLines(item.getArea(), item.getPrefecture());
        }
        else if (item.getStation() == null) {
            ttla.showStations(item.getArea(), item.getPrefecture(), item.getLine());
        }
        else if (item.getTimeTable() == null) {
            ttla.showTimeTables(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation());
        }
    }
}
