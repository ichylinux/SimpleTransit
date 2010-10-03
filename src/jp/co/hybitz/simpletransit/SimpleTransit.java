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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import jp.benishouga.common.AndroidExceptionHandler;
import jp.co.hybitz.android.ContextMenuAware;
import jp.co.hybitz.common.Engine;
import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.common.model.Time;
import jp.co.hybitz.simpletransit.action.FirstAndLastCheckBoxListener;
import jp.co.hybitz.simpletransit.action.FromToDragListener;
import jp.co.hybitz.simpletransit.alarm.AlarmSettingDialog;
import jp.co.hybitz.simpletransit.common.BaseActivity;
import jp.co.hybitz.simpletransit.common.model.Favorable;
import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.db.TransitQueryDao;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.favorite.FavoriteArrayAdapter;
import jp.co.hybitz.simpletransit.history.QueryHistoryWorker;
import jp.co.hybitz.simpletransit.model.Location;
import jp.co.hybitz.simpletransit.model.TimeTypeAndDate;
import jp.co.hybitz.simpletransit.model.TransitItem;
import jp.co.hybitz.simpletransit.model.TransitQueryEx;
import jp.co.hybitz.simpletransit.model.TransitResultEx;
import jp.co.hybitz.simpletransit.station.SearchNearStationsTask;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import jp.co.hybitz.simpletransit.util.ToastUtils;
import jp.co.hybitz.stationapi.model.Station;
import jp.co.hybitz.transit.TransitUtil;
import jp.co.hybitz.transit.model.TimeType;
import jp.co.hybitz.transit.model.TransitResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends BaseActivity implements SimpleTransitConst {
    private Engine engine = Engine.GOOGLE;
    private TransitQueryEx query = new TransitQueryEx();
    private TimeTypeAndDate selectedTime;
    private TimeTypeAndDate currentTime;
    private Stack<TimeTypeAndDate> previousTime = new Stack<TimeTypeAndDate>();
    private Stack<TimeTypeAndDate> nextTime = new Stack<TimeTypeAndDate>();
    private Button search;
    private LinearLayout searchDetails;
    private ListView results;
    private RelativeLayout searchButtons;
    private int orientationWhenStarted;
    private boolean showingResultsOnFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidExceptionHandler.bind(this, APP_ID);
        initFirstBoot();
        initView();
        initAction();
        initFavorite();
    }
    
    private void initFirstBoot() {
        if (!Preferences.isResultsOnFullScreenDefined(this)) {
            if (isSmallDevice()) {
                Preferences.setResultsOnFullScreen(this, true);
            }
        }
    }
    
    private boolean isSmallDevice() {
        Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        if (width <= 320 && height <= 240) {
            return true;
        }
        else if (width <= 240 && height <= 320) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        TextView summary = (TextView) findViewById(R.id.tv_summary);
        summary.setTextSize(Preferences.getTextSize(this));
        if (results.getAdapter() instanceof FavoriteArrayAdapter) {
            initFavorite();
        }
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_QUERY_HISTORY, 1, "検索履歴");
        menu.add(0, MENU_ITEM_MEMO, 2, "メモ");
        menu.add(0, MENU_ITEM_ALARM, 3, "アラーム");
        menu.add(0, MENU_ITEM_TRAVEL_DELAY, 4, "運行情報");
        menu.add(0, MENU_ITEM_TIME_TABLE, 5, "駅・時刻表");
        menu.add(0, MENU_ITEM_JORUDAN_LIVE, 6, "ジョルダンライブ！");
        menu.add(0, MENU_ITEM_VOICE, 7, "音声入力");
        menu.add(0, MENU_ITEM_SEARCH_NEAR_STATIONS, 8, "最寄駅検索");
        menu.add(0, MENU_ITEM_PREFERENCES, 9, "設定");
        menu.add(0, MENU_ITEM_QUIT, 10, "終了");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.results) {
            ListView results = (ListView) v;
            if (results.getAdapter() instanceof ContextMenuAware) {
                ((ContextMenuAware)results.getAdapter()).onCreateContextMenu(menu, v, menuInfo);
            }
        }
        else if (v.getId() == R.id.from || v.getId() == R.id.to) {
            menu.setHeaderTitle(Preferences.getText(this, "テキストを編集"));
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_REVERSE_LOCATION, 1, "逆経路");
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_LOCATION_CLEAR, 2, "経路をクリア");
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_CANCEL, 3, "キャンセル");
//            menu.add(0, MENU_ITEM_SELECT_LOCATION, 2, "履歴から選択");
        }
        else if (v.getId() == R.id.search) {
            menu.setHeaderTitle(Preferences.getText(this, "検索対象を選択"));
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_SEARCH_BY_GOOGLE, 1, "Googleトランジットで検索");
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_SEARCH_BY_GOO, 2, "goo路線で検索");
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_SEARCH_STATIONS, 3, "駅候補を検索");
            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_SEARCH_NEAR_STATIONS, 4, "最寄駅を検索");
        }
    }
    
    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_SEARCH_NEAR_STATIONS :
            searchNearStations();
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void searchNearStations() {
        new SearchNearStationsTask(this).execute();
    }

    /**
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PREFERENCE) {
                if (findViewById(R.id.search_details) == null) {
                    CheckBox express = (CheckBox) searchDetails.findViewById(R.id.express);
                    CheckBox airline = (CheckBox) searchDetails.findViewById(R.id.airline);
                    express.setChecked(Preferences.isUseExpress(this));
                    airline.setChecked(Preferences.isUseAirline(this));
                    initSort();
                    
                    if (Preferences.isFullInput(this)) {
                        addSearchDetails();
                    }
                }
                else {
                    if (!Preferences.isFullInput(this)) {
                        removeSearchDetails();
                    }
                }
        }
        else if (requestCode == REQUEST_CODE_SELECT_TRANSIT_QUERY) {
            if (resultCode == RESULT_CODE_ROUTE_SELECTED) {
            	TransitQueryEx q = (TransitQueryEx) data.getExtras().getSerializable(EXTRA_KEY_TRANSIT_QUERY);
                query.setFrom(q.getFrom());
                query.setTo(q.getTo());
                updateQueryView();
            }
            else if (resultCode == RESULT_CODE_FROM_SELECTED) {
                Location l = (Location) data.getExtras().getSerializable(EXTRA_KEY_LOCATION);
                query.setFrom(l.getLocation());
                updateQueryView();
            }
            else if (resultCode == RESULT_CODE_TO_SELECTED) {
                Location l = (Location) data.getExtras().getSerializable(EXTRA_KEY_LOCATION);
                query.setTo(l.getLocation());
                updateQueryView();
            }
        }
        else if (requestCode == REQUEST_CODE_VOICE_INPUT) {
            if (resultCode == RESULT_OK) {
            	String from = query.getFrom();
            	String to = query.getTo();
            	if (StringUtils.isNotEmpty(from) && StringUtils.isNotEmpty(to)) {
            		from = to = null;
            	}
            	
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for (int i = 0; i < results.size(); i ++) {
                	String result = results.get(i);
                	String[] split = result.split(" ");
                	for (int j = 0; j < split.length; j ++) {
                		if (StringUtils.isEmpty(from)) {
                			from = split[j];
                		}
                		else if (StringUtils.isEmpty(to)) {
                			to = split[j];
                		}
                		else {
                			break;
                		}
                	}
                	
                	if (StringUtils.isNotEmpty(from) && StringUtils.isNotEmpty(to)) {
                		break;
                	}
                }
                
                query.setFrom(from);
                query.setTo(to);
                updateQueryView();
            }
        }
    }
    
    private void initView() {
    	Preferences.initTheme(this);
        setContentView(getLayoutId());

        search = (Button) findViewById(R.id.search);
        results = (ListView) findViewById(R.id.results);
        searchButtons = (RelativeLayout) findViewById(R.id.layout_search_buttons);

        CheckBox first = (CheckBox) findViewById(R.id.first);
        first.setTextColor(Preferences.getTextColor(this));
        CheckBox last = (CheckBox) findViewById(R.id.last);
        last.setTextColor(Preferences.getTextColor(this));
        
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setTextSize(16);

        CheckBox express = (CheckBox) findViewById(R.id.express);
        express.setTextColor(Preferences.getTextColor(this));
        CheckBox airline = (CheckBox) findViewById(R.id.airline);
        airline.setTextColor(Preferences.getTextColor(this));

        Button maybe = (Button) findViewById(R.id.maybe);
        maybe.setVisibility(View.INVISIBLE);

        initQuery();
        updatePreviousTimeAndNextTimeVisibility();
    }
    
    @SuppressWarnings("unchecked")
    private void initFavorite() {
        List list = new TransitQueryDao(this).getTransitQueriesByFavorite();
        if (list.size() > 0) {
            list.add(new Favorable());
        }
        list.addAll(new TimeTableResultDao(this).getTimeTablesByFavorite());
        if (list.isEmpty()) {
            return;
        }
        
        TextView summary = (TextView) findViewById(R.id.tv_summary);
        summary.setTextSize(Preferences.getTextSize(this));
        summary.setText("\nお気に入り");
        
        ListView fab = (ListView) findViewById(R.id.results);
        fab.setAdapter(new FavoriteArrayAdapter(this, list));
        fab.requestFocus();
        hideInputMethod();
        registerForContextMenu(fab);
    }

    private void initQuery() {
        query.setTimeType(TimeType.DEPARTURE);
        
        if (Preferences.isUseLatestQueryHistory(this)) {
            TransitQueryEx latest = new TransitQueryDao(this).getLatestTransitQuery();
            if (latest != null) {
                updateFromAndTo(latest.getFrom(), latest.getTo());
            }
        }
        
        CheckBox express = (CheckBox) findViewById(R.id.express);
        express.setChecked(Preferences.isUseExpress(this));
        CheckBox airline = (CheckBox) findViewById(R.id.airline);
        airline.setChecked(Preferences.isUseAirline(this));
        
        searchDetails = (LinearLayout) findViewById(R.id.search_details);
        
        initSort();
        updateUseAirline();
        
        if (!Preferences.isFullInput(this)) {
            removeSearchDetails();
        }
    }
    
    private void initSort() {
        Spinner sort = (Spinner) searchDetails.findViewById(R.id.sort);
        String prefferedSort = Preferences.getSort(this);
        if ("time".equals(prefferedSort)) {
            sort.setSelection(0);
        }
        else if ("fare".equals(prefferedSort)) {
            sort.setSelection(1);
        }
        else if ("num".equals(prefferedSort)) {
            sort.setSelection(2);
        }
    }
    
    public void updateFromAndTo(String from, String to) {
        query.setFrom(from);
        query.setTo(to);
        updateQueryView();
    }
    
    public void updateFrom(String from) {
        query.setFrom(from);
        updateQueryView();
    }

    public void updateTo(String to) {
        query.setTo(to);
        updateQueryView();
    }

    private int getLayoutId() {
        orientationWhenStarted = Preferences.getOrientation(this);
        if (orientationWhenStarted == ORIENTATION_PORTRAIT) {
            return R.layout.main_portrait;
        }
        else if (orientationWhenStarted == ORIENTATION_LANDSCAPE) {
            return R.layout.main_landscape;
        }
        else {
            throw new IllegalStateException("予期していないレイアウトの向きです。orientation=" + orientationWhenStarted);
        }
    }

    private void initAction() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        from.setOnTouchListener(new FromToDragListener(from, to));
        to.setOnTouchListener(new FromToDragListener(to, from));
        registerForContextMenu(from);
        registerForContextMenu(to);
        
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
        
        search.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                searchNew();
            }
        });
        registerForContextMenu(search);
        
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
        
        ListView results = (ListView) findViewById(R.id.results);
        registerForContextMenu(results);
    }
    
    private TransitItem getSelectedTransitItem(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
        ListView lv = (ListView) findViewById(R.id.results);
        return (TransitItem) lv.getItemAtPosition(info.position);
    }
    
    private TransitQueryEx getSelectedTransitQuery(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
        ListView lv = (ListView) findViewById(R.id.results);
        return (TransitQueryEx) lv.getItemAtPosition(info.position);
    }

    private Object getSelectedItem(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
        return results.getItemAtPosition(info.position);
    }

    /**
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == MENU_ITEM_COPY_TEXT) {
            TransitItem ti = getSelectedTransitItem(menuItem);

            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            StringBuilder sb = new StringBuilder();
            sb.append(ResultRenderer.createTitleWithDate(ti.getTransitResult()));
            sb.append("\n\n");
            sb.append(ti.toString());
            cm.setText(sb.toString());
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_ALARM_CREATE) {
            TransitItem ti = getSelectedTransitItem(menuItem);
            
            AlarmSettingDialog dialog = new AlarmSettingDialog(this, ti.getTransitResult(), ti.getTransit());
            dialog.show();
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_MEMO_CREATE) {
            TransitItem ti = getSelectedTransitItem(menuItem);

            TransitResultEx str = new TransitResultEx(ti.getTransitResult());
            new TransitResultDao(this).createTransitResult(str, ti.getTransit());
            ToastUtils.toast(this, "メモに保存しました。");
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_FAVORITE) {
            TransitQueryEx stq = getSelectedTransitQuery(menuItem);
            updateFromAndTo(stq.getFrom(), stq.getTo());
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_FAVORITE_REVERSE) {
            TransitQueryEx stq = getSelectedTransitQuery(menuItem);
            updateFromAndTo(stq.getTo(), stq.getFrom());
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_REVERSE_LOCATION) {
            updateFromAndTo(query.getTo(), query.getFrom());
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SELECT_LOCATION) {
        }
        else if (menuItem.getItemId() == MENU_ITEM_SEARCH_BY_GOO) {
            engine = Engine.GOO;
            updateUseAirline();
            searchNew();
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SEARCH_BY_GOOGLE) {
            engine = Engine.GOOGLE;
            updateUseAirline();
            searchNew();
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SEARCH_STATIONS) {
            searchStations();
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_FROM) {
            Object item = getSelectedItem(menuItem);
            if (item instanceof Station) {
                updateFrom(((Station)item).getName());
                return true;
            }
            else if (item instanceof TimeTableEx) {
                updateFrom(((TimeTableEx)item).getStation().getName());
                return true;
            }
            else if (item instanceof StationItem) {
                updateFrom(((StationItem)item).getStation().getName());
                return true;
            }
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_TO) {
            Object item = getSelectedItem(menuItem);
            if (item instanceof Station) {
                updateTo(((Station)item).getName());
                return true;
            }
            else if (item instanceof TimeTableEx) {
                updateTo(((TimeTableEx)item).getStation().getName());
                return true;
            }
            else if (item instanceof StationItem) {
                updateTo(((StationItem)item).getStation().getName());
                return true;
            }
        }
        else if (menuItem.getItemId() == MENU_ITEM_LOCATION_CLEAR) {
            updateFromAndTo(null, null);
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_CANCEL) {
            // 何もしない
            return true;
        }

        return super.onContextItemSelected(menuItem);
    }

    private void showTimeDialog() {
    	final TimeDialog dialog = new TimeDialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface di) {
			    selectedTime = dialog.getTimeTypeAndDate();
                renderSelectedTime();
			}
		});
    	
   	    dialog.setTimeTypeAndDate(selectedTime);
    	dialog.show();
    }
    
    private void renderSelectedTime() {
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(selectedTime != null ? selectedTime.toString() : null);
    }
    
    private void updateQueryView() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        from.setText(query.getFrom());
        to.setText(query.getTo());
    }

    private void updateQuery() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        CheckBox first = (CheckBox) findViewById(R.id.first);
        CheckBox last = (CheckBox) findViewById(R.id.last);

        query.setFrom(from.getText().toString());
        query.setTo(to.getText().toString());
        
        if (engine == Engine.GOO) {
            query.setFromCode(null);
            query.setToCode(null);
            
            ListView results = (ListView) findViewById(R.id.results);
            if (results.getAdapter() instanceof StationArrayAdapter) {
                for (int i = 0; i < results.getCount(); i ++) {
                    StationItem item = (StationItem) results.getItemAtPosition(i);
                    if (item.getStation() == null) {
                        continue;
                    }
                    
                    if (item.getStation().getName().equals(query.getFrom())) {
                        query.setFromCode(item.getStation().getCode());
                    }
                    if (item.getStation().getName().equals(query.getTo())) {
                        query.setToCode(item.getStation().getCode());
                    }
                }
            }
        }
        
        if (first.isChecked()) {
            currentTime = new TimeTypeAndDate(TimeType.FIRST, new Date());
        }
        else if (last.isChecked()) {
            currentTime = new TimeTypeAndDate(TimeType.LAST, new Date());
        }
        else {
            currentTime = selectedTime;
            if (currentTime == null) {
                currentTime = new TimeTypeAndDate(TimeType.DEPARTURE, new Date());
            }
        }
        query.setTimeType(currentTime.getTimeType());
        query.setDate(currentTime.getDate());

        CheckBox express = (CheckBox) searchDetails.findViewById(R.id.express);
        CheckBox airline = (CheckBox) searchDetails.findViewById(R.id.airline);
        query.setUseExpress(express.isChecked());
        query.setUseAirline(airline.isChecked());
        
        Spinner sort = (Spinner) searchDetails.findViewById(R.id.sort);
        if (sort.getSelectedItemPosition() == 0) {
            query.setSort("time");
        }
        else if (sort.getSelectedItemPosition() == 1) {
            query.setSort("fare");
        }
        else if (sort.getSelectedItemPosition() == 2) {
            query.setSort("num");
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (showingResultsOnFullScreen) {
                showSearchCondition();
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    private void searchPrevious() {
        TimeTypeAndDate ttd = previousTime.peek();
        query.setTimeType(ttd.getTimeType());
        query.setDate(ttd.getDate());
        search(SEARCH_TYPE_PREVIOUS);
    }
    
    private void searchNext() {
        TimeTypeAndDate ttd = nextTime.peek();
        query.setTimeType(ttd.getTimeType());
        query.setDate(ttd.getDate());
        search(SEARCH_TYPE_NEXT);
    }
    
    private void searchNew() {
        updateQuery();
        query.setEngine(engine);
        search(SEARCH_TYPE_NEW);
    }
    
    private void searchStations() {
        updateQuery();
        search(SEARCH_TYPE_STATIONS);
    }

    public void saveHistory() {
        new Thread(new QueryHistoryWorker(this, query)).start();
    }
    
    public void hideInputMethod() {
        InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        EditText from = (EditText) findViewById(R.id.from);
        ime.hideSoftInputFromWindow(from.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
        EditText to = (EditText) findViewById(R.id.to);
        ime.hideSoftInputFromWindow(to.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    private boolean shouldHideSearchCondition() {
        return orientationWhenStarted == ORIENTATION_PORTRAIT && Preferences.isResultsOnFullScreen(this);
    }

    public void hideSearchCondition() {
        if (shouldHideSearchCondition()) {
            LinearLayout searchCondition = (LinearLayout) findViewById(R.id.search_condition);
            searchCondition.setVisibility(View.GONE);
            LinearLayout searchDetails = (LinearLayout) findViewById(R.id.search_details);
            if (searchDetails != null) {
                searchDetails.setVisibility(View.GONE);
            }
            showingResultsOnFullScreen = true;
        }
    }

    private void showSearchCondition() {
        LinearLayout searchCondition = (LinearLayout) findViewById(R.id.search_condition);
        searchCondition.setVisibility(View.VISIBLE);
        LinearLayout searchDetails = (LinearLayout) findViewById(R.id.search_details);
        if (searchDetails != null) {
            searchDetails.setVisibility(View.VISIBLE);
        }
        showingResultsOnFullScreen = false;
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
    
    private void removeSearchDetails() {
        if (Preferences.getOrientation(this) == ORIENTATION_PORTRAIT) { 
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_top);
            view.removeView(searchDetails);
        }
        else if (Preferences.getOrientation(this) == ORIENTATION_LANDSCAPE) {
            Button search = (Button) findViewById(R.id.search);
            LinearLayout searchDetailsMiddle = (LinearLayout) findViewById(R.id.search_details_middle);
            LinearLayout searchDetailsBottom = (LinearLayout) findViewById(R.id.search_details_bottom);
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_search);
            searchDetailsMiddle.removeView(search);
            view.removeView(searchDetails);
            searchDetailsBottom.addView(search);
        }
        else {
            throw new IllegalStateException("予期していないレイアウトの向きです。" + Preferences.getOrientation(this));
        }
    }
    
    private void addSearchDetails() {
        if (Preferences.getOrientation(this) == ORIENTATION_PORTRAIT) { 
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_top);
            if (view != null) {
                view.addView(searchDetails, 1);
            }
        }
        else if (Preferences.getOrientation(this) == ORIENTATION_LANDSCAPE) {
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_search);
            if (view != null) {
                Button search = (Button) findViewById(R.id.search);
                LinearLayout searchDetailsBottom = (LinearLayout) findViewById(R.id.search_details_bottom);
                searchDetailsBottom.removeView(search);
                view.addView(searchDetails, 4);
                LinearLayout searchDetailsMiddle = (LinearLayout) findViewById(R.id.search_details_middle);
                searchDetailsMiddle.addView(search);
            }
        }
        else {
            throw new IllegalStateException("予期していないレイアウトの向きです。" + Preferences.getOrientation(this));
        }
    }

    private void search(int searchType) {
        if (!validateQuery()) {
            return;
        }
        
        new TransitSearchTask(this, searchType).execute(query.getTransitQuery());
    }
    
    public void updatePreviousTimeAndNextTime(int searchType, TransitResult result) {
        if (result == null || result.getTransitCount() == 0) {
            if (searchType == SEARCH_TYPE_STATIONS || searchType == SEARCH_TYPE_NEW) {
                nextTime.clear();
                previousTime.clear();
            }

            updatePreviousTimeAndNextTimeVisibility();
            return;
        }

        if (searchType == SEARCH_TYPE_NEW) {
            nextTime.clear();
            previousTime.clear();
        }
        else if (searchType == SEARCH_TYPE_NEXT) {
            if (currentTime != null) {
                previousTime.push(currentTime);
            }
            TimeTypeAndDate ttd = nextTime.pop();
            currentTime = ttd;
        }
        else if (searchType == SEARCH_TYPE_PREVIOUS) {
            if (currentTime != null) {
                nextTime.push(currentTime);
            }
            TimeTypeAndDate ttd = previousTime.pop();
            currentTime = ttd;
        }
        
        if (result.getTimeType() == TimeType.DEPARTURE || result.getTimeType() == TimeType.FIRST) {
            if (nextTime.isEmpty()) {
                Time firstDepartureTime = TransitUtil.getFirstDepartureTime(result);
                if (firstDepartureTime != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(result.getQueryDate());
                    c.set(Calendar.HOUR_OF_DAY, firstDepartureTime.getHour());
                    c.set(Calendar.MINUTE, firstDepartureTime.getMinute());
                    
                    // 日を跨いでる場合
                    if (c.getTime().before(result.getQueryDate())) {
                        c.add(Calendar.DATE, 1);
                    }
    
                    // 1分後に出発する乗換を検索
                    c.add(Calendar.MINUTE, 1);
                    nextTime.push(new TimeTypeAndDate(TimeType.DEPARTURE, c.getTime()));
                }
            }
            
            if (previousTime.isEmpty()) {
                Time firstArrivalTime = TransitUtil.getFirstArrivalTime(result);
                if (firstArrivalTime != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(result.getQueryDate());
                    c.set(Calendar.HOUR_OF_DAY, firstArrivalTime.getHour());
                    c.set(Calendar.MINUTE, firstArrivalTime.getMinute());
                    
                    // 日を跨いでる場合
                    if (c.getTime().before(result.getQueryDate())) {
                        c.add(Calendar.DATE, 1);
                    }
                    
                    // 1分前に到着する乗換を基点に検索
                    c.add(Calendar.MINUTE, -1);
                    previousTime.push(new TimeTypeAndDate(TimeType.PREVIOUS_DEPARTURE, c.getTime()));
                }
            }
        }
        else if (result.getTimeType() == TimeType.ARRIVAL || result.getTimeType() == TimeType.LAST) {
            if (nextTime.isEmpty()) {
                Time lastDepartureTime = TransitUtil.getLastDepartureTime(result);
                if (lastDepartureTime != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(result.getQueryDate());
                    c.set(Calendar.HOUR_OF_DAY, lastDepartureTime.getHour());
                    c.set(Calendar.MINUTE, lastDepartureTime.getMinute());
                    
                    // 日を跨いでる場合
                    if (c.getTime().after(result.getQueryDate())) {
                        c.add(Calendar.DATE, -1);
                    }
    
                    // 1分後に出発する乗換を基点に検索
                    c.add(Calendar.MINUTE, 1);
                    nextTime.push(new TimeTypeAndDate(TimeType.NEXT_ARRIVAL, c.getTime()));
                }
            }

            if (previousTime.isEmpty()) {
                Time lastArrivalTime = TransitUtil.getLastArrivalTime(result);
                if (lastArrivalTime != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(result.getQueryDate());
                    c.set(Calendar.HOUR_OF_DAY, lastArrivalTime.getHour());
                    c.set(Calendar.MINUTE, lastArrivalTime.getMinute());
                    
                    // 日を跨いでる場合
                    if (c.getTime().after(result.getQueryDate())) {
                        c.add(Calendar.DATE, -1);
                    }
                    
                    // 1分前に到着する乗換を検索
                    c.add(Calendar.MINUTE, -1);
                    previousTime.push(new TimeTypeAndDate(TimeType.ARRIVAL, c.getTime()));
                }
            }
        }
        
        updatePreviousTimeAndNextTimeVisibility();
    }
    
    private void updatePreviousTimeAndNextTimeVisibility() {
        if (nextTime.isEmpty() && previousTime.isEmpty()) {
            searchButtons.setVisibility(View.GONE);
        }
        else {
            searchButtons.setVisibility(View.VISIBLE);
        }
    }
    
    private void updateUseAirline() {
        CheckBox airline = (CheckBox) searchDetails.findViewById(R.id.airline);
        if (engine == Engine.GOOGLE) {
            airline.setVisibility(View.VISIBLE);
        }
        else if (engine == Engine.GOO) {
            airline.setVisibility(View.GONE);
        }
            
    }
    
}