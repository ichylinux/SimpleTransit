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
package jp.co.hybitz.simpletransit.history;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.action.StarListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class RouteHistoryArrayAdapter extends ArrayAdapterEx<QueryHistoryListItem> {
    private Bitmap[] images;

    public RouteHistoryArrayAdapter(Context context, int textViewResourceId, List<QueryHistoryListItem> items) {
        super(context, textViewResourceId, items);
        Resources r = getContext().getResources();
        images = new Bitmap[]{
                BitmapFactory.decodeResource(r, R.drawable.star_off),
                BitmapFactory.decodeResource(r, R.drawable.star_on),
            };
    }

    @Override
    protected void updateView(View view, QueryHistoryListItem item) {
        view.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        
        ImageView star = (ImageView) view.findViewWithTag("star");
        star.setImageBitmap(item.getQuery().isFavorite() ? images[1] : images[0]);
        star.setOnClickListener(new StarListener(images, item.getQuery()));
        
        TextView fromTo = (TextView) view.findViewWithTag("from_to");
        fromTo.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        fromTo.setText(item.getQuery().getFromTo());

        TextView stopOver = (TextView) view.findViewWithTag("stopover");
        if (StringUtils.isNotEmpty(item.getQuery().getStopOver())) {
            stopOver.setText("　" + item.getQuery().getStopOver() + "経由");
            stopOver.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        }
        else {
            stopOver.setVisibility(View.INVISIBLE);
        }

        TextView useCount = (TextView) view.findViewWithTag("use_count");
        useCount.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        useCount.setText("利用回数: " + item.getQuery().getUseCount() + "回");
    }
}
