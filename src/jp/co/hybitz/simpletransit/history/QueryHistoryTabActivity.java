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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.action.OptionMenuHandler;
import jp.co.hybitz.simpletransit.db.TransitQueryDao;
import jp.co.hybitz.simpletransit.model.Location;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class QueryHistoryTabActivity extends TabActivity implements SimpleTransitConst {
    private static final String TAG_ALL = "all";
    private static final String TAG_FAVORITE = "favorite";
    private static final String TAG_LOCATION = "location";
    
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);

    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    initView();
	    initAction();
	    showList();
	}
    
    /**
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        showList();
    }
    
    private void initView() {
        Preferences.initTheme(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.query_history_tab);

        TabHost th = getTabHost();
        registerForContextMenu(th);

        th.addTab(th.newTabSpec(TAG_FAVORITE).setIndicator("お気に入り").setContent(R.id.query_history_favorite));
        th.addTab(th.newTabSpec(TAG_ALL).setIndicator("検索履歴").setContent(R.id.query_history_all));
        th.addTab(th.newTabSpec(TAG_LOCATION).setIndicator("目的地履歴").setContent(R.id.query_history_location));
        th.setCurrentTab(1);
    }
    
    private void initAction() {
        ListView fab = (ListView) findViewById(R.id.query_history_favorite);
        fab.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QueryHistoryListItem item = (QueryHistoryListItem) parent.getItemAtPosition(position);
                
                Intent result = new Intent();
                result.putExtra(EXTRA_KEY_TRANSIT_QUERY, item.getQuery());
                setResult(RESULT_CODE_ROUTE_SELECTED, result);
                finish();
            }
        });
        registerForContextMenu(fab);

        ListView all = (ListView) findViewById(R.id.query_history_all);
    	all.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QueryHistoryListItem item = (QueryHistoryListItem) parent.getItemAtPosition(position);
                
                Intent result = new Intent();
                result.putExtra(EXTRA_KEY_TRANSIT_QUERY, item.getQuery());
                setResult(RESULT_CODE_ROUTE_SELECTED, result);
                finish();
            }
        });
    	registerForContextMenu(all);

        ListView location = (ListView) findViewById(R.id.query_history_location);
        registerForContextMenu(location);
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

    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.query_history_all) {
            menu.setHeaderTitle(Preferences.getText(this, "履歴の編集"));
            menu.add(0, MENU_ITEM_DELETE, 1, "削除");
            menu.add(0, MENU_ITEM_CANCEL, 2, "キャンセル");
        }
        else if (v.getId() == R.id.query_history_location) {
            menu.setHeaderTitle(Preferences.getText(this, "目的地の編集"));
            menu.add(0, MENU_ITEM_SET_FROM, 1, "出発地に設定");
            menu.add(0, MENU_ITEM_SET_TO, 2, "到着地に設定");
            menu.add(0, MENU_ITEM_CANCEL, 3, "キャンセル");
        }
    }

    /**
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == MENU_ITEM_CANCEL) {
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_DELETE) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
            
            ListView lv = (ListView) findViewById(R.id.query_history_all);
            QueryHistoryListItem item = (QueryHistoryListItem) lv.getItemAtPosition(info.position);
            int count = new TransitQueryDao(this).deleteTransitQuery(item.getQuery().getId());
            if (count == 1) {
                showList();
            }
            
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_FROM || menuItem.getItemId() == MENU_ITEM_SET_TO) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
            
            ListView lv = (ListView) findViewById(R.id.query_history_location);
            Location item = (Location) lv.getItemAtPosition(info.position);

            Intent result = new Intent();
            result.putExtra(EXTRA_KEY_LOCATION, item);
            setResult(getResultCode(menuItem.getItemId()), result);
            finish();
            
            return true;
        }

        return super.onContextItemSelected(menuItem);
    }
    
    private int getResultCode(int menuItemId) {
        switch (menuItemId) {
        case MENU_ITEM_SET_FROM :
            return RESULT_CODE_FROM_SELECTED;
        case MENU_ITEM_SET_TO :
            return RESULT_CODE_TO_SELECTED;
        default :
            throw new IllegalStateException("予期していないメニューアイテムIDです。menuItemId=" + menuItemId);
        }
        
    }
    
    public void showList() {
        ListView all = (ListView) findViewById(R.id.query_history_all);
        all.setAdapter(new RouteHistoryArrayAdapter(this, R.layout.query_history_route, getRoutes(false)));

        ListView fab = (ListView) findViewById(R.id.query_history_favorite);
        fab.setAdapter(new RouteHistoryArrayAdapter(this, R.layout.query_history_route, getRoutes(true)));
        
        ListView location = (ListView) findViewById(R.id.query_history_location);
        location.setAdapter(new LocationHistoryArrayAdapter(this, R.layout.query_history_location, getLocations()));
    }

    private List<QueryHistoryListItem> getRoutes(boolean favariteOnly) {
        List<QueryHistoryListItem> ret = new ArrayList<QueryHistoryListItem>();
        
        TransitQueryDao dao = new TransitQueryDao(this);
        List<SimpleTransitQuery> list = favariteOnly ?
                dao.getTransitQueriesByFavorite() : dao.getTransitQueries();
        for (Iterator<SimpleTransitQuery> it = list.iterator(); it.hasNext();) {
            SimpleTransitQuery query = it.next();
            ret.add(new QueryHistoryListItem(query));
        }
        
        return ret;
    }
    
    private List<Location> getLocations() {
        List<Location> ret = new ArrayList<Location>();
        
        List<Location> list = new TransitQueryDao(this).getLocations();
        for (Iterator<Location> it = list.iterator(); it.hasNext();) {
            Location location = it.next();
            ret.add(location);
        }
        
        return ret;
    }
}

