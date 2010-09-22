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
    private static final int DB_VERSION = 18;

    /**
     * コンストラクタ
     * 
     * @param context
     */
    public SimpleTransitDbHelper(Context context) {
        super(context, DB_NAME, new CursorFactoryEx(), DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableTransitQuery(db);
        createTableTransitResult(db);
        createTableTransit(db);
        createTableTransitDetail(db);
        createTableArea(db);
        createTablePrefecture(db);
        createTableLine(db);
        createTableStation(db);
        createTableTimeTable(db);
        createTableTimeLine(db);
        createIndexTimeTableIdOnTimeLine(db);
        createTableTransitTime(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
        case 1 :
            upgradeFrom1To2(db);
        case 2 :
            upgradeFrom2To3(db);
        case 3 :
            upgradeFrom3To4(db);
        case 4 :
            upgradeFrom4To5(db);
        case 5 :
            upgradeFrom5To6(db);
        case 6 :
            upgradeFrom6To7(db);
        case 7 :
            upgradeFrom7To8(db);
        case 8 :
            upgradeFrom8To9(db);
        case 9 :
            upgradeFrom9To10(db);
        case 10 :
            upgradeFrom10To11(db);
        case 11 :
            upgradeFrom11To12(db);
        case 12 :
            upgradeFrom12To13(db);
        case 13 :
            upgradeFrom13To14(db);
        case 14 :
            upgradeFrom14To15(db);
        case 15 :
            upgradeFrom15To16(db);
        case 16 :
            upgradeFrom16To17(db);
        case 17 :
            upgradeFrom17To18(db);
        default :
        }
    }

    private void upgradeFrom1To2(SQLiteDatabase db) {
        db.execSQL("alter table transit_result add column alarm_at integer not null default 0 ");
        db.execSQL("alter table transit_result add column created_at integer not null default 0 ");
    }
    
    private void upgradeFrom2To3(SQLiteDatabase db) {
        createTableTransitQuery(db);
    }
    
    private void upgradeFrom3To4(SQLiteDatabase db) {
        db.execSQL("alter table transit_result add column query_date integer ");
    }

    private void upgradeFrom4To5(SQLiteDatabase db) {
        db.execSQL("alter table transit_query add column updated_at integer not null default 0 ");
    }

    private void upgradeFrom5To6(SQLiteDatabase db) {
        db.execSQL("alter table transit_query add column is_favorite integer not null default 0 ");
    }

    private void upgradeFrom6To7(SQLiteDatabase db) {
        db.execSQL("alter table transit_query add column used_at integer not null default 0 ");
        db.execSQL("update transit_query set used_at = updated_at ");
    }

    private void upgradeFrom7To8(SQLiteDatabase db) {
        createTableArea(db);
        createTablePrefecture(db);
    }

    private void upgradeFrom8To9(SQLiteDatabase db) {
        createTableLine(db);
    }

    private void upgradeFrom9To10(SQLiteDatabase db) {
        createTableStation(db);
    }

    private void upgradeFrom10To11(SQLiteDatabase db) {
        createTableTimeTable(db);
        createTableTimeLine(db);
        createTableTransitTime(db);
        db.execSQL("delete from area ");
        db.execSQL("delete from prefecture ");
        db.execSQL("delete from line ");
        db.execSQL("delete from station ");
        db.execSQL("delete from time_table ");
        db.execSQL("delete from time_line ");
        db.execSQL("delete from transit_time ");
    }

    private void upgradeFrom11To12(SQLiteDatabase db) {
        try {
            db.execSQL("alter table station add column is_favorite integer not null default 0 ");
        }
        catch (Exception e) {
        }
    }

    private void upgradeFrom12To13(SQLiteDatabase db) {
        try {
            db.execSQL("alter table time_table add column is_favorite integer not null default 0 ");
        }
        catch (Exception e) {
        }
    }

    private void upgradeFrom13To14(SQLiteDatabase db) {
        try {
            db.execSQL("alter table station add column is_favorite integer not null default 0 ");
        }
        catch (Exception e) {
        }
    }

    private void upgradeFrom14To15(SQLiteDatabase db) {
        try {
            db.execSQL("alter table line add column display_order integer not null default 0 ");
        }
        catch (Exception e) {
        }
    }

    private void upgradeFrom15To16(SQLiteDatabase db) {
        try {
            db.execSQL("alter table line add column display_order integer not null default 0 ");
        }
        catch (Exception e) {
        }
    }

    private void upgradeFrom16To17(SQLiteDatabase db) {
        try {
            db.execSQL("alter table transit_result add column display_name text ");
        }
        catch (Exception e) {
        }
        try {
            db.execSQL("alter table time_table add column line_name text ");
        }
        catch (Exception e) {
        }
    }

    private void upgradeFrom17To18(SQLiteDatabase db) {
        try {
            db.execSQL("alter table time_table add column line_name text ");
        }
        catch (Exception e) {
        }
    }

    private void createTableTransitQuery(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table transit_query ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("transit_from text not null, ");
        sb.append("transit_to text not null, ");
        sb.append("is_favorite integer not null default 0, ");
        sb.append("use_count integer not null default 0, ");
        sb.append("used_at integer not null default 0, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }

    private void createTableTransitResult(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table transit_result ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("query_date integer, ");
        sb.append("time_type text not null, ");
        sb.append("time text, ");
        sb.append("transit_from text not null, ");
        sb.append("transit_to text not null, ");
        sb.append("prefecture text, ");
        sb.append("display_name text, ");
        sb.append("alarm_status integer not null default 0, ");
        sb.append("alarm_at integer not null default 0, ");
        sb.append("created_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTableTransit(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table transit ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("transit_result_id integer not null, ");
        sb.append("duration_and_fare text not null ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTableTransitDetail(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table transit_detail ( ");
        sb.append("transit_id integer not null, ");
        sb.append("detail_no integer not null, ");
        sb.append("route text not null, ");
        sb.append("departure_time text, ");
        sb.append("departure_place text, ");
        sb.append("arrival_time text, ");
        sb.append("arrival_place text ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTableArea(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table area ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("name text not null, ");
        sb.append("url text, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTablePrefecture(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table prefecture ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("area_id integer not null, ");
        sb.append("name text not null, ");
        sb.append("url text not null, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTableLine(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table line ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("prefecture_id integer not null, ");
        sb.append("name text not null, ");
        sb.append("company text, ");
        sb.append("url text not null, ");
        sb.append("display_order integer not null default 0, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTableStation(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table station ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("line_id integer not null, ");
        sb.append("name text not null, ");
        sb.append("url text not null, ");
        sb.append("is_favorite integer not null default 0, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }

    private void createTableTimeTable(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table time_table ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("line_name text, ");
        sb.append("station_id integer not null, ");
        sb.append("direction text not null, ");
        sb.append("type integer not null, ");
        sb.append("is_favorite integer not null default 0, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createTableTimeLine(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table time_line ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("time_table_id integer not null, ");
        sb.append("hour integer not null, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
    
    private void createIndexTimeTableIdOnTimeLine(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create index index_time_table_id on time_line( time_table_id ) ");
        db.execSQL(sb.toString());
    }

    private void createTableTransitTime(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table transit_time ( ");
        sb.append("_id integer primary key autoincrement, ");
        sb.append("time_line_id integer not null, ");
        sb.append("hour integer not null, ");
        sb.append("minute integer not null, ");
        sb.append("transit_class text, ");
        sb.append("bound_for text, ");
        sb.append("created_at integer not null default 0, ");
        sb.append("updated_at integer not null default 0 ");
        sb.append(") ");
        db.execSQL(sb.toString());
    }
}
