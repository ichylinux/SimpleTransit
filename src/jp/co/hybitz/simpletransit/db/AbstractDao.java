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
import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public abstract class AbstractDao {

    private Context context;

    public AbstractDao(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
    
    protected long getCurrentDateTime() {
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return Long.parseLong(now);
    }
    
    protected SQLiteDatabase getReadableDatabase() {
        return new SimpleTransitDbHelper(context).getReadableDatabase();
    }
    
    protected SQLiteDatabase getWritableDatabase() {
        return new SimpleTransitDbHelper(context).getWritableDatabase();
    }
}
