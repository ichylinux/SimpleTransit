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
package jp.co.hybitz.simpletransit.alarm;

import java.util.List;

import jp.co.hybitz.simpletransit.ResultRenderer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class AlarmArrayAdapter extends ArrayAdapter<AlarmListItem> {
    private LayoutInflater inflater;
    private int textViewResourceId;

    public AlarmArrayAdapter(Context context, int textViewResourceId, List<AlarmListItem> items) {
        super(context, textViewResourceId, items);
        this.textViewResourceId = textViewResourceId;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(textViewResourceId, null);
        }

        AlarmListItem item = getItem(position);
        TextView title = (TextView) view.findViewWithTag("title");
        title.setText(ResultRenderer.createTitle(item.getResult().getTransitResult()));
        TextView alarmAt = (TextView) view.findViewWithTag("alarm_at");
        alarmAt.setText("アラーム時刻： " + AlarmUtils.toDateTimeString(item.getResult().getAlarmAt()));

        return view;
    }
}
