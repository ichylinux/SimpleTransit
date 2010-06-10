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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jp.co.hybitz.android.CursorEx;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeAndPlace;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitDetail;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.model.AlarmTransitResult;
import jp.co.hybitz.util.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransitDao implements SimpleTransitConst {

    private Context context;

    public SimpleTransitDao(Context context) {
        this.context = context;
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
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", new String[]{"_id"}, 
                    "alarm_status=?", new String[]{String.valueOf(ALARM_STATUS_SET)}, null, null, "_id", "1");
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
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getReadableDatabase();
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

    public List<AlarmTransitResult> getTransitResultsByAlarmStatus(int alarmStatus) {
        List<AlarmTransitResult> ret = new ArrayList<AlarmTransitResult>();
        
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", null, 
                    "alarm_status=?", new String[]{String.valueOf(alarmStatus)}, null, null, "_id");
            while (c.moveToNext()) {
                ret.add(loadAlarmTransitResult(db, c));
            }
            c.close();
        }
        finally {
            db.close();
        }
        
        return ret;
    }
    
    private AlarmTransitResult loadAlarmTransitResult(SQLiteDatabase db, CursorEx c) {
        AlarmTransitResult ret = new AlarmTransitResult();
        ret.setId(c.getLong("_id"));
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

    public AlarmTransitResult getTransitResult(long id) {
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_result", null, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
            if (c.getCount() != 1) {
                return null;
            }
            
            c.moveToFirst();
            AlarmTransitResult ret = loadAlarmTransitResult(db, c);
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
            t.setDurationAndFare(c.getString("duration_and_fare"));
            for (Iterator<TransitDetail> it = getTransitDetails(db, transitId).iterator(); it.hasNext();) {
                TransitDetail td = it.next();
                t.addDetail(td);
            }
            ret.add(t);
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
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("alarm_status", ALARM_STATUS_FINISHED);
            return db.update("transit_result", values, "_id=?", new String[]{String.valueOf(transitResultId)});
        }
        finally {
            db.close();
        }
        
    }
    
    private long getCurrentDateTime() {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return Long.parseLong(now);
    }
    
    public long createTransitResult(AlarmTransitResult tr, Transit t) {
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getWritableDatabase();
        db.beginTransaction();
        try {
            // transit_result
            ContentValues trv = new ContentValues();
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
            tv.put("duration_and_fare", t.getDurationAndFare());
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
                    tdv.put("arrival_time", td.getArrival().getTime().getTimeAsString());
                    tdv.put("arrival_place", td.getArrival().getPlace());
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
