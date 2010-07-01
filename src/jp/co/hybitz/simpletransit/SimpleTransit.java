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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.benishouga.common.AndroidExceptionHandler;
import jp.co.hybitz.android.DialogUtils;
import jp.co.hybitz.android.ToastUtils;
import jp.co.hybitz.googletransit.Platform;
import jp.co.hybitz.googletransit.TransitSearchException;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.TransitUtil;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.simpletransit.alarm.AlarmPlayActivity;
import jp.co.hybitz.simpletransit.alarm.AlarmSettingDialog;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.history.QueryHistoryListActivity;
import jp.co.hybitz.simpletransit.history.QueryHistoryWorker;
import jp.co.hybitz.simpletransit.memo.MemoListActivity;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import jp.co.hybitz.simpletransit.model.TimeTypeAndDate;
import jp.co.hybitz.simpletransit.model.TransitItem;
import jp.co.hybitz.util.StringUtils;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity implements SimpleTransitConst {
    private ExceptionHandler exceptionHandler = new ExceptionHandler(this);
    private ResultRenderer resultRenderer = new ResultRenderer(this);
    private SimpleTransitQuery query = new SimpleTransitQuery();
    private TimeTypeAndDate currentTime;
    private TimeTypeAndDate previousTime;
    private TimeTypeAndDate nextTime;

	/**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidExceptionHandler.bind(this, APP_ID);
        initView();
        initActions();
    }
    
    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_QUERY_HISTORY, 1, "検索履歴");
        menu.add(0, MENU_ITEM_VOICE, 2, "音声入力");
        menu.add(0, MENU_ITEM_ALARM, 3, "アラーム");
        menu.add(0, MENU_ITEM_MEMO, 4, "メモ");
        menu.add(0, MENU_ITEM_PREFERENCES, 5, "設定");
        menu.add(0, MENU_ITEM_QUIT, 6, "終了");
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
        case MENU_ITEM_QUERY_HISTORY :
            showQueryHistoryList();
            return true;
        case MENU_ITEM_ALARM :
            showMemoList(true);
            return true;
        case MENU_ITEM_MEMO :
            showMemoList(false);
            return true;
        case MENU_ITEM_VOICE :
            voiceInput();
            return true;
        case MENU_ITEM_QUIT :
            finish();
            return true;
        default :
            return super.onMenuItemSelected(featureId, item);
        }
    }
    
    /**
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_TRANSIT_QUERY) {
            if (resultCode == RESULT_OK) {
                query = (SimpleTransitQuery) data.getExtras().getSerializable(EXTRA_KEY_TRANSIT_QUERY);
                updateQueryView();
            }
        }
        else if (requestCode == REQUEST_CODE_VOICE_INPUT) {
            if (resultCode == RESULT_OK) {
            	String from = null;
            	String to = null;
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for (int i = 0; i < results.size(); i ++) {
                	String result = results.get(i);
                	String[] split = result.split(" ");
                	for (int j = 0; j < split.length; j ++) {
                		if (from == null) {
                			from = split[j];
                		}
                		else if (to == null) {
                			to = split[j];
                		}
                		else {
                			break;
                		}
                	}
                	
                	if (from != null && to != null) {
                		break;
                	}
                }
                
                query.setFrom(from);
                query.setTo(to);
                updateQueryView();
            }
        }
    }
    
    private void voiceInput() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "出発地・到着地を叫んでください！");
            startActivityForResult(intent, REQUEST_CODE_VOICE_INPUT);
        }
        catch (ActivityNotFoundException e) {
            ToastUtils.toastLong(this, "音声入力に対応していません。");
        }        
    }
    
    private void initView() {
        setContentView(R.layout.main);
        query.setTimeType(TimeType.DEPARTURE);
        updatePreviousTimeAndNextTimeVisibility();
    }

    private void showQueryHistoryList() {
        Intent intent = new Intent(this, QueryHistoryListActivity.class);
        intent.putExtra(EXTRA_KEY_TRANSIT_QUERY, query);
        startActivityForResult(intent, REQUEST_CODE_SELECT_TRANSIT_QUERY);
    }
    
    private void showMemoList(boolean alarmOnly) {
        if (alarmOnly) {
            TransitResultDao dao = new TransitResultDao(this);
            int count = dao.getTransitResultCountByAlarmStatus(ALARM_STATUS_BEING_SET);
            if (count == 0) {
                ToastUtils.toast(this, "アラームは設定されていません。");
                return;
            }
            else if (count == 1) {
                Intent intent = new Intent(this, AlarmPlayActivity.class);
                intent.putExtra(EXTRA_KEY_TRANSIT, dao.getTransitResultIdForAlarm());
                startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(this, MemoListActivity.class);
        intent.putExtra(EXTRA_KEY_ALARM_ONLY, alarmOnly);
        startActivity(intent);
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
        registerForContextMenu(lv);
    }
    
    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, MENU_ITEM_MEMO_CREATE, 1, "メモとして保存");
        menu.add(0, MENU_ITEM_ALARM_CREATE, 2, "アラームをセット");
    }

    /**
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
        ResultListView lv = (ResultListView) findViewById(R.id.results);
        TransitItem ti = lv.getTransitItem(info.position);

        if (menuItem.getItemId() == MENU_ITEM_ALARM_CREATE) {
            AlarmSettingDialog dialog = new AlarmSettingDialog(this, ti.getTransitResult(), ti.getTransit());
            dialog.show();
        }
        else if (menuItem.getItemId() == MENU_ITEM_MEMO_CREATE) {
            SimpleTransitResult str = new SimpleTransitResult(ti.getTransitResult());
            new TransitResultDao(this).createTransitResult(str, ti.getTransit());
            ToastUtils.toast(this, "メモに保存しました。");
        }

        return super.onContextItemSelected(menuItem);
    }

    private void showTimeDialog() {
    	final TimeDialog dialog = new TimeDialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface di) {
			    Date selected = TransitUtil.getRelativeDate(dialog.getTime(), true);
			    if (selected != null) {
	                currentTime = new TimeTypeAndDate(dialog.getTimeType(), selected);
			    } else {
			        currentTime = null;
			    }
                renderSelectedTime();
			}
		});
    	
    	if (currentTime != null) {
    	    dialog.setTimeType(currentTime.getTimeType());
    	    dialog.setTime(TransitUtil.getTime(currentTime.getDate()));
    	}
    	dialog.show();
    }
    
    private void renderSelectedTime() {
        TextView timeView = (TextView) findViewById(R.id.time);
        if (currentTime != null) {
            timeView.setText(TransitUtil.getTime(currentTime.getDate()) + "に" + (currentTime.getTimeType() == TimeType.DEPARTURE ? "出発" : "到着"));
        }
        else {
            timeView.setText(null);
        }
    }
    
    private void updateQueryView() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        CheckBox first = (CheckBox) findViewById(R.id.first);
        CheckBox last = (CheckBox) findViewById(R.id.last);
        TextView time = (TextView) findViewById(R.id.time);

        from.setText(query.getFrom());
        to.setText(query.getTo());
        first.setChecked(false);
        last.setChecked(false);
        time.setText(null);
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
            if (currentTime != null) {
                query.setTimeType(currentTime.getTimeType());
                query.setDate(currentTime.getDate());
            } else {
                query.setTimeType(TimeType.DEPARTURE);
                query.setDate(null);
            }
        }

        query.setUseExpress(Preferences.isUseExpress(this));
        query.setUseAirline(Preferences.isUseAirline(this));
    }
    
    private void searchPrevious() {
        query.setDate(previousTime.getDate());
        search();
    }
    
    private void searchNext() {
        query.setDate(nextTime.getDate());
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
            TransitResult result = searcher.search(query.getTransitQuery());
            
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 検索結果を表示
                resultRenderer.render(result);
                
                // 前の時刻と次の時刻を取得
                updatePreviousTimeAndNextTime(result);
            }
            else {
                showResponseCode(result.getResponseCode());
            }
            
            // 検索条件を履歴として保存
            if (result.getTransitCount() > 0) {
                new Thread(new QueryHistoryWorker(this, query)).start();
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
                    nextTime = new TimeTypeAndDate(query.getTimeType(), c.getTime());
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
                    previousTime = new TimeTypeAndDate(query.getTimeType(), c.getTime());
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
            previous.setEnabled(previousTime.getDate().after(new Date()));
        }
        
        Button next = (Button) findViewById(R.id.next_time);
        next.setVisibility(nextTime != null ? View.VISIBLE : View.INVISIBLE);
    }
    
    private void showResponseCode(int responseCode) {
        DialogUtils.showMessage(this, "連絡", "Googleの応答が「" + responseCode + "」でした。。", "しかたないね");
    }
    
}