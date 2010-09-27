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
package jp.co.hybitz.simpletransit.memo;

import java.util.List;

import jp.co.hybitz.android.ArrayAdapterEx;
import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.ResultRenderer;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.alarm.AlarmUtils;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class MemoArrayAdapter extends ArrayAdapterEx<MemoListItem> implements SimpleTransitConst {

    public MemoArrayAdapter(Context context, int textViewResourceId, List<MemoListItem> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    protected void updateView(View view, MemoListItem item) {
        TextView title = (TextView) view.findViewWithTag("title");
        title.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        if (StringUtils.isNotEmpty(item.getResult().getDisplayName())) {
            title.setText(item.getResult().getDisplayName());
        }
        else {
            title.setText(ResultRenderer.createTitle(item.getResult().getTransitResult(), false));
        }

        View bottomLayout = view.findViewWithTag("bottom_layout");
        bottomLayout.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        
        TextView queryDate = (TextView) view.findViewWithTag("query_date");
        queryDate.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
        if (item.getResult().getQueryDate() != null) {
            queryDate.setText(ResultRenderer.createDate(item.getResult().getTransitResult()));
        }
        else {
            queryDate.setText(ResultRenderer.createTime(item.getResult().getTransitResult()));
        }

        if (item.getResult().getAlarmStatus() == ALARM_STATUS_BEING_SET) {
            TextView alarmAt = (TextView) view.findViewWithTag("alarm_at");
            alarmAt.setBackgroundResource(Preferences.getBackgroundResource(getContext()));
            alarmAt.setText("アラーム：" + AlarmUtils.toDateTimeString(item.getResult().getAlarmAt()));
        }
    }
}
