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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.ResultRenderer;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.db.SimpleTransitDao;
import jp.co.hybitz.simpletransit.model.AlarmTransitResult;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
        AlarmTransitResult atr = (AlarmTransitResult) map.get("transit_result");

        // アラーム設定なし表示が選択された場合
        if (atr == null) {
            return;
        }

        Intent intent = new Intent(this, AlarmPlayActivity.class);
        intent.putExtra(EXTRA_KEY_TRANSIT, atr.getId());
        startActivity(intent);
    }
    
    private void showList() {
        setListAdapter(new SimpleAdapter(this, getData(),
                android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        
        SimpleTransitDao dao = new SimpleTransitDao(this);
        List<AlarmTransitResult> list = dao.getTransitResultsByAlarmStatus(ALARM_STATUS_SET);
        if (list.isEmpty()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", getString(R.string.tv_alarm_title_alarm_not_set));
            ret.add(map);
        }
        else {
            for (Iterator<AlarmTransitResult> it = list.iterator(); it.hasNext();) {
                AlarmTransitResult atr = it.next();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", ResultRenderer.createTitle(atr));
                map.put("transit_result", atr);
                ret.add(map);
            }
        }
        
        return ret;
        
    }
}
