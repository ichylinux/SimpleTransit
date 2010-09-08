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
import java.util.List;

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.simpletransit.SimpleTransit;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
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
    protected TimeTableResult search(TimeTableQuery in) throws HttpSearchException {
        TimeTableResult result = TimeTableSearcherFactory.createSearcher(Platform.LOOSE_HTML).search(in);

        if (isCancelled()) {
            return null;
        }
        
        if (result.isOK()) {
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
                TimeTableResultDao dao = new TimeTableResultDao(getActivity());
                Area a = result.getAreas().get(0);
                Prefecture p = a.getPrefectures().get(0);
                
                if (item.getLine() == null) {
                    // 検索結果を保存
                    if (dao.getLines(item.getPrefecture().getId()).isEmpty()) {
                        List<LineEx> lines = new ArrayList<LineEx>();
                        for (Line l : p.getLines()) {
                            lines.add(new LineEx(l));
                        }
                        dao.insertLines(item.getPrefecture().getId(), lines);
                    }
                    item.getPrefecture().setLines(dao.getLines(item.getPrefecture().getId()));
                }
                else if (item.getStation() == null) {
                    // 検索結果を保存
                    if (dao.getStations(item.getLine().getId()).isEmpty()) {
                        List<StationEx> stations = new ArrayList<StationEx>();
                        for (Station s : p.getLines().get(0).getStations()) {
                            stations.add(new StationEx(s));
                        }
                        dao.insertStations(item.getLine().getId(), stations);
                    }
                    item.getLine().setStations(dao.getStations(item.getLine().getId()));
                }
                else if (item.getTimeTable() == null) {
                    // 検索結果を保存
                    if (dao.getTimeTables(item.getStation().getId()).isEmpty()) {
                        List<TimeTableEx> timeTables = new ArrayList<TimeTableEx>();
                        for (TimeTable tt : p.getLines().get(0).getStations().get(0).getTimeTables()) {
                            timeTables.add(new TimeTableEx(tt));
                        }
                        dao.insertTimeTables(item.getStation().getId(), timeTables);
                    }
                    item.getStation().setTimeTables(dao.getTimeTables(item.getStation().getId()));
                }

                return result;
            }
        }

        return null;
    }

    @Override
    protected void updateView(TimeTableResult out) {
        if (out == null) {
            return;
        }
        
        TimeTableActivity ttla = (TimeTableActivity) getActivity();
        
        if (item.getLine() == null) {
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
