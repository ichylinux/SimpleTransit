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
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class QueryHistoryArrayAdapter extends ArrayAdapterEx<QueryHistoryListItem> {

    public QueryHistoryArrayAdapter(Context context, int textViewResourceId, List<QueryHistoryListItem> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    protected void updateView(View view, QueryHistoryListItem item) {
        TextView fromTo = (TextView) view.findViewWithTag("from_to");
        fromTo.setText(item.getQuery().getFrom() + " ～ " + item.getQuery().getTo());
        TextView useCount = (TextView) view.findViewWithTag("use_count");
        useCount.setText("利用回数： " + item.getQuery().getUseCount() + "回");
    }
}
