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
package jp.co.hybitz.simpletransit.alarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmListActivity extends ListActivity implements SimpleTransitConst {

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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlarmListItem item = (AlarmListItem) l.getItemAtPosition(position);

        Intent intent = new Intent(this, AlarmPlayActivity.class);
        intent.putExtra(EXTRA_KEY_TRANSIT, item.getResult().getId());
        startActivity(intent);
    }
    
    private void showList() {
        setListAdapter(new AlarmArrayAdapter(this, R.layout.alarm_list, getItems()));
    }

    private List<AlarmListItem> getItems() {
        List<AlarmListItem> ret = new ArrayList<AlarmListItem>();
        
        TransitResultDao dao = new TransitResultDao(this);
        List<SimpleTransitResult> list = dao.getTransitResultsByAlarmStatus(ALARM_STATUS_SET);
        if (!list.isEmpty()) {
            for (Iterator<SimpleTransitResult> it = list.iterator(); it.hasNext();) {
                SimpleTransitResult str = it.next();
                ret.add(new AlarmListItem(str));
            }
        }
        
        return ret;
    }
}
