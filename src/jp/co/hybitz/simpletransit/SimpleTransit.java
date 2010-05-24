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

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import jp.co.hybitz.googletransit.Platform;
import jp.co.hybitz.googletransit.TransitSearchException;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitDetail;
import jp.co.hybitz.googletransit.model.TransitQuery;
import jp.co.hybitz.googletransit.model.TransitResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity {
    private static final int MENU_ITEM_PREFERENCES = Menu.FIRST + 1;
    private static final int MENU_ITEM_QUIT = Menu.FIRST + 2;
    
	private TimeType timeType;
	private Time time;

	/**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView time = (TextView) findViewById(R.id.time);
        time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTimeDialog();
			}
		});
        
        CheckBox last = (CheckBox) findViewById(R.id.last);
        last.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TextView timeView = (TextView) findViewById(R.id.time);
				timeView.setEnabled(!isChecked);
				if (timeView.isEnabled()) {
				    timeView.setTextColor(Color.BLACK);
                    timeView.setBackgroundResource(R.layout.time_border_enabled);
				}
				else {
                    timeView.setTextColor(Color.GRAY);
				    timeView.setBackgroundResource(R.layout.time_border_disabled);
				}
			}
		});
        
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				search();
			}
		});
    }
    
    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_PREFERENCES, 0, "設定");
        menu.add(0, MENU_ITEM_QUIT, 1, "終了");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_PREFERENCES :
            startActivity(new Intent(this, Preferences.class));
            return true;
        case MENU_ITEM_QUIT :
            finish();
            return true;
        default :
            return super.onMenuItemSelected(featureId, item);
        }
    }

    private void showTimeDialog() {
    	final TimeDialog dialog = new TimeDialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface di) {
				TextView timeView = (TextView) findViewById(R.id.time);
				time = dialog.getTime();
                timeType = dialog.getTimeType();
				
				if (timeType != null && time != null) {
				    timeView.setText(time.getTimeAsString(true) + "に" + (timeType == TimeType.DEPARTURE ? "出発" : "到着"));
				}
				else {
				    timeView.setText(null);
				}
			}
		});
    	
	    dialog.setTimeType(timeType);
		dialog.setTime(time);
    	dialog.show();
    }
    
	private void renderResult(TransitResult result) {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.listview);
        aa.add(createSummary(result));
        
        for (Iterator<Transit> it = result.getTransits().iterator(); it.hasNext();) {
            Transit transit = it.next();
            aa.add(createResult(transit));
        }
        
        ListView lv = (ListView) findViewById(R.id.results);
        lv.setAdapter(aa);
    }
    
    private TransitQuery createQuery() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        CheckBox last = (CheckBox) findViewById(R.id.last);

        TransitQuery query = new TransitQuery();
        query.setFrom(from.getText().toString());
        query.setTo(to.getText().toString());
        if (last.isChecked()) {
            query.setTimeType(TimeType.LAST);
        }
        else {
            query.setTimeType(timeType);

            if (time != null) {
            	query.setDate(getDate());
            	query.setTime(time.getTimeAsString());
            }
        }
        query.setUseExpress(Preferences.isUseExpress(this));
        query.setUseAirline(Preferences.isUseAirline(this));

        return query;
    }
    
    private String getDate() {
        Calendar c = Calendar.getInstance();

        String now = new SimpleDateFormat("hhmm").format(c.getTime());
        if (now.compareTo(time.getTimeAsString()) < 0) {
        	return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
        }
        else {
        	c.add(Calendar.DATE, 1);
        	return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
        }
    }
    
    private void search() {
        try {
            TransitSearcher searcher = TransitSearcherFactory.createSearcher(Platform.ANDROID);
            TransitResult result = searcher.search(createQuery());
            
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                renderResult(result);
            } else {
                showResponseCode(result.getResponseCode());
            }
            
        } catch (TransitSearchException e) {
            Log.e("SimpleTransit", e.getMessage(), e);
            apologize(e);
        }
        
    }
    
    private void apologize(TransitSearchException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ごめん！！");
        builder.setMessage("こんなエラー出た。。\n" + e.getCause().getClass().getSimpleName() + "\n" + e.getMessage());
        builder.setPositiveButton("許す", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    
    private void showResponseCode(int responseCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("連絡");
        builder.setMessage("Googleの応答が「" + responseCode + "」でした。。");
        builder.setPositiveButton("許す", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private String createSummary(TransitResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getTransitCount() > 0) {
            sb.append(result.getTitle()).append("\n");
            sb.append("検索結果は " + result.getTransitCount() + " 件です。");
        } else {
            sb.append("該当するルートが見つかりませんでした。");
        }
        return sb.toString();
    }
    
    private String createResult(Transit transit) {
    	StringBuilder sb = new StringBuilder();

    	sb.append(transit.getTimeAndFare());
    	if (transit.getTransferCount() > 0) {
    		sb.append(" - 乗り換え" + transit.getTransferCount() + "回");
    	}
    	sb.append("\n");

    	for (int i = 0; i < transit.getDetails().size(); i ++) {
    		TransitDetail detail = transit.getDetails().get(i);
    		
    		sb.append(detail.getRoute());

    		if (!detail.isWalking()) {
    			sb.append("\n");
	    		sb.append(detail.getDeparture().getTime().getTimeAsString(true)).append("発　");
	    		sb.append(detail.getDeparture().getPlace()).append("\n");
	    		sb.append(detail.getArrival().getTime().getTimeAsString(true)).append("着　");
	    		sb.append(detail.getArrival().getPlace());
    		}

    		if (i < transit.getDetails().size() - 1) {
    			sb.append("\n");
    		}
    	}
    	return sb.toString();
    }
}