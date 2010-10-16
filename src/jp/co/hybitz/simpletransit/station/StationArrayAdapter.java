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
package jp.co.hybitz.simpletransit.station;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransit;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.stationapi.model.Station;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class StationArrayAdapter extends ArrayAdapterEx<Station> implements SimpleTransitConst {

    public StationArrayAdapter(SimpleTransit context, List<Station> items) {
        super(context, R.layout.near_station_list, items);
    }

    @Override
    protected void updateView(View view, final Station item) {
        view.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SimpleTransit st = (SimpleTransit) getContext();
                st.updateFrom(item.getName());
            }
        });

        TextView name = (TextView) view.findViewWithTag("name");
        name.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        name.setText(item.getName());

        View bottomLayout = view.findViewWithTag("bottom_layout");
        bottomLayout.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        
        TextView line = (TextView) view.findViewWithTag("line");
        line.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        line.setText(item.getLine());

        TextView directionAndDistance = (TextView) view.findViewWithTag("direction_and_distance");
        directionAndDistance.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        directionAndDistance.setText(item.getDirection() + "へ" + item.getDistanceM());
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        int order = 0;
        menu.add(0, MENU_ITEM_SET_FROM, ++order, "出発地に設定");
        menu.add(0, MENU_ITEM_SET_TO, ++order, "到着地に設定");
        if (Preferences.isUseStopOver(getContext())) {
            menu.add(0, MENU_ITEM_SET_STOPOVER, ++order, "経由地に設定");
        }
        menu.add(0, MENU_ITEM_CANCEL, ++order, "キャンセル");
    }
}
