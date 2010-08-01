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
    private static final int DB_VERSION = 7;

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
        default :
            break;
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
    
}
