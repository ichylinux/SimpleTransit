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
package jp.co.hybitz.simpletransit.timetable;

import java.util.List;

import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import android.content.Context;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class LineSaveWorker implements Runnable {

    private Context context;
    private long prefectureId;
    private List<LineEx> lines;
    
    public LineSaveWorker(Context context, long prefectureId, List<LineEx> lines) {
        this.context = context;
        this.prefectureId = prefectureId;
        this.lines = lines;
    }

    public void run() {
        TimeTableResultDao dao = new TimeTableResultDao(context);
        if (dao.getLines(prefectureId).isEmpty()) {
            dao.insertLines(prefectureId, lines);
        }
    }

}
