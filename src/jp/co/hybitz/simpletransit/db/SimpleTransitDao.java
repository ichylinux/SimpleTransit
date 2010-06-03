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

import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitDetail;
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.util.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransitDao {

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

    public TransitResult getTransitResult(long id) {
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getReadableDatabase();
        Cursor c = db.query("transit_result", null, "_id=" + String.valueOf(id), null, null, null, null);
        if (c.getCount() != 1) {
            db.close();
            return null;
        }
        
        c.moveToFirst();
        
        TransitResult ret = new TransitResult();
        ret.setTimeType(toTimeType(c.getString(c.getColumnIndex("time_type"))));

        String time = c.getString(c.getColumnIndex("time"));
        if (StringUtils.isNotEmpty(time)) {
            ret.setTime(new Time(time));
        }
        
        ret.setFrom(c.getString(c.getColumnIndex("transit_from")));
        ret.setTo(c.getString(c.getColumnIndex("transit_to")));
        ret.setPrefecture(c.getString(c.getColumnIndex("prefecture")));
        ret.setTransits(getTransitsByTransitResultId(db, id));
        
        db.close();
        return ret;
    }
    
    private List<Transit> getTransitsByTransitResultId(SQLiteDatabase db, long transitResultId) {
        List<Transit> ret = new ArrayList<Transit>();
        return ret;
    }
    
    public long createTransitResult(TransitResult tr, Transit t) {
        SQLiteDatabase db = new SimpleTransitDbHelper(context).getWritableDatabase();
        db.beginTransaction();
        
        // transit_result
        ContentValues trv = new ContentValues();
        trv.put("time_type", toTimeTypeString(tr.getTimeType()));
        if (tr.getTime() != null) {
            trv.put("time", tr.getTime().getTimeAsString());
        }
        trv.put("transit_from", tr.getFrom());
        trv.put("transit_to", tr.getTo());
        trv.put("prefecture", tr.getPrefecture());
        long trId = db.insertOrThrow("transit_result", null, trv);
        if (trId < 0) {
            db.endTransaction();
            db.close();
            return -1;
        }
        
        // transit
        ContentValues tv = new ContentValues();
        tv.put("transit_result_id", trId);
        tv.put("duration_and_fare", t.getDurationAndFare());
        long tId = db.insertOrThrow("transit", null, tv);
        if (tId < 0) {
            db.endTransaction();
            db.close();
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
        
        db.endTransaction();
        db.close();
        
        return trId;
    }
    
}
