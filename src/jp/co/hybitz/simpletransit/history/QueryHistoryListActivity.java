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
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class QueryHistoryListActivity extends ListActivity implements SimpleTransitConst {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showList();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        showList();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
        SimpleTransitQuery query = (SimpleTransitQuery) map.get("transit_query");
        
        Intent result = new Intent();
        result.putExtra(EXTRA_KEY_TRANSIT_QUERY, query);
        setResult(RESULT_OK, result);
        finish();
    }
    
    private void showList() {
        setListAdapter(new SimpleAdapter(this, getData(),
                android.R.layout.simple_list_item_1, new String[] { "from_to" },
                new int[] { android.R.id.text1 }));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        
        TransitQueryDao dao = new TransitQueryDao(this);
        List<SimpleTransitQuery> list = dao.getTransitQueries();
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
