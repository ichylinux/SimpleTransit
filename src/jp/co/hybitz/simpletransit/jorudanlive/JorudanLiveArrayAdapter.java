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
package jp.co.hybitz.simpletransit.jorudanlive;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class JorudanLiveArrayAdapter extends ArrayAdapterEx<JorudanLiveItem> implements SimpleTransitConst {

    public JorudanLiveArrayAdapter(Context context, int textViewResourceId, List<JorudanLiveItem> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    protected void updateView(View view, JorudanLiveItem item) {
        TextView title = (TextView) view.findViewWithTag("title");
        title.setText(item.getTitle());
        title.setBackgroundResource(Preferences.getBackgroundResource(getContext()));

        TextView summary = (TextView) view.findViewWithTag("summary");
        summary.setText(item.getSummary());
        summary.setBackgroundResource(Preferences.getBackgroundResource(getContext()));

        View bottomLayout = view.findViewWithTag("bottom_layout");
        bottomLayout.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        
        TextView condition = (TextView) view.findViewWithTag("detail");
        condition.setText(item.getDetail());
        condition.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
    }
}
