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

import jp.co.hybitz.simpletransit.R;
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        QueryHistoryListItem item = (QueryHistoryListItem) l.getItemAtPosition(position);
        
        Intent result = new Intent();
        result.putExtra(EXTRA_KEY_TRANSIT_QUERY, item.getQuery());
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
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == MENU_ITEM_DELETE) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
            
            QueryHistoryListItem item = (QueryHistoryListItem) getListView().getItemAtPosition(info.position);
            int count = new TransitQueryDao(this).deleteTransitQuery(item.getQuery().getId());
            if (count == 1) {
                showList();
            }
        }

        return super.onContextItemSelected(menuItem);
    }
    
    private void showList() {
        setListAdapter(new QueryHistoryArrayAdapter(this, R.layout.query_history_list, getItems()));
    }

    private List<QueryHistoryListItem> getItems() {
        List<QueryHistoryListItem> ret = new ArrayList<QueryHistoryListItem>();
        
        List<SimpleTransitQuery> list = new TransitQueryDao(this).getTransitQueries();
        for (Iterator<SimpleTransitQuery> it = list.iterator(); it.hasNext();) {
            SimpleTransitQuery query = it.next();
            ret.add(new QueryHistoryListItem(query));
        }
        
        return ret;
    }
}
