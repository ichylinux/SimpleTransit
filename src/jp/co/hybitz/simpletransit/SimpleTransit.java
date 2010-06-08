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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.co.hybitz.android.DialogUtils;
import jp.co.hybitz.googletransit.Platform;
import jp.co.hybitz.googletransit.TransitSearchException;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.TransitUtil;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.TransitQuery;
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.simpletransit.alarm.AlarmListActivity;
import jp.co.hybitz.simpletransit.alarm.AlarmPlayActivity;
import jp.co.hybitz.simpletransit.alarm.AlarmSettingDialog;
import jp.co.hybitz.simpletransit.db.SimpleTransitDao;
import jp.co.hybitz.simpletransit.model.TransitItem;
import jp.co.hybitz.util.StringUtils;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity implements SimpleTransitConst {
    private static final int MENU_ITEM_PREFERENCES = Menu.FIRST + 1;
    private static final int MENU_ITEM_ALARM = Menu.FIRST + 2;
    private static final int MENU_ITEM_QUIT = Menu.FIRST + 3;

    private ExceptionHandler exceptionHandler = new ExceptionHandler(this);
    private ResultRenderer resultRenderer = new ResultRenderer(this);
    private TransitQuery query = new TransitQuery();
    private Date currentTime;
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
        
        initActions();
    }
    
    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_PREFERENCES, 0, "設定");
        menu.add(0, MENU_ITEM_ALARM, 1, "アラーム");
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
        case MENU_ITEM_ALARM :
            showAlarmList();
            return true;
        case MENU_ITEM_QUIT :
            finish();
            return true;
        default :
            return super.onMenuItemSelected(featureId, item);
        }
    }
    
    private void showAlarmList() {
        SimpleTransitDao dao = new SimpleTransitDao(this);
        int count = dao.getTransitResultCountByAlarmStatus(ALARM_STATUS_SET);
        if (count == 1) {
            Intent intent = new Intent(this, AlarmPlayActivity.class);
            intent.putExtra(EXTRA_KEY_TRANSIT, dao.getTransitResultIdForAlarm());
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, AlarmListActivity.class);
            startActivity(intent);
        }
    }
    
    private void initActions() {
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
                searchNew();
            }
        });
        
        Button previous = (Button) findViewById(R.id.previous_time);
        previous.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                searchPrevious();
            }
        });
        
        Button next = (Button) findViewById(R.id.next_time);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                searchNext();
            }
        });
        
        ListView lv = (ListView) findViewById(R.id.results);
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TransitItem ti = (TransitItem) parent.getItemAtPosition(position);
                showAlarmSettingDialog(ti);
                return false;
            }
        });
    }
    
    private void showAlarmSettingDialog(TransitItem ti) {
        AlarmSettingDialog dialog = new AlarmSettingDialog(SimpleTransit.this, ti.getTransitResult(), ti.getTransit());
        dialog.show();
    }

    private void showTimeDialog() {
    	final TimeDialog dialog = new TimeDialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface di) {
                query.setTimeType(dialog.getTimeType());
                currentTime = TransitUtil.getRelativeDate(dialog.getTime(), true);
                renderSelectedTime();
			}
		});
    	
	    dialog.setTimeType(query.getTimeType());
	    dialog.setTime(TransitUtil.getTime(currentTime));
    	dialog.show();
    }
    
    private void renderSelectedTime() {
        TextView timeView = (TextView) findViewById(R.id.time);
        if (currentTime != null) {
            timeView.setText(TransitUtil.getTime(currentTime) + "に" + (query.getTimeType() == TimeType.DEPARTURE ? "出発" : "到着"));
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
        }
        else if (last.isChecked()) {
            query.setTimeType(TimeType.LAST);
            query.setDate(null);
        }
        else {
            query.setDate(currentTime);
        }

        query.setUseExpress(Preferences.isUseExpress(this));
        query.setUseAirline(Preferences.isUseAirline(this));
    }
    
    private void searchPrevious() {
        query.setDate(previousTime);
        search();
    }
    
    private void searchNext() {
        query.setDate(nextTime);
        search();
    }
    
    private void searchNew() {
        updateQuery();
        search();
    }
    
    private boolean validateQuery() {
        if (StringUtils.isEmpty(query.getFrom())) {
            DialogUtils.showMessage(this, R.string.error_from_required);
            return false;
        }
        
        if (StringUtils.isEmpty(query.getTo())) {
            DialogUtils.showMessage(this, R.string.error_to_required);
            return false;
        }
        
        return true;
    }
    
    private void search() {
        if (!validateQuery()) {
            return;
        }

        try {
            TransitSearcher searcher = TransitSearcherFactory.createSearcher(Platform.ANDROID);
            TransitResult result = searcher.search(query);
            
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 検索結果を表示
                resultRenderer.render(result);
                
                // 前の時刻と次の時刻を取得
                updatePreviousTimeAndNextTime(result);
            }
            else {
                showResponseCode(result.getResponseCode());
            }
            
        } catch (TransitSearchException e) {
            exceptionHandler.handleException(e);
        }
    }
    
    private void updatePreviousTimeAndNextTime(TransitResult result) {
        nextTime = null;
        previousTime = null;
        
        if (result != null && result.getTransitCount() > 0) {
            if (query.getTimeType() == TimeType.DEPARTURE) {
                Time t = TransitUtil.getFirstDepartureTime(result);
                if (t != null) {
                    Date date = TransitUtil.getRelativeDate(t, true);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.MINUTE, 1);
                    nextTime = c.getTime();
                    Log.i("SimpleTransit", "次の時刻=" + new SimpleDateFormat("yyyyMMddHHmm").format(nextTime));
                }
                
                List<Time> times = TransitUtil.getDepartureTimes(result);
                Collections.sort(times);
                if (times.size() > 2) {
                    
                }
            }
            else if (query.getTimeType() == TimeType.ARRIVAL) {
                Time t = TransitUtil.getLastArrivalTime(result);
                if (t != null) {
                    Date date = TransitUtil.getRelativeDate(t, false);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.MINUTE, -1);
                    previousTime = c.getTime();
                    Log.i("SimpleTransit", "前の時刻=" + new SimpleDateFormat("yyyyMMddHHmm").format(previousTime));
                }
            }
        }
        
        updatePreviousTimeAndNextTimeVisibility();
    }
    
    private void updatePreviousTimeAndNextTimeVisibility() {
        Button previous = (Button) findViewById(R.id.previous_time);
        if (previousTime == null) {
            previous.setVisibility(View.INVISIBLE);
        }
        else {
            previous.setVisibility(View.VISIBLE);
            previous.setEnabled(previousTime.after(new Date()));
        }
        
        Button next = (Button) findViewById(R.id.next_time);
        next.setVisibility(nextTime != null ? View.VISIBLE : View.INVISIBLE);
    }
    
    private void showResponseCode(int responseCode) {
        DialogUtils.showMessage(this, "連絡", "Googleの応答が「" + responseCode + "」でした。。", "しかたないね");
    }
    
}