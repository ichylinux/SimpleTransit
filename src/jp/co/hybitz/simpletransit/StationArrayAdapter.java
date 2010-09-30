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
        view.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Station s = item.getStation();
                if (s == null) {
                    return;
                }
                
                SimpleTransit st = (SimpleTransit) getContext();
                if (item.isFrom()) {
                    st.updateFrom(s.getName());
                }
                else {
                    st.updateTo(s.getName());
                }
            }
        });

        TextView name = (TextView) view.findViewWithTag("name");
        name.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        if (item.getStation() != null) {
            name.setText(item.getStation().getName());
        }

        View bottomLayout = view.findViewWithTag("bottom_layout");
        bottomLayout.setVisibility(View.GONE);
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        StationItem item = getItem(info.position);
        if (item.getStation() != null) {
            menu.add(0, MENU_ITEM_SET_FROM, 1, "出発地に設定");
            menu.add(0, MENU_ITEM_SET_TO, 2, "到着地に設定");
            menu.add(0, MENU_ITEM_CANCEL, 3, "キャンセル");
        }
    }
}
