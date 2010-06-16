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
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitQueryDao extends AbstractDao {
    
    public TransitQueryDao(Context context) {
        super(context);
    }

    public List<SimpleTransitQuery> getTransitQueries() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            List<SimpleTransitQuery> ret = new ArrayList<SimpleTransitQuery>(); 

            CursorEx c = (CursorEx) db.query("transit_query", null, null, null, null, null, "use_count desc");
            while (c.moveToNext()) {
                ret.add(loadTransitQuery(c));
            }
            c.close();

            return ret;
        }
        finally {
            db.close();
        }
    }
    
    private SimpleTransitQuery loadTransitQuery(CursorEx c) {
        SimpleTransitQuery ret = new SimpleTransitQuery();
        ret.setId(c.getLong("_id"));
        ret.setFrom(c.getString("transit_from"));
        ret.setTo(c.getString("transit_to"));
        ret.setTimeType(TimeType.DEPARTURE);
        ret.setUseCount(c.getInt("use_count"));
        ret.setCreatedAt(c.getLong("created_at"));
        return ret;
    }

    public SimpleTransitQuery getTransitQuery(String from, String to) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            CursorEx c = (CursorEx) db.query("transit_query", null, 
                    "transit_from=? and transit_to=?", new String[]{from, to}, null, null, null);

            SimpleTransitQuery ret = null;
            
            if (c.getCount() > 0) {
                c.moveToFirst();
                ret = loadTransitQuery(c);
            }
            
            c.close();
            return ret;
        }
        finally {
            db.close();
        }
    }
    
    public long createTransitQuery(SimpleTransitQuery tq) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("transit_from", tq.getFrom());
            values.put("transit_to", tq.getTo());
            values.put("created_at", getCurrentDateTime());
            return db.insertOrThrow("transit_query", null, values);
        }
        finally {
            db.close();
        }
    }
    
    public int deleteTransitQuery(long id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete("transit_query", "_id=?", new String[]{String.valueOf(id)});
        }
        finally {
            db.close();
        }
    }

    public int updateUseCount(long id, int useCount) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("use_count", useCount);
            return db.update("transit_query", values, "_id=?", new String[]{String.valueOf(id)});
        }
        finally {
            db.close();
        }
    }
}
