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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.co.hybitz.googletransit.Platform;
import jp.co.hybitz.googletransit.TransitSearchException;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.TransitUtil;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.TransitQuery;
import jp.co.hybitz.googletransit.model.TransitResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity {
    private static final int MENU_ITEM_PREFERENCES = Menu.FIRST + 1;
    private static final int MENU_ITEM_QUIT = Menu.FIRST + 2;

    private TransitQuery query = new TransitQuery();
    private Date previousTime;
    private Date nextTime;

	/**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        query.setTimeType(TimeType.DEPARTURE);
        updatePreviousTimeAndNextTimeVisibility();
        
        TextView time = (TextView) findViewById(R.id.time);
        time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTimeDialog();
			}
		});
        
        FirstAndLastCheckBoxListener flListener = new FirstAndLastCheckBoxListener(this);
        CheckBox first = (CheckBox) findViewById(R.id.first);
        first.setOnCheckedChangeListener(flListener);
        CheckBox last = (CheckBox) findViewById(R.id.last);
        last.setOnCheckedChangeListener(flListener);
        
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				search();
			}
		});
        
        Button previous = (Button) findViewById(R.id.previous_time);
        previous.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (previousTime != null) {
                    String date = new SimpleDateFormat("yyyyMMddHHmm").format(previousTime);
                    query.setDate(date.substring(0, 8));
                    query.setTime(new Time(date.substring(8, 10), date.substring(10, 12)));
                    search();
                }
            }
        });
        
        Button next = (Button) findViewById(R.id.next_time);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (nextTime != null) {
                    String date = new SimpleDateFormat("yyyyMMddHHmm").format(nextTime);
                    query.setDate(date.substring(0, 8));
                    query.setTime(new Time(date.substring(8, 10), date.substring(10, 12)));
                    search();
                }
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
                query.setTimeType(dialog.getTimeType());
                query.setDate(getDate(dialog.getTime(), true));
				query.setTime(dialog.getTime());
                renderSelectedTime();
			}
		});
    	
	    dialog.setTimeType(query.getTimeType());
		dialog.setTime(query.getTime());
    	dialog.show();
    }
    
    private void renderSelectedTime() {
        TextView timeView = (TextView) findViewById(R.id.time);
        if (query.getTime() != null) {
            timeView.setText(query.getTime().getTimeAsString(true) + "に" + (query.getTimeType() == TimeType.DEPARTURE ? "出発" : "到着"));
        }
        else {
            timeView.setText(null);
        }
    }
    
    private void updateQuery() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        CheckBox first = (CheckBox) findViewById(R.id.first);
        CheckBox last = (CheckBox) findViewById(R.id.last);

        query.setFrom(from.getText().toString());
        query.setTo(to.getText().toString());
        
        if (first.isChecked()) {
            query.setTimeType(TimeType.FIRST);
            query.setDate(null);
            query.setTime(null);
        }
        else if (last.isChecked()) {
            query.setTimeType(TimeType.LAST);
            query.setDate(null);
            query.setTime(null);
        }
        query.setUseExpress(Preferences.isUseExpress(this));
        query.setUseAirline(Preferences.isUseAirline(this));
    }
    
    private String getDate(Time time, boolean incrementDate) {
        Calendar c = Calendar.getInstance();

        String now = new SimpleDateFormat("HHmm").format(c.getTime());
        if (now.compareTo(time.getTimeAsString()) < 0) {
        	return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
        }
        else {
            if (incrementDate) {
            	c.add(Calendar.DATE, 1);
            	return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
            }
            else {
                return null;
            }
        }
    }
    
    private void search() {
        try {
            updateQuery();
            TransitSearcher searcher = TransitSearcherFactory.createSearcher(Platform.ANDROID);
            TransitResult result = searcher.search(query);
            
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 検索結果を表示
                new ResultRenderer(this).render(result);
                
                // 前の時刻と次の時刻を取得
                updatePreviousTimeAndNextTime(result);
            }
            else {
                showResponseCode(result.getResponseCode());
            }
            
        } catch (TransitSearchException e) {
            Log.e("SimpleTransit", e.getMessage(), e);
            apologize(e);
        }
    }
    
    private void updatePreviousTimeAndNextTime(TransitResult result) {
        nextTime = null;
        previousTime = null;
        
        if (result != null && result.getTransitCount() > 0) {
            if (query.getTimeType() == TimeType.DEPARTURE) {
                Time t = TransitUtil.getFirstDepartureTime(result);
                if (t != null) {
                    String date = query.getDate();
                    if (date == null) {
                        date = getDate(t, true);
                    }
                    
                    if (date != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                            Date d = sdf.parse(date + t.getTimeAsString());
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.MINUTE, 1);
                            nextTime = c.getTime();
                            Log.i("SimpleTransit", "次の時刻=" + sdf.format(nextTime));
                        }
                        catch (ParseException e) {
                            Log.w("SimpleTransit", e.getMessage());
                        }
                    }
                }
            }
            else if (query.getTimeType() == TimeType.ARRIVAL) {
                Time t = TransitUtil.getLastArrivalTime(result);
                if (t != null) {
                    String date = query.getDate();
                    if (date == null) {
                        date = getDate(t, false);
                    }
                    
                    if (date != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                            Date d = sdf.parse(date + t.getTimeAsString());
                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            c.add(Calendar.MINUTE, -1);
                            previousTime = c.getTime();
                            Log.i("SimpleTransit", "前の時刻=" + sdf.format(previousTime));
                        }
                        catch (ParseException e) {
                            Log.w("SimpleTransit", e.getMessage());
                        }
                    }
                }
            }
        }
        
        updatePreviousTimeAndNextTimeVisibility();
    }
    
    private void updatePreviousTimeAndNextTimeVisibility() {
        Button previous = (Button) findViewById(R.id.previous_time);
        previous.setVisibility(previousTime != null ? View.VISIBLE : View.INVISIBLE);
        
        Button next = (Button) findViewById(R.id.next_time);
        next.setVisibility(nextTime != null ? View.VISIBLE : View.INVISIBLE);
    }
    
    /**
     * エラーが出たので謝ります。。
     * 
     * @param e
     */
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

}