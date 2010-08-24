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
import java.util.Date;
import java.util.List;

import jp.benishouga.common.AndroidExceptionHandler;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.TransitUtil;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.simpletransit.action.FirstAndLastCheckBoxListener;
import jp.co.hybitz.simpletransit.action.FromToDragListener;
import jp.co.hybitz.simpletransit.action.MaybeListener;
import jp.co.hybitz.simpletransit.action.OptionMenuHandler;
import jp.co.hybitz.simpletransit.alarm.AlarmSettingDialog;
import jp.co.hybitz.simpletransit.db.TransitQueryDao;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.favorite.FavoriteArrayAdapter;
import jp.co.hybitz.simpletransit.favorite.FavoriteListView;
import jp.co.hybitz.simpletransit.history.QueryHistoryWorker;
import jp.co.hybitz.simpletransit.model.Location;
import jp.co.hybitz.simpletransit.model.SimpleTransitQuery;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import jp.co.hybitz.simpletransit.model.TimeTypeAndDate;
import jp.co.hybitz.simpletransit.model.TransitItem;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import jp.co.hybitz.simpletransit.util.ToastUtils;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity implements SimpleTransitConst {
    private ExceptionHandler exceptionHandler = new ExceptionHandler(this);
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);
    private ResultRenderer resultRenderer = new ResultRenderer(this);
    private SimpleTransitQuery query = new SimpleTransitQuery();
    private TimeTypeAndDate currentTime;
    private TimeTypeAndDate previousTime;
    private TimeTypeAndDate nextTime;
    private View searchDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidExceptionHandler.bind(this, APP_ID);
        initView();
        initAction();
        initFavorite();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        TextView summary = (TextView) findViewById(R.id.tv_summary);
        summary.setTextSize(Preferences.getTextSize(this));
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_QUERY_HISTORY, 1, "検索履歴");
        menu.add(0, MENU_ITEM_MEMO, 2, "メモ");
        menu.add(0, MENU_ITEM_ALARM, 3, "アラーム");
        menu.add(0, MENU_ITEM_TRAVEL_DELAY, 4, "運行状況");
        menu.add(0, MENU_ITEM_PREFERENCES, 5, "設定");
        menu.add(0, MENU_ITEM_VOICE, 6, "音声入力");
        menu.add(0, MENU_ITEM_QUIT, 7, "終了");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (optionMenuHandler.onMenuItemSelected(featureId, item)) {
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
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
            	SimpleTransitQuery q = (SimpleTransitQuery) data.getExtras().getSerializable(EXTRA_KEY_TRANSIT_QUERY);
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
    	
        CheckBox first = (CheckBox) findViewById(R.id.first);
        first.setTextColor(Preferences.getTextColor(this));
        CheckBox last = (CheckBox) findViewById(R.id.last);
        last.setTextColor(Preferences.getTextColor(this));
        
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setTextSize(16);

        query.setTimeType(TimeType.DEPARTURE);
        
        CheckBox express = (CheckBox) findViewById(R.id.express);
        express.setTextColor(Preferences.getTextColor(this));
        CheckBox airline = (CheckBox) findViewById(R.id.airline);
        airline.setTextColor(Preferences.getTextColor(this));

        Button maybe = (Button) findViewById(R.id.maybe);
        maybe.setVisibility(View.INVISIBLE);

        initQuery();
        updatePreviousTimeAndNextTimeVisibility();
    }
    
    private void initFavorite() {
        List<SimpleTransitQuery> list = new TransitQueryDao(this).getTransitQueriesByFavarite();
        if (list.isEmpty()) {
            return;
        }
        
        TextView summary = (TextView) findViewById(R.id.tv_summary);
        summary.setTextSize(Preferences.getTextSize(this));
        summary.setText("\nお気に入り");
        
        FavoriteListView fab = (FavoriteListView) findViewById(R.id.favorite);
        fab.setAdapter(new FavoriteArrayAdapter(this, R.layout.favorite_list, list));
        fab.requestFocus();
        hideInputMethod();
        registerForContextMenu(fab);
    }

    private void initQuery() {
        if (Preferences.isUseLatestQueryHistory(this)) {
            SimpleTransitQuery latest = new TransitQueryDao(this).getLatestTransitQuery();
            if (latest != null) {
                updateQuery(latest.getFrom(), latest.getTo());
            }
        }
        
        CheckBox express = (CheckBox) findViewById(R.id.express);
        express.setChecked(Preferences.isUseExpress(this));
        CheckBox airline = (CheckBox) findViewById(R.id.airline);
        airline.setChecked(Preferences.isUseAirline(this));
        
        searchDetails = findViewById(R.id.search_details);
        if (!Preferences.isFullInput(this)) {
            removeSearchDetails();
        }
    }
    
    public void updateQuery(String from, String to) {
        query.setFrom(from);
        query.setTo(to);
        updateQueryView();
    }
    
    private int getLayoutId() {
        int orientation = Preferences.getOrientation(this);
        if (orientation == ORIENTATION_PORTRAIT) {
            return R.layout.main_portrait;
        }
        else if (orientation == ORIENTATION_LANDSCAPE) {
            return R.layout.main_landscape;
        }
        else {
            throw new IllegalStateException("予期していないレイアウトの向きです。orientation=" + orientation);
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
        
        ListView results = (ListView) findViewById(R.id.results);
        registerForContextMenu(results);
    }
    
    /**
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.results) {
            menu.add(0, MENU_ITEM_COPY_TEXT, 1, "テキストをコピー");
            menu.add(0, MENU_ITEM_MEMO_CREATE, 2, "メモとして保存");
            menu.add(0, MENU_ITEM_ALARM_CREATE, 3, "アラームをセット");
            menu.add(0, MENU_ITEM_CANCEL, 4, "キャンセル");
        }
        else if (v.getId() == R.id.from || v.getId() == R.id.to) {
            menu.setHeaderTitle(Preferences.getText(this, "テキストを編集"));
//            menu.add(Menu.CATEGORY_SYSTEM, MENU_ITEM_REVERSE_LOCATION, Menu.FIRST, "逆経路");
//            menu.add(0, MENU_ITEM_SELECT_LOCATION, 2, "履歴から選択");
        }
        else if (v.getId() == R.id.favorite) {
            menu.add(0, MENU_ITEM_SET_FAVORITE, 1, "経路を設定");
            menu.add(0, MENU_ITEM_SET_FAVORITE_REVERSE, 2, "逆経路を設定");
            menu.add(0, MENU_ITEM_CANCEL, 3, "キャンセル");
        }
    }
    
    private TransitItem getSelectedTransitItem(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
        ResultListView lv = (ResultListView) findViewById(R.id.results);
        return lv.getTransitItem(info.position);
    }
    
    private SimpleTransitQuery getSelectedTransitQuery(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo(); 
        FavoriteListView lv = (FavoriteListView) findViewById(R.id.favorite);
        return lv.getTransitQuery(info.position);
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

            SimpleTransitResult str = new SimpleTransitResult(ti.getTransitResult());
            new TransitResultDao(this).createTransitResult(str, ti.getTransit());
            ToastUtils.toast(this, "メモに保存しました。");
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_FAVORITE) {
            SimpleTransitQuery stq = getSelectedTransitQuery(menuItem);
            updateQuery(stq.getFrom(), stq.getTo());
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SET_FAVORITE_REVERSE) {
            SimpleTransitQuery stq = getSelectedTransitQuery(menuItem);
            updateQuery(stq.getTo(), stq.getFrom());
            return true;
        }
        else if (menuItem.getItemId() == MENU_ITEM_SELECT_LOCATION) {
        }
        else if (menuItem.getItemId() == MENU_ITEM_REVERSE_LOCATION) {
            updateQuery(query.getTo(), query.getFrom());
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
				currentTime = dialog.getTimeTypeAndDate();
                renderSelectedTime();
			}
		});
    	
   	    dialog.setTimeTypeAndDate(currentTime);
    	dialog.show();
    }
    
    private void renderSelectedTime() {
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText(currentTime != null ? currentTime.toString() : null);
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
        
        if (first.isChecked()) {
            query.setTimeType(TimeType.FIRST);
            query.setDate(new Date());
        }
        else if (last.isChecked()) {
            query.setTimeType(TimeType.LAST);
            query.setDate(new Date());
        }
        else {
            if (currentTime != null) {
                query.setTimeType(currentTime.getTimeType());
                query.setDate(currentTime.getDate());
            } else {
                query.setTimeType(TimeType.DEPARTURE);
                query.setDate(new Date());
            }
        }

        CheckBox express = (CheckBox) searchDetails.findViewById(R.id.express);
        CheckBox airline = (CheckBox) searchDetails.findViewById(R.id.airline);
        query.setUseExpress(express.isChecked());
        query.setUseAirline(airline.isChecked());
    }
    
    private void searchPrevious() {
        query.setTimeType(previousTime.getTimeType());
        query.setDate(previousTime.getDate());
        search();
    }
    
    private void searchNext() {
        query.setTimeType(nextTime.getTimeType());
        query.setDate(nextTime.getDate());
        search();
    }
    
    private void searchNew() {
        updateQuery();
        int count = search();
        
        // 検索条件を履歴として保存
        if (count > 0) {
            new Thread(new QueryHistoryWorker(this, query)).start();
        }
    }
    
    private void hideInputMethod() {
        InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        EditText from = (EditText) findViewById(R.id.from);
        ime.hideSoftInputFromWindow(from.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
        EditText to = (EditText) findViewById(R.id.to);
        ime.hideSoftInputFromWindow(to.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
    
    private void removeFavoriteList() {
        FavoriteListView fab = (FavoriteListView) findViewById(R.id.favorite);
        if (fab != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_top);
            view.removeView(fab);
        }
    }
    
    private void removeSearchDetails() {
        if (Preferences.getOrientation(this) == ORIENTATION_PORTRAIT) { 
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_top);
            view.removeView(searchDetails);
        }
        else if (Preferences.getOrientation(this) == ORIENTATION_LANDSCAPE) {
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_search);
            view.removeView(searchDetails);
        }
        else {
            throw new IllegalStateException("予期していないレイアウトの向きです。" + Preferences.getOrientation(this));
        }
    }
    
    private void addSearchDetails() {
        if (Preferences.getOrientation(this) == ORIENTATION_PORTRAIT) { 
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_top);
            view.addView(searchDetails, 1);
        }
        else if (Preferences.getOrientation(this) == ORIENTATION_LANDSCAPE) {
            LinearLayout view = (LinearLayout) findViewById(R.id.layout_search);
            view.addView(searchDetails, 4);
        }
        else {
            throw new IllegalStateException("予期していないレイアウトの向きです。" + Preferences.getOrientation(this));
        }
    }

    private int search() {
        if (!validateQuery()) {
            return 0;
        }
        
        try {
            TransitSearcher searcher = TransitSearcherFactory.createSearcher(Platform.ANDROID);
            TransitResult result = searcher.search(query.getTransitQuery());
            
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // お気に入りを非表示
                removeFavoriteList();

                // 検索結果を表示
                resultRenderer.render(result);
                
                // 前の時刻と次の時刻を取得
                updatePreviousTimeAndNextTime(result);
                
                // もしかしてを更新
                updateMaybe(result);
            }
            else {
                showResponseCode(result.getResponseCode());
            }
            
            hideInputMethod();
            return result.getTransitCount();
            
        } catch (HttpSearchException e) {
            exceptionHandler.handleException(e);
        }
        
        return 0;
    }
    
    private void updateMaybe(TransitResult result) {
        Button maybe = (Button) findViewById(R.id.maybe);

        if (Preferences.isUseMaybe(this) && result.getMaybe() != null) {
            maybe.setVisibility(View.VISIBLE);
            maybe.setOnClickListener(new MaybeListener(this, result.getMaybe()));
        }
        else {
            maybe.setVisibility(View.INVISIBLE);
        }
    }
    
    private void updatePreviousTimeAndNextTime(TransitResult result) {
        nextTime = null;
        previousTime = null;
        
        if (result != null && result.getTransitCount() > 0) {
            if (query.getTimeType() == TimeType.DEPARTURE || query.getTimeType() == TimeType.FIRST) {
                Time t = TransitUtil.getFirstDepartureTime(result);
                if (t != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(result.getQueryDate());
                    c.set(Calendar.HOUR_OF_DAY, t.getHour());
                    c.set(Calendar.MINUTE, t.getMinute());
                    
                    // 日を跨いでる場合
                    if (c.getTime().before(result.getQueryDate())) {
                        c.add(Calendar.DATE, 1);
                    }

                    // 1分後に出発する乗換を検索
                    c.add(Calendar.MINUTE, 1);
                    nextTime = new TimeTypeAndDate(TimeType.DEPARTURE, c.getTime());
                }
            }
            else if (query.getTimeType() == TimeType.ARRIVAL || query.getTimeType() == TimeType.LAST) {
                Time t = TransitUtil.getLastArrivalTime(result);
                if (t != null) {
                	Calendar c = Calendar.getInstance();
                	c.setTime(result.getQueryDate());
                    c.set(Calendar.HOUR_OF_DAY, t.getHour());
                    c.set(Calendar.MINUTE, t.getMinute());
                    
                    // 日を跨いでる場合
                    if (c.getTime().after(result.getQueryDate())) {
                        c.add(Calendar.DATE, -1);
                    }
                    
                    // 1分前に到着する乗換を検索
                    c.add(Calendar.MINUTE, -1);
                    previousTime = new TimeTypeAndDate(TimeType.ARRIVAL, c.getTime());
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