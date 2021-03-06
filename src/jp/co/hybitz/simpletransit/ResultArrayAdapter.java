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
import jp.co.hybitz.simpletransit.model.TransitItem;
import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class ResultArrayAdapter extends ArrayAdapterEx<TransitItem> implements SimpleTransitConst {

    public ResultArrayAdapter(Context context, List<TransitItem> items) {
        super(context, R.layout.result_list, items);
    }

    @Override
    protected void updateView(View view, TransitItem item) {
        TextView textView = (TextView) view;
        textView.setTextSize(Preferences.getTextSize(getContext()));
        textView.setText(item.toString());
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, MENU_ITEM_COPY_TEXT, 1, "テキストをコピー");
        menu.add(0, MENU_ITEM_MEMO_CREATE, 2, "メモに保存");
        menu.add(0, MENU_ITEM_ALARM_CREATE, 3, "アラームをセット");
        menu.add(0, MENU_ITEM_CANCEL, 4, "キャンセル");
    }
}
