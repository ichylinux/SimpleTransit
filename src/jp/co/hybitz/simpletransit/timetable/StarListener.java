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

import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class StarListener implements View.OnClickListener {
    private Bitmap[] images;
    private TimeTableEx timeTable;
    
    public StarListener(Bitmap[] images, TimeTableEx timeTable) {
        this.images = images;
        this.timeTable = timeTable;
    }
    
    public void onClick(View v) {
        timeTable.setFavorite(!timeTable.isFavorite());

        ImageView star = (ImageView) v;
        int count = new TimeTableResultDao(star.getContext()).updateFavorite(timeTable);
        if (count == 1) {
            star.setImageBitmap(timeTable.isFavorite() ? images[1] : images[0]);
        }
    }
}
