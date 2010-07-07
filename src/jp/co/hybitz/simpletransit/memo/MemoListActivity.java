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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.alarm.AlarmPlayActivity;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.menu.OptionMenuHandler;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class MemoListActivity extends ListActivity implements SimpleTransitConst {
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Preferences.initTheme(this);
        registerForContextMenu(getListView());
        showList();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        showList();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MemoListItem item = (MemoListItem) l.getItemAtPosition(position);

        Intent intent = new Intent(this, AlarmPlayActivity.class);
        intent.putExtra(EXTRA_KEY_TRANSIT, item.getResult().getId());
        startActivity(intent);
    }

    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.clearHeader();
        menu.add(0, MENU_ITEM_DELETE, 0, "削除");
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_PREFERENCES, 1, "設定");
        menu.add(0, MENU_ITEM_QUIT, 2, "終了");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return optionMenuHandler.onMenuItemSelected(featureId, item);
    }

    /**
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == MENU_ITEM_DELETE) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
            
            MemoListItem item = (MemoListItem) getListView().getItemAtPosition(info.position);
            int count = new TransitResultDao(this).deleteTransitResult(item.getResult().getId());
            if (count == 1) {
                showList();
            }
        }

        return super.onContextItemSelected(menuItem);
    }

    private void showList() {
        setListAdapter(new MemoArrayAdapter(this, R.layout.memo_list, getItems()));
    }

    private List<MemoListItem> getItems() {
        boolean alarmOnly = getIntent().getBooleanExtra(EXTRA_KEY_ALARM_ONLY, false);
        
        List<MemoListItem> ret = new ArrayList<MemoListItem>();
        
        TransitResultDao dao = new TransitResultDao(this);
        List<SimpleTransitResult> list = alarmOnly ? dao.getTransitResultsByAlarmStatus(ALARM_STATUS_BEING_SET) : dao.getTransitResults();
        if (!list.isEmpty()) {
            for (Iterator<SimpleTransitResult> it = list.iterator(); it.hasNext();) {
                SimpleTransitResult str = it.next();
                ret.add(new MemoListItem(str));
            }
        }
        
        return ret;
    }
}
