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

import java.util.ArrayList;

import jp.co.hybitz.jorudanlive.model.JorudanLiveResult;
import jp.co.hybitz.jorudanlive.model.LiveInfo;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.action.OptionMenuHandler;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class JorudanLiveListActivity extends ListActivity implements SimpleTransitConst {
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);
    private JorudanLiveArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Preferences.initTheme(this);
        setTitle(getTitle() + "　ジョルダンライブ！");
        adapter = new JorudanLiveArrayAdapter(this, R.layout.jorudan_live_list, new ArrayList<JorudanLiveItem>());
        registerForContextMenu(getListView());
        showList();
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
        if (optionMenuHandler.onMenuItemSelected(featureId, item)) {
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }

    private void showList() {
        JorudanLiveResult result = (JorudanLiveResult) getIntent().getExtras().getSerializable(EXTRA_KEY_JORUDAN_LIVE_RESULT);
        
        for (LiveInfo li : result.getLiveInfoList()) {
            JorudanLiveItem item = new JorudanLiveItem(li);
            adapter.add(item);
        }

        setListAdapter(adapter);
    }

}
