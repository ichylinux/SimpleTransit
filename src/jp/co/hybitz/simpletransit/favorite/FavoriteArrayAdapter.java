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
package jp.co.hybitz.simpletransit.favorite;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransit;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.common.model.Favorable;
import jp.co.hybitz.simpletransit.model.TransitQueryEx;
import jp.co.hybitz.simpletransit.timetable.TimeTableActivity;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class FavoriteArrayAdapter extends ArrayAdapterEx<Favorable> implements SimpleTransitConst {
    private SimpleTransit activity;

    public FavoriteArrayAdapter(SimpleTransit activity, List<Favorable> items) {
        super(activity, R.layout.favorite_list, items);
        this.activity = activity;
    }

    @Override
    protected void updateView(View view, final Favorable item) {
        TextView textView = (TextView) view.findViewWithTag("title");
        textView.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        textView.setText(getText(item));
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (item instanceof TransitQueryEx) {
                    TransitQueryEx query = (TransitQueryEx) item;
                    activity.updateLocations(query.getFrom(), query.getTo(), query.getStopOver());
                }
                else if (item instanceof TimeTableEx) {
                    TimeTableEx tt = (TimeTableEx) item;
                    Intent intent = new Intent(activity, TimeTableActivity.class);
                    intent.putExtra(EXTRA_KEY_TIME_TABLE, tt);
                    activity.startActivity(intent);
                }
            }
        });
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        Favorable item = getItem(info.position);
        if (item instanceof TransitQueryEx) {
            menu.add(0, MENU_ITEM_SET_FAVORITE, 1, "経路を設定");
            menu.add(0, MENU_ITEM_SET_FAVORITE_REVERSE, 2, "逆経路を設定");
            menu.add(0, MENU_ITEM_CANCEL, 3, "キャンセル");
        }
        else if (item instanceof TimeTableEx) {
            menu.add(0, MENU_ITEM_SET_FROM, 1, "出発地に設定");
            menu.add(0, MENU_ITEM_SET_TO, 2, "到着地に設定");
            menu.add(0, MENU_ITEM_CANCEL, 3, "キャンセル");
        }
    }
    
    private String getText(Favorable f) {
        if (f instanceof TransitQueryEx) {
            TransitQueryEx query = (TransitQueryEx) f;
            return query.getFromTo();
        }
        else if (f instanceof TimeTableEx) {
            TimeTableEx tt = (TimeTableEx) f;
            return tt.getStation().getName() + "　" + tt.getDirection() + "　" + tt.getTypeString();
        }
        
        return null;
    }
}
