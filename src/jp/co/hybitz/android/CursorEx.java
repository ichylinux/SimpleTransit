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
package jp.co.hybitz.android;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class CursorEx extends SQLiteCursor {

    public CursorEx(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        super(db, driver, editTable, query);
    }

    public byte[] getBlob(String columnName) {
        return super.getBlob(getColumnIndex(columnName));
    }
    
    public boolean getBoolean(String columnName) {
        int ret = getInt(columnName);
        return ret != 0;
    }

    public double getDouble(String columnName) {
        return super.getDouble(getColumnIndex(columnName));
    }

    public float getFloat(String columnName) {
        return super.getFloat(getColumnIndex(columnName));
    }

    public int getInt(String columnName) {
        return super.getInt(getColumnIndex(columnName));
    }

    public long getLong(String columnName) {
        return super.getLong(getColumnIndex(columnName));
    }

    public short getShort(String columnName) {
        return super.getShort(getColumnIndex(columnName));
    }

    public String getString(String columnName) {
        return getString(getColumnIndex(columnName));
    }

    public boolean isBlob(String columnName) {
        return super.isBlob(getColumnIndex(columnName));
    }

    public boolean isNull(String columnName) {
        return super.isNull(getColumnIndex(columnName));
    }
}
