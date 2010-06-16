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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.db.TransitQueryDao;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class QueryHistoryListActivity extends ListActivity implements SimpleTransitConst {
    
    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
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

    /**
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
        SimpleTransitQuery query = (SimpleTransitQuery) map.get("transit_query");
        if (query == null) {
            return;
        }
        
        Intent result = new Intent();
        result.putExtra(EXTRA_KEY_TRANSIT_QUERY, query);
        setResult(RESULT_OK, result);
        finish();
    }
    
    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, MENU_ITEM_DELETE, 0, "削除");
    }

    /**
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ITEM_DELETE) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo(); 
            
            Map<String, Object> map = (Map<String, Object>) getListView().getItemAtPosition(info.position);
            SimpleTransitQuery query = (SimpleTransitQuery) map.get("transit_query");
            
            int count = new TransitQueryDao(this).deleteTransitQuery(query.getId());
            if (count == 1) {
                showList();
            }
        }

        return super.onContextItemSelected(item);
    }
    
    private void showList() {
        setListAdapter(new SimpleAdapter(this, getData(),
                android.R.layout.simple_list_item_1, new String[] { "from_to" },
                new int[] { android.R.id.text1 }));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        
        List<SimpleTransitQuery> list = new TransitQueryDao(this).getTransitQueries();
        for (Iterator<SimpleTransitQuery> it = list.iterator(); it.hasNext();) {
            SimpleTransitQuery query = it.next();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("from_to", query.getFrom() + " ～ " + query.getTo());
            map.put("transit_query", query);
            ret.add(map);
        }
        
        return ret;
    }
}
