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
package jp.co.hybitz.simpletransit.traveldelay;

import java.util.ArrayList;

import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.action.OptionMenuHandler;
import jp.co.hybitz.traveldelay.model.Category;
import jp.co.hybitz.traveldelay.model.TravelDelay;
import jp.co.hybitz.traveldelay.model.TravelDelayQuery;
import jp.co.hybitz.traveldelay.model.TravelDelayResult;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TravelDelayListActivity extends ListActivity implements SimpleTransitConst {
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);
    private TravelDelayArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Preferences.initTheme(this);
        setTitle(getTitle() + "　Goo運行情報");
        adapter = new TravelDelayArrayAdapter(this, R.layout.travel_delay_list, new ArrayList<TravelDelayItem>());
        registerForContextMenu(getListView());
        showList();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        TravelDelayItem item = (TravelDelayItem) l.getItemAtPosition(position);
        if (item.getCategory() != null) {
            TravelDelayQuery query = new TravelDelayQuery();
            query.setCategory(item.getCategory());
            new TravelDelayTask(this).execute(query);
        }
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
        TravelDelayResult result = (TravelDelayResult) getIntent().getExtras().getSerializable(EXTRA_KEY_TRAVEL_DELAY_RESULT);
        
        for (Category c : result.getCategories()) {
            if (StringUtils.isNotEmpty(c.getName())) {
                if (c.isInternational()) {
                    continue;
                }
                
                TravelDelayItem item = new TravelDelayItem(c);
                adapter.add(item);
            }
            else {
                for (TravelDelay td : c.getOperationCompanies().get(0).getTravelDelays()) {
                    TravelDelayItem tdi = new TravelDelayItem(td);
                    adapter.add(tdi);
                }
            }            
        }

        setListAdapter(adapter);
    }

}
