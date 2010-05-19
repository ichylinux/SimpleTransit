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

import java.util.Iterator;
import java.util.List;

import jp.co.hybitz.simpletransit.model.Transit;
import jp.co.hybitz.simpletransit.model.TransitDetail;
import jp.co.hybitz.simpletransit.model.TransitQuery;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity {

	/**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SimpleTransit.this.onClick(v);
			}
		});
    }
    
    private TransitQuery createQuery() {
    	EditText from = (EditText) findViewById(R.id.from);
    	EditText to = (EditText) findViewById(R.id.to);

    	TransitQuery query = new TransitQuery();
    	query.setFrom(from.getText().toString());
    	query.setTo(to.getText().toString());
    	return query;
    }
    
    private void onClick(View v) {
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.listview);

		List<Transit> list = new TransitSearcher().search(createQuery());
		aa.add("検索結果は " + list.size() + " 件です。");
		
		for (Iterator<Transit> it = list.iterator(); it.hasNext();) {
			Transit transit = it.next();
			aa.add(createResult(transit));
		}
		
		ListView lv = (ListView) findViewById(R.id.results);
		lv.setAdapter(aa);
    }
    
    private String createResult(Transit transit) {
    	StringBuilder sb = new StringBuilder();

    	sb.append(transit.getTitle());
    	if (transit.getTransferCount() > 0) {
    		sb.append(" - 乗り換え" + transit.getTransferCount() + "回");
    	}
    	sb.append("\n");

    	for (int i = 0; i < transit.getDetails().size(); i ++) {
    		TransitDetail detail = transit.getDetails().get(i);
    		
    		sb.append(detail.getRoute());

    		if (!detail.isWalking()) {
    			sb.append("\n");
	    		sb.append(detail.getDeparture().getTime()).append("発　").append(detail.getDeparture().getPlace()).append("\n");
	    		sb.append(detail.getArrival().getTime()).append("着　").append(detail.getArrival().getPlace());
    		}

    		if (i < transit.getDetails().size() - 1) {
    			sb.append("\n");
    		}
    	}
    	return sb.toString();
    }
}