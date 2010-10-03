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
import java.util.Iterator;
import java.util.List;

import jp.co.hybitz.android.CursorEx;
import jp.co.hybitz.android.DateUtils;
import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.common.model.Time;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.model.TransitResultEx;
import jp.co.hybitz.transit.model.TimeAndPlace;
import jp.co.hybitz.transit.model.TimeType;
import jp.co.hybitz.transit.model.Transit;
import jp.co.hybitz.transit.model.TransitDetail;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitResultDao extends AbstractDao implements SimpleTransitConst {

    public TransitResultDao(Context context) {
        super(context);
    }
    
    private String toTimeTypeString(TimeType tt) {
        if (tt == TimeType.DEPARTURE) {
            return "departure";
        }
        else if (tt == TimeType.ARRIVAL) {
            return "arrival";
        }
        else if (tt == TimeType.FIRST) {
            return "first";
        }
        else if (tt == TimeType.LAST) {
            return "last";
        }
        else  {
            throw new IllegalArgumentException("予期していないTimeTypeです。");
        }
    }
    
    private TimeType toTimeType(String tt) {
        if ("departure".equals(tt)) {
            return TimeType.DEPARTURE;
        }
        else if ("arrival".equals(tt)) {
            return TimeType.ARRIVAL;
        }
        else if ("first".equals(tt)) {
            return TimeType.FIRST;
        }
        else if ("last".equals(tt)) {
            return TimeType.LAST;
        }
        else  {
            throw new IllegalArgumentException("予期していないTimeTypeです。" + tt);
        }
    }
    
    public long getTransitResultIdForAlarm() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", new String[]{"_id"}, 
                    "alarm_status=?", new String[]{String.valueOf(ALARM_STATUS_BEING_SET)}, null, null, "_id", "1");
            if (c.getCount() != 1) {
                return -1;
            }
            
            c.moveToFirst();
            long ret = c.getLong("_id");
            c.close();
            return ret;
        }
        finally {
            db.close();
        }
        
    }
    
    public int getTransitResultCountByAlarmStatus(int alarmStatus) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", new String[]{"_id"}, 
                    "alarm_status=?", new String[]{String.valueOf(alarmStatus)}, null, null, null);
            int ret = c.getCount();
            c.close();
            return ret;
        }
        finally {
            db.close();
        }
    }

    public List<TransitResultEx> getTransitResultsByAlarmStatus(int alarmStatus) {
        List<TransitResultEx> ret = new ArrayList<TransitResultEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", null, 
                    "alarm_status=?", new String[]{String.valueOf(alarmStatus)}, null, null, "_id desc");
            while (c.moveToNext()) {
                ret.add(loadSimpleTransitResult(db, c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }
    
    public List<TransitResultEx> getTransitResults() {
        List<TransitResultEx> ret = new ArrayList<TransitResultEx>();
        
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", null, null, null, null, null, "_id desc");
            while (c.moveToNext()) {
                ret.add(loadSimpleTransitResult(db, c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }

    private TransitResultEx loadSimpleTransitResult(SQLiteDatabase db, CursorEx c) {
        TransitResultEx ret = new TransitResultEx();
        ret.setId(c.getLong("_id"));
        ret.setDisplayName(c.getString("display_name"));
        ret.setQueryDate(DateUtils.toDate(c.getLong("query_date")));
        ret.setTimeType(toTimeType(c.getString("time_type")));
        ret.setAlarmStatus(c.getInt("alarm_status"));
        ret.setAlarmAt(c.getLong("alarm_at"));
        ret.setCreatedAt(c.getLong("created_at"));

        String time = c.getString("time");
        if (StringUtils.isNotEmpty(time)) {
            ret.setTime(new Time(time));
        }
        
        ret.setFrom(c.getString("transit_from"));
        ret.setTo(c.getString("transit_to"));
        ret.setPrefecture(c.getString("prefecture"));
        ret.setTransits(getTransits(db, ret.getId()));
        
        return ret;
    }

    public TransitResultEx getTransitResult(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
            if (c.getCount() != 1) {
                return null;
            }
            
            c.moveToFirst();
            TransitResultEx ret = loadSimpleTransitResult(db, c);
            c.close();
            return ret;
        }
        finally {
            db.close();
        }
    }
    
    private List<Transit> getTransits(SQLiteDatabase db, long transitResultId) {
        List<Transit> ret = new ArrayList<Transit>();
        
        CursorEx c = (CursorEx) db.query("transit", null, "transit_result_id=?", new String[]{String.valueOf(transitResultId)}, null, null, null);
        while (c.moveToNext()) {
            Transit t = new Transit();
            long transitId = c.getLong("_id");
            String durationAndFare = c.getString("duration_and_fare");
            if (StringUtils.isNotEmpty(durationAndFare)) {
                String[] split = durationAndFare.split("-");
                t.setDuration(split[0].trim());
                t.setFare(split[1].trim());
            }

            for (Iterator<TransitDetail> it = getTransitDetails(db, transitId).iterator(); it.hasNext();) {
                TransitDetail td = it.next();
                t.addDetail(td);
            }
            ret.add(t);
        }
        c.close();
        
        return ret;
    }
    
    private List<Long> getTransitIds(SQLiteDatabase db, long transitResultId) {
        List<Long> ret = new ArrayList<Long>();
        
        CursorEx c = (CursorEx) db.query("transit", null, "transit_result_id=?", new String[]{String.valueOf(transitResultId)}, null, null, null);
        while (c.moveToNext()) {
            ret.add(c.getLong("_id"));
        }
        c.close();
        
        return ret;
    }

    private List<TransitDetail> getTransitDetails(SQLiteDatabase db, long transitId) {
        List<TransitDetail> ret = new ArrayList<TransitDetail>();
        
        CursorEx c = (CursorEx) db.query("transit_detail", null, "transit_id=?", new String[]{String.valueOf(transitId)}, null, null, "detail_no");
        while (c.moveToNext()) {
            TransitDetail td = new TransitDetail();
            td.setRoute(c.getString("route"));
            String dTime = c.getString("departure_time");
            String dPlace = c.getString("departure_place");
            if (StringUtils.isNotEmpty(dTime) && StringUtils.isNotEmpty(dPlace)) {
                TimeAndPlace departure = new TimeAndPlace(new Time(dTime), dPlace);
                td.setDeparture(departure);
            }
            String aTime = c.getString("arrival_time");
            String aPlace = c.getString("arrival_place");
            if (StringUtils.isNotEmpty(aTime) && StringUtils.isNotEmpty(aPlace)) {
                TimeAndPlace arrival = new TimeAndPlace(new Time(aTime), aPlace);
                td.setArrival(arrival);
            }
            ret.add(td);
        }
        c.close();
        
        return ret;
    }
    
    public int updateAlarmStatus(long transitResultId, int alarmStatus) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("alarm_status", ALARM_STATUS_FINISHED);
            return db.update("transit_result", values, "_id=?", new String[]{String.valueOf(transitResultId)});
        }
        finally {
            db.close();
        }
    }
    
    public int updateDisplayName(long transitResultId, String displayName) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("display_name", displayName);
            return db.update("transit_result", values, "_id=?", new String[]{String.valueOf(transitResultId)});
        }
        finally {
            db.close();
        }
    }
    
    public int deleteTransitResult(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            List<Long> ids = getTransitIds(db, id);
            StringBuilder sb = new StringBuilder();
            String[] params = new String[ids.size()];
            sb.append("transit_id in (");
            for (int i = 0; i < ids.size(); i ++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append("?");
                params[i] = String.valueOf(ids.get(i));
            }
            sb.append(") ");
            int ret = db.delete("transit_detail", sb.toString(), params);
            Log.i("SimpleTransit", ret + " rows deleted from transit_detail");
            
            ret = db.delete("transit", "transit_result_id=?", new String[]{String.valueOf(id)});
            Log.i("SimpleTransit", ret + " rows deleted from transit");
            
            ret = db.delete("transit_result", "_id=?", new String[]{String.valueOf(id)});
            Log.i("SimpleTransit", ret + " rows deleted from transit_result");
            if (ret != 1) {
                return ret;
            }
            
            db.setTransactionSuccessful();
            return ret;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public long createTransitResult(TransitResultEx tr, Transit t) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // transit_result
            ContentValues trv = new ContentValues();
            if (tr.getQueryDate() != null) {
                trv.put("query_date", DateUtils.toLong(tr.getQueryDate()));
            }
            trv.put("time_type", toTimeTypeString(tr.getTimeType()));
            if (tr.getTime() != null) {
                trv.put("time", tr.getTime().getTimeAsString());
            }
            trv.put("transit_from", tr.getFrom());
            trv.put("transit_to", tr.getTo());
            trv.put("prefecture", tr.getPrefecture());
            trv.put("alarm_status", tr.getAlarmStatus());
            trv.put("alarm_at", tr.getAlarmAt());
            trv.put("created_at", getCurrentDateTime());
            long trId = db.insertOrThrow("transit_result", null, trv);
            if (trId < 0) {
                return -1;
            }
            
            // transit
            ContentValues tv = new ContentValues();
            tv.put("transit_result_id", trId);
            tv.put("duration_and_fare", t.getDuration() + " - " + t.getFare());
            long tId = db.insertOrThrow("transit", null, tv);
            if (tId < 0) {
                return -1;
            }
            
            // transit_details
            for (int i = 0; i < t.getDetails().size(); i ++) {
                TransitDetail td = t.getDetails().get(i);
                
                ContentValues tdv = new ContentValues();
                tdv.put("transit_id", tId);
                tdv.put("detail_no", i + 1);
                tdv.put("route", td.getRoute());
                if (!td.isWalking()) {
                    tdv.put("departure_time", td.getDeparture().getTime().getTimeAsString());
                    tdv.put("departure_place", td.getDeparture().getPlace());
                    if (td.getArrival() != null) {
                        tdv.put("arrival_time", td.getArrival().getTime().getTimeAsString());
                        tdv.put("arrival_place", td.getArrival().getPlace());
                    }
                }
                
                db.insertOrThrow("transit_detail", null, tdv);
            }
            
            db.setTransactionSuccessful();
            return trId;
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
    
}
