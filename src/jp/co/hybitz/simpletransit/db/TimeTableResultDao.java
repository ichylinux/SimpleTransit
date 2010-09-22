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
package jp.co.hybitz.simpletransit.db;

import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.android.CursorEx;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeLineEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.timetable.model.TransitTimeEx;
import jp.co.hybitz.timetable.model.TimeTable;
import jp.co.hybitz.timetable.model.TransitTime;
import jp.co.hybitz.timetable.model.TimeTable.Type;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TimeTableResultDao extends AbstractDao implements SimpleTransitConst {

    public TimeTableResultDao(Context context) {
        super(context);
    }

    public List<AreaEx> getAreas() {
        List<AreaEx> ret = new ArrayList<AreaEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("area", null, null, null, null, null, "_id asc");
            while (c.moveToNext()) {
                AreaEx a = loadArea(c);
                a.setPrefectures(getPrefectures(db, a.getId()));
                ret.add(a);
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }
    
    public AreaEx getArea(long areaId, boolean eager) {
        SQLiteDatabase db = getReadableDatabase();
        CursorEx c = null;
        try {
            c = (CursorEx) db.query("area", null, "_id=?", new String[]{String.valueOf(areaId)}, null, null, null);
            if (c.moveToFirst()) {
                AreaEx a = loadArea(c);
                if (eager) {
                    a.setPrefectures(getPrefectures(db, a.getId()));
                }
                return a;
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
    }

    public List<PrefectureEx> getPrefectures(long areaId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            return getPrefectures(db, areaId);
        }
        finally {
            db.close();
        }
    }

    public PrefectureEx getPrefecture(long prefectureId) {
        SQLiteDatabase db = getReadableDatabase();
        CursorEx c = null;
        try {
            String[] args = new String[]{String.valueOf(prefectureId)};
            c = (CursorEx) db.query("prefecture", null, "_id=?", args, null, null, null);
            if (c.moveToFirst()) {
                PrefectureEx p = loadPrefecture(c);
                return p;
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
    }
    
    private List<PrefectureEx> getPrefectures(SQLiteDatabase db, long areaId) {
        List<PrefectureEx> ret = new ArrayList<PrefectureEx>();
        
        CursorEx c = (CursorEx) db.query("prefecture", null, "area_id=?", new String[]{String.valueOf(areaId)}, null, null, "_id asc");
        while (c.moveToNext()) {
            PrefectureEx p = loadPrefecture(c); 
            ret.add(p);
        }
        c.close();
        
        return ret;
    }

    public List<LineEx> getLines(long prefectureId) {
        List<LineEx> ret = new ArrayList<LineEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("line", null, "prefecture_id=?", new String[]{String.valueOf(prefectureId)}, null, null, "display_order");
            while (c.moveToNext()) {
                ret.add(loadLine(c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }

    public LineEx getLine(long lineId) {
        SQLiteDatabase db = getReadableDatabase();
        CursorEx c = null;
        try {
            String[] args = new String[]{String.valueOf(lineId)};
            c = (CursorEx) db.query("line", null, "_id=?", args, null, null, null);
            if (c.moveToFirst()) {
                LineEx p = loadLine(c);
                return p;
            }
            else {
                return null;
            }
        }
        finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
    }

    private LineEx getLine(SQLiteDatabase db, long lineId) {
        CursorEx c = (CursorEx) db.query("line", null, "_id=?", new String[]{String.valueOf(lineId)}, null, null, null);
        try {
            if (c.moveToFirst()) {
                return loadLine(c);
            }
        }
        finally {
            c.close();
        }
        
        return null;
    }

    public List<StationEx> getStations(long lineId) {
        List<StationEx> ret = new ArrayList<StationEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("station", null, "line_id=?", new String[]{String.valueOf(lineId)}, null, null, "_id asc");
            while (c.moveToNext()) {
                ret.add(loadStation(c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }

    public StationEx getStation(long stationId, boolean eager) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            return getStation(db, stationId, eager);
        }
        finally {
            db.close();
        }
    }

    private StationEx getStation(SQLiteDatabase db, long stationId, boolean eager) {
        CursorEx c = (CursorEx) db.query("station", null, "_id=?", new String[]{String.valueOf(stationId)}, null, null, null);
        try {
            if (c.moveToFirst()) {
                StationEx ret = loadStation(c);
                if (eager) {
                    ret.setTimeTables(getTimeTables(db, stationId, eager));
                }
                return ret;
            }
            else {
                return null;
            }
            
        }
        finally {
            c.close();
        }
    }

    public List<TimeTableEx> getTimeTables(long stationId, boolean eager) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            return getTimeTables(db, stationId, eager);
        }
        finally {
            db.close();
        }
    }
    
    private List<TimeTableEx> getTimeTables(SQLiteDatabase db, long stationId, boolean eager) {
        List<TimeTableEx> ret = new ArrayList<TimeTableEx>();
        
        String[] args = new String[]{String.valueOf(stationId)};
        CursorEx c = (CursorEx) db.query("time_table", null, "station_id=?", args, null, null, "_id asc");
        while (c.moveToNext()) {
            TimeTableEx tt = loadTimeTable(c);
            if (eager) {
                tt.setTimeLines(getTimeLines(db, tt.getId()));
            }
            ret.add(tt);
        }
        c.close();
        
        return ret;
    }

    public TimeTableEx getTimeTable(long stationId, String direction, TimeTable.Type type) {
        SQLiteDatabase db = getReadableDatabase();
        CursorEx c = null;
        try {
            c = (CursorEx) db.query("time_table", null, "station_id=? and direction=? and type=?",
                    new String[]{String.valueOf(stationId), direction, String.valueOf(toInt(type))}, null, null, null);
            if (c.moveToFirst()) {
                TimeTableEx tt = loadTimeTable(c);
                tt.setTimeLines(getTimeLines(db, tt.getId()));
                return tt;
            }
        }
        finally {
            if (c != null) { c.close(); }
            db.close();
        }
        
        return null;
    }

    public List<TimeLineEx> getTimeLines(long timeTableId) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            return getTimeLines(db, timeTableId);
        }
        finally {
            db.close();
        }
    }

    private List<TimeLineEx> getTimeLines(SQLiteDatabase db, long timeTableId) {
        List<TimeLineEx> ret = new ArrayList<TimeLineEx>();
        
        CursorEx c = (CursorEx) db.query("time_line", null, "time_table_id=?", new String[]{String.valueOf(timeTableId)}, null, null, "_id asc");
        while (c.moveToNext()) {
            TimeLineEx tl = loadTimeLine(c);
            tl.setTimes(getTransitTimes(db, tl.getId()));
            ret.add(tl);
        }
        c.close();
        
        return ret;
    }

    private List<TransitTimeEx> getTransitTimes(SQLiteDatabase db, long timeLineId) {
        List<TransitTimeEx> ret = new ArrayList<TransitTimeEx>();
        
        CursorEx c = (CursorEx) db.query("transit_time", null, "time_line_id=?", new String[]{String.valueOf(timeLineId)}, null, null, "_id asc");
        while (c.moveToNext()) {
            TransitTimeEx tt = loadTransitTime(c);
            ret.add(tt);
        }
        c.close();
        
        return ret;
    }

    private AreaEx loadArea(CursorEx c) {
        AreaEx a = new AreaEx();
        a.setId(c.getLong("_id"));
        a.setName(c.getString("name"));
        a.setUrl(c.getString("url"));
        a.setCreatedAt(c.getLong("created_at"));
        a.setUpdatedAt(c.getLong("updated_at"));
        return a;
    }
    
    private PrefectureEx loadPrefecture(CursorEx c) {
        PrefectureEx p = new PrefectureEx();
        p.setId(c.getLong("_id"));
        p.setAreaId(c.getLong("area_id"));
        p.setName(c.getString("name"));
        p.setUrl(c.getString("url"));
        p.setCreatedAt(c.getLong("created_at"));
        p.setUpdatedAt(c.getLong("updated_at"));
        return p;
    }

    private LineEx loadLine(CursorEx c) {
        LineEx l = new LineEx();
        l.setId(c.getLong("_id"));
        l.setPrefectureId(c.getLong("prefecture_id"));
        l.setName(c.getString("name"));
        l.setCompany(c.getString("company"));
        l.setUrl(c.getString("url"));
        l.setDiaplayOrder(c.getInt("display_order"));
        l.setCreatedAt(c.getLong("created_at"));
        l.setUpdatedAt(c.getLong("updated_at"));
        return l;
    }

    private StationEx loadStation(CursorEx c) {
        StationEx s = new StationEx();
        s.setId(c.getLong("_id"));
        s.setLineId(c.getLong("line_id"));
        s.setName(c.getString("name"));
        s.setUrl(c.getString("url"));
        s.setCreatedAt(c.getLong("created_at"));
        s.setUpdatedAt(c.getLong("updated_at"));
        return s;
    }

    private TimeTableEx loadTimeTable(CursorEx c) {
        TimeTableEx tt = new TimeTableEx();
        tt.setId(c.getLong("_id"));
        tt.setStationId(c.getLong("station_id"));
        tt.setLineName(c.getString("line_name"));
        tt.setDirection(c.getString("direction"));
        tt.setType(toType(c.getInt("type")));
        tt.setFavorite(c.getBoolean("is_favorite"));
        tt.setCreatedAt(c.getLong("created_at"));
        tt.setUpdatedAt(c.getLong("updated_at"));
        return tt;
    }
    
    private TimeLineEx loadTimeLine(CursorEx c) {
        TimeLineEx tl = new TimeLineEx();
        tl.setId(c.getLong("_id"));
        tl.setTimeTableId(c.getLong("time_table_id"));
        tl.setHour(c.getInt("hour"));
        return tl;
    }

    private TransitTimeEx loadTransitTime(CursorEx c) {
        TransitTime t = new TransitTime(c.getInt("hour"), c.getInt("minute"));
        TransitTimeEx tt = new TransitTimeEx(t);
        tt.setId(c.getLong("_id"));
        tt.setTimeLineId(c.getLong("time_line_id"));
        tt.setTransitClass(c.getString("transit_class"));
        tt.setBoundFor(c.getString("bound_for"));
        tt.setCreatedAt(c.getLong("created_at"));
        tt.setUpdatedAt(c.getLong("updated_at"));
        return tt;
    }

    private Type toType(int type) {
        switch (type) {
        case 1 :
            return Type.WEEKDAY;
        case 2 :
            return Type.SATURDAY;
        case 4 :
            return Type.SUNDAY;
        default :
            throw new IllegalStateException("予期していないタイプです。type=" + type);
        }
    }

    private int toInt(Type type) {
        switch (type) {
        case WEEKDAY :
            return 1;
        case SATURDAY :
            return 2;
        case SUNDAY :
            return 4;
        default :
            throw new IllegalStateException("予期していないタイプです。type=" + type);
        }
    }

    public List<Long> insertAreas(List<AreaEx> areas) {
        long now = getCurrentDateTime();
        List<Long> areaIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (AreaEx a : areas) {
                // area
                ContentValues cv = new ContentValues();
                cv.put("name", a.getName());
                cv.put("url", a.getUrl());
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long areaId = db.insertOrThrow("area", null, cv);
                
                // prefecture
                insertPrefectures(db, areaId, a.getPrefectures());
                
                areaIdList.add(areaId);
            }
            
            db.setTransactionSuccessful();
            return areaIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public List<Long> insertPrefectures(long areaId, List<PrefectureEx> prefectures) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            List<Long> prefectureIdList = insertPrefectures(db, areaId, prefectures);
            db.setTransactionSuccessful();
            return prefectureIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Long> insertPrefectures(SQLiteDatabase db, long areaId, List<PrefectureEx> prefectures) {
        long now = getCurrentDateTime();
        List<Long> prefectureIdList = new ArrayList<Long>();

        for (PrefectureEx p : prefectures) {
            ContentValues pcv = new ContentValues();
            pcv.put("area_id", areaId);
            pcv.put("name", p.getName());
            pcv.put("url", p.getUrl());
            pcv.put("created_at", now);
            pcv.put("updated_at", now);
            
            prefectureIdList.add(db.insertOrThrow("prefecture", null, pcv));
        }
        
        return prefectureIdList;
    }

    public List<Long> insertLines(long prefectureId, List<LineEx> lines) {
        long now = getCurrentDateTime();
        List<Long> lineIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (LineEx l : lines) {
                ContentValues cv = new ContentValues();
                cv.put("prefecture_id", prefectureId);
                cv.put("name", l.getName());
                cv.put("company", l.getCompany());
                cv.put("url", l.getUrl());
                cv.put("display_order", l.getDiaplayOrder());
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long lineId = db.insertOrThrow("line", null, cv);
                if (lineId < 0) {
                    return new ArrayList<Long>();
                }
                
                lineIdList.add(lineId);
            }
            
            db.setTransactionSuccessful();
            return lineIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public List<Long> insertStations(long lineId, List<StationEx> stations) {
        long now = getCurrentDateTime();
        List<Long> stationIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (StationEx s : stations) {
                ContentValues cv = new ContentValues();
                cv.put("line_id", lineId);
                cv.put("name", s.getName());
                cv.put("url", s.getUrl());
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long stationId = db.insertOrThrow("station", null, cv);
                if (stationId < 0) {
                    return new ArrayList<Long>();
                }
                
                stationIdList.add(stationId);
            }
            
            db.setTransactionSuccessful();
            return stationIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public List<Long> insertTimeTables(long stationId, List<TimeTableEx> timeTables) {
        long now = getCurrentDateTime();
        List<Long> timeTableIdList = new ArrayList<Long>();
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (TimeTableEx tt : timeTables) {
                // time_table
                ContentValues cv = new ContentValues();
                cv.put("station_id", stationId);
                cv.put("line_name", tt.getLineName());
                cv.put("direction", tt.getDirection());
                cv.put("type", toInt(tt.getType()));
                cv.put("is_favorite", tt.isFavorite() ? 1 : 0);
                cv.put("created_at", now);
                cv.put("updated_at", now);
                
                long timeTableId = db.insertOrThrow("time_table", null, cv);
                
                // time_line
                for (TimeLineEx tl : tt.getTimeLines()) {
                    insertTimeLine(db, timeTableId, tl);
                }
                
                timeTableIdList.add(timeTableId);
            }
            
            db.setTransactionSuccessful();
            return timeTableIdList;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    private long insertTimeLine(SQLiteDatabase db, long timeTableId, TimeLineEx timeLine) {
        long now = getCurrentDateTime();
        ContentValues lcv = new ContentValues();
        lcv.put("time_table_id", timeTableId);
        lcv.put("hour", timeLine.getHour());
        lcv.put("created_at", now);
        lcv.put("updated_at", now);

        long timeLineId = db.insertOrThrow("time_line", null, lcv);
        
        // transit_time
        for (TransitTimeEx t : timeLine.getTimes()) {
            ContentValues tcv = new ContentValues();
            tcv.put("time_line_id", timeLineId);
            tcv.put("hour", t.getHour());
            tcv.put("minute", t.getMinute());
            tcv.put("transit_class", t.getTransitClass());
            tcv.put("bound_for", t.getBoundFor());
            tcv.put("created_at", now);
            tcv.put("updated_at", now);
            
            db.insertOrThrow("transit_time", null, tcv);
        }
        
        return timeLineId;
    }
    
    public int updateArea(AreaEx area) {
        long now = getCurrentDateTime();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("name", area.getName());
            cv.put("url", area.getUrl());
            cv.put("updated_at", now);
            
            int count = db.update("area", cv, "_id=?", new String[]{String.valueOf(area.getId())});
            if (count != 1) {
                return count;
            }
            
            db.setTransactionSuccessful();
            return count;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int updatePrefecture(PrefectureEx prefecture) {
        long now = getCurrentDateTime();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("area_id", prefecture.getAreaId());
            cv.put("name", prefecture.getName());
            cv.put("url", prefecture.getUrl());
            cv.put("updated_at", now);
            
            int count = db.update("prefecture", cv, "_id=?", new String[]{String.valueOf(prefecture.getId())});
            if (count != 1) {
                return count;
            }
            
            db.setTransactionSuccessful();
            return count;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int updateLine(LineEx line) {
        long now = getCurrentDateTime();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // line
            ContentValues cv = new ContentValues();
            cv.put("prefecture_id", line.getPrefectureId());
            cv.put("name", line.getName());
            cv.put("company", line.getCompany());
            cv.put("url", line.getUrl());
            cv.put("display_order", line.getDiaplayOrder());
            cv.put("updated_at", now);
            
            int count = db.update("line", cv, "_id=?", new String[]{String.valueOf(line.getId())});
            if (count != 1) {
                return count;
            }
            
            db.setTransactionSuccessful();
            return count;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int updateStation(StationEx station) {
        long now = getCurrentDateTime();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // station
            ContentValues cv = new ContentValues();
            cv.put("line_id", station.getLineId());
            cv.put("name", station.getName());
            cv.put("url", station.getUrl());
            cv.put("is_favorite", 0);
            cv.put("updated_at", now);
            
            int count = db.update("station", cv, "_id=?", new String[]{String.valueOf(station.getId())});
            if (count != 1) {
                return count;
            }
            
            db.setTransactionSuccessful();
            return count;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int deleteArea(long areaId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = db.delete("area", "_id=?", new String[]{String.valueOf(areaId)});
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int deletePrefecture(long prefectureId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = db.delete("prefecture", "_id=?", new String[]{String.valueOf(prefectureId)});
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int deleteLine(long lineId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = db.delete("line", "_id=?", new String[]{String.valueOf(lineId)});
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int deleteStation(long stationId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = db.delete("station", "_id=?", new String[]{String.valueOf(stationId)});
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int updateTimeTable(TimeTableEx timeTable) {
        long now = getCurrentDateTime();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // time_table
            ContentValues cv = new ContentValues();
            cv.put("station_id", timeTable.getStationId());
            cv.put("line_name", timeTable.getLineName());
            cv.put("direction", timeTable.getDirection());
            cv.put("type", toInt(timeTable.getType()));
            cv.put("is_favorite", timeTable.isFavorite() ? 1 : 0);
            cv.put("updated_at", now);
            
            int count = db.update("time_table", cv, "_id=?", new String[]{String.valueOf(timeTable.getId())});
            if (count != 1) {
                return count;
            }
            
            // time_line
            deleteTimeLines(db, timeTable.getId());
            for (TimeLineEx tl : timeTable.getTimeLines()) {
                insertTimeLine(db, timeTable.getId(), tl);
            }
            
            db.setTransactionSuccessful();
            return count;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int updateFavorite(TimeTableEx timeTable) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("is_favorite", timeTable.isFavorite() ? 1 : 0);
            values.put("updated_at", getCurrentDateTime());
            return db.update("time_table", values, "_id=?", new String[]{String.valueOf(timeTable.getId())});
        }
        finally {
            db.close();
        }
    }
    
    public List<TimeTableEx> getTimeTablesByFavorite() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            List<TimeTableEx> ret = new ArrayList<TimeTableEx>(); 

            CursorEx c = (CursorEx) db.query("time_table", null, "is_favorite <> 0", null, null, null, "_id asc");
            while (c.moveToNext()) {
                TimeTableEx tt = loadTimeTable(c);
                StationEx station = getStation(db, tt.getStationId(), false);
                LineEx line = getLine(db, station.getLineId());
                station.setLine(line);
                tt.setStation(station);
                ret.add(tt);
            }
            c.close();

            return ret;
        }
        finally {
            db.close();
        }
    }
    
    public int deleteStations(long lineId) {
        String[] params = new String[]{String.valueOf(lineId)};
        
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            CursorEx c = (CursorEx) db.query("station", new String[]{"_id"}, "line_id=?", params, null, null, null);
            while (c.moveToNext()) {
                deleteTimeTables(db, c.getLong("_id"));
            }
            c.close();

            int ret = db.delete("station", "line_id=?", params);
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public int deleteTimeTables(long stationId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = deleteTimeTables(db, stationId);
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
    public int deleteTimeTable(long timeTableId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            deleteTimeLines(db, timeTableId);
            int ret = db.delete("time_table", "_id=?", new String[]{String.valueOf(timeTableId)});
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    private int deleteTimeTables(SQLiteDatabase db, long stationId) {
        String[] params = new String[]{String.valueOf(stationId)};

        CursorEx c = (CursorEx) db.query("time_table", new String[]{"_id"}, "station_id=?", params, null, null, null);
        while (c.moveToNext()) {
            deleteTimeLines(db, c.getLong("_id"));
        }
        c.close();

        return db.delete("time_table", "station_id=?", params);
    }

    private int deleteTimeLines(SQLiteDatabase db, long timeTableId) {
        String[] params = new String[]{String.valueOf(timeTableId)};
        
        CursorEx c = (CursorEx) db.query("time_line", new String[]{"_id"}, "time_table_id=?", params, null, null, null);
        while (c.moveToNext()) {
            deleteTransitTimes(db, c.getLong("_id"));
        }
        c.close();
        
        return db.delete("time_line", "time_table_id=?", params);
    }
    
    private int deleteTransitTimes(SQLiteDatabase db, long timeLineId) {
        String[] params = new String[]{String.valueOf(timeLineId)};
        return db.delete("transit_time", "time_line_id=?", params);
    }
}
