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

import jp.co.hybitz.android.CursorFactoryEx;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransitDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "simple_transit.db";
    private static final int DB_VERSION = 1;

    public SimpleTransitDbHelper(Context context) {
        super(context, DB_NAME, new CursorFactoryEx(), DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder tr = new StringBuilder();
        tr.append("create table transit_result ( ");
        tr.append("_id integer primary key autoincrement, ");
        tr.append("alarm_status integer not null default 0, ");
        tr.append("time_type text not null, ");
        tr.append("time text, ");
        tr.append("transit_from text not null, ");
        tr.append("transit_to text not null, ");
        tr.append("prefecture text ");
        tr.append(") ");
        db.execSQL(tr.toString());

        StringBuilder t = new StringBuilder();
        t.append("create table transit ( ");
        t.append("_id integer primary key autoincrement, ");
        t.append("transit_result_id integer not null, ");
        t.append("duration_and_fare text not null ");
        t.append(") ");
        db.execSQL(t.toString());
        
        StringBuilder td = new StringBuilder();
        td.append("create table transit_detail ( ");
        td.append("transit_id integer not null, ");
        td.append("detail_no integer not null, ");
        td.append("route text not null, ");
        td.append("departure_time text, ");
        td.append("departure_place text, ");
        td.append("arrival_time text, ");
        td.append("arrival_place text ");
        td.append(") ");
        db.execSQL(td.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        
    }

}
