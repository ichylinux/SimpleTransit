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
package jp.co.hybitz.simpletransit;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.transit.model.Station;
import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class StationArrayAdapter extends ArrayAdapterEx<StationItem> implements SimpleTransitConst {

    public StationArrayAdapter(Context context, List<StationItem> stations) {
        super(context, R.layout.station_list, stations);
    }

    @Override
    protected void updateView(View view, final StationItem item) {
        TextView name = (TextView) view.findViewWithTag("name");
        name.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        if (item.getStation() != null) {
            name.setText(item.getStation().getName());
        }
        else {
            name.setText(null);
        }
        name.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Station s = item.getStation();
                if (s == null) {
                    return;
                }
                
                SimpleTransit st = (SimpleTransit) getContext();
                switch (item.getStationType()) {
                case StationItem.STATION_TYPE_FROM :
                    st.updateFrom(s.getName());
                    break;
                case StationItem.STATION_TYPE_TO :
                    st.updateTo(s.getName());
                    break;
                case StationItem.STATION_TYPE_STOPOVER :
                    st.updateStopOver(s.getName());
                    break;
                }
            }
        });
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        StationItem item = getItem(info.position);
        if (item.getStation() != null) {
            int order = 0;
            menu.add(0, MENU_ITEM_SET_FROM, ++order, "出発地に設定");
            menu.add(0, MENU_ITEM_SET_TO, ++order, "到着地に設定");
            if (Preferences.isUseStopOver(getContext())) {
                menu.add(0, MENU_ITEM_SET_STOPOVER, ++order, "経由地に設定");
            }
            menu.add(0, MENU_ITEM_CANCEL, ++order, "キャンセル");
        }
    }
}
