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
package jp.co.hybitz.simpletransit.action;

import jp.co.hybitz.simpletransit.db.TransitQueryDao;
import jp.co.hybitz.simpletransit.history.QueryHistoryTabActivity;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class StarListener implements View.OnClickListener {
    private Bitmap[] images;
    private SimpleTransitQuery query;
    
    public StarListener(Bitmap[] images, SimpleTransitQuery query) {
        this.images = images;
        this.query = query;
    }
    
    public void onClick(View v) {
        ImageView star = (ImageView) v;
        int count = new TransitQueryDao(star.getContext()).updateFavorite(query.getId(), !query.isFavorite());
        if (count == 1) {
            query.setFavorite(!query.isFavorite());
            star.setImageBitmap(query.isFavorite() ? images[1] : images[0]);
            if ( star.getContext() instanceof QueryHistoryTabActivity) {
                QueryHistoryTabActivity activity = (QueryHistoryTabActivity) star.getContext();
                activity.showList();
            }
        }
    }
}
