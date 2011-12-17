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
package jp.co.hybitz.simpletransit.timetable;

import java.util.List;

import jp.co.hybitz.android.DateUtils;
import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.common.BaseActivity;
import jp.co.hybitz.simpletransit.db.TimeTableResultDao;
import jp.co.hybitz.simpletransit.timetable.model.AreaEx;
import jp.co.hybitz.simpletransit.timetable.model.LineEx;
import jp.co.hybitz.simpletransit.timetable.model.PrefectureEx;
import jp.co.hybitz.simpletransit.timetable.model.StationEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeLineEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableEx;
import jp.co.hybitz.simpletransit.timetable.model.TimeTableResultEx;
import jp.co.hybitz.simpletransit.util.ToastUtils;
import jp.co.hybitz.timetable.model.Area;
import jp.co.hybitz.timetable.model.Line;
import jp.co.hybitz.timetable.model.Prefecture;
import jp.co.hybitz.timetable.model.Station;
import jp.co.hybitz.timetable.model.TimeTableQuery;
import jp.co.hybitz.timetable.model.TimeTable.Type;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TimeTableActivity extends BaseActivity implements SimpleTransitConst {
    private Bitmap[] images;
    private TimeTableItem currentItem;
    private ParentBackItem backItem;
    private TimeTableResultEx result;
    private TimeTableEx timeTable;
    private ImageView star;
    private TextView title;
    private ListView list;
    private TextView lastUpdate;
    private Button previousType;
    private Button nextType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = (TimeTableResultEx) getIntent().getExtras().getSerializable(EXTRA_KEY_TIME_TABLE_RESULT);
        timeTable = (TimeTableEx) getIntent().getExtras().getSerializable(EXTRA_KEY_TIME_TABLE);
        
        initView();

        if (result != null) {
            initAction(true);
            showAreas(result);
        }
        else if (timeTable != null) {
            initAction(false);
            showTimeLines(null, null, timeTable.getStation().getLine(), timeTable.getStation(), timeTable);
        }
    }
    
    private void initView() {
        Preferences.initTheme(this);
        setContentView(R.layout.time_table);
        setTitle(getTitle() + "　Yahoo時刻表");

        images = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.star_off),
                BitmapFactory.decodeResource(getResources(), R.drawable.star_on),
            };
        
        star = (ImageView) findViewById(R.id.star);
        title = (TextView) findViewById(R.id.time_table_title);
        list = (ListView) findViewById(R.id.time_table_list);
        lastUpdate = (TextView) findViewById(R.id.last_update);
        previousType = (Button) findViewById(R.id.previous_type);
        nextType = (Button) findViewById(R.id.next_type);
    }
    
    private void initAction(boolean clickable) {
        if (clickable) {
            list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TimeTableItem item = (TimeTableItem) parent.getItemAtPosition(position);
                    updateList(item);
                }
            });
        }
        registerForContextMenu(list);
        
        nextType.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showTimeTable(true);
            }
        });

        previousType.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showTimeTable(false);
            }
        });
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backItem != null) {
                handleBack(backItem);
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int i = 0;
        if (timeTable != null) {
            menu.add(0, MENU_ITEM_TIME_TABLE, ++i, "駅・時刻表");
        }
        else {
            menu.add(0, MENU_ITEM_REFRESH, ++i, "再読込");
        }
        menu.add(0, MENU_ITEM_PREFERENCES, ++i, "設定");
        menu.add(0, MENU_ITEM_QUIT, ++i, "終了");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (MENU_ITEM_REFRESH == item.getItemId()) {
            handleRefresh();
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void showTimeTable(boolean next) {
        TimeTableEx current = currentItem.getTimeTable();
        
        Type type = null;
        if (next) {
            switch (current.getType()) {
            case WEEKDAY :
                type = Type.SATURDAY;
                break;
            case SATURDAY :
                type = Type.SUNDAY;
                break;
            case SUNDAY :
                type = Type.WEEKDAY;
                break;
            }
        }
        else {
            switch (current.getType()) {
            case WEEKDAY :
                type = Type.SUNDAY;
                break;
            case SATURDAY :
                type = Type.WEEKDAY;
                break;
            case SUNDAY :
                type = Type.SATURDAY;
                break;
            }
        }
        
        TimeTableEx tt = currentItem.getStation().getTimeTable(current.getDirection(), type);
        if (tt == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            tt = dao.getTimeTable(current.getStationId(), current.getDirection(), type);
            currentItem.getStation().addTimeTable(tt);
        }
        showTimeLines(currentItem.getArea(), currentItem.getPrefecture(), currentItem.getLine(), currentItem.getStation(), tt);
    }
    
    private void handleRefresh() {
        if (currentItem == null) {
            searchAreas();
        }
        else if (currentItem.getTimeTable() != null) {
            ToastUtils.toast(this, "まだ未対応です。。");
        }
        else if (currentItem.getStation() != null) {
            searchTimeTables(currentItem);
        }
        else if (currentItem.getLine() != null) {
            searchStations(currentItem);
        }
        else if (currentItem.getPrefecture() != null) {
            searchLines(currentItem);
        }
        else if (currentItem.getArea() != null) {
            searchPrefectures(currentItem);
        }
        else {
            ToastUtils.toast(this, "まだ未対応です。。");
        }
    }
    
    private void handleBack(ParentBackItem item) {
        if (item.getArea() == null) {
            showAreas(result);
        }
        else if (item.getPrefecture() == null) {
            showPrefectures(item.getArea());
        }
        else if (item.getLine() == null) {
            showLines(item.getArea(), item.getPrefecture());
        }
        else if (item.getStation() == null) {
            showStations(item.getArea(), item.getPrefecture(), item.getLine());
        }
        else if (item.getTimeTable() == null) {
            showTimeTables(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation());
        }
        else if (item.getTimeLine() == null) {
            showTimeLines(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation(), item.getTimeTable());
        }
    }
    
    private void searchAreas() {
        TimeTableQuery query = new TimeTableQuery();
        new TimeTableTask(this).execute(query);
    }

    private void searchPrefectures(TimeTableItem item) {
        TimeTableQuery query = new TimeTableQuery();
        new TimeTableTask(this, item).execute(query);
    }

    private void searchLines(TimeTableItem item) {
        TimeTableQuery query = new TimeTableQuery();
        query.setArea(new Area());
        query.getArea().setName(item.getArea().getName());
        query.getArea().setUrl(item.getArea().getUrl());
        query.setPrefecture(new Prefecture());
        query.getPrefecture().setName(item.getPrefecture().getName());
        query.getPrefecture().setUrl(item.getPrefecture().getUrl());
        new TimeTableTask(this, item).execute(query);
    }
    
    private void searchStations(TimeTableItem item) {
        TimeTableQuery query = new TimeTableQuery();
        query.setArea(new Area());
        query.getArea().setName(item.getArea().getName());
        query.getArea().setUrl(item.getArea().getUrl());
        query.setPrefecture(new Prefecture());
        query.getPrefecture().setName(item.getPrefecture().getName());
        query.getPrefecture().setUrl(item.getPrefecture().getUrl());
        query.setLine(new Line());
        query.getLine().setName(item.getLine().getName());
        query.getLine().setUrl(item.getLine().getUrl());
        new TimeTableTask(this, item).execute(query);
    }
    
    private void searchTimeTables(TimeTableItem item) {
        TimeTableQuery query = new TimeTableQuery();
        query.setArea(new Area());
        query.getArea().setName(item.getArea().getName());
        query.getArea().setUrl(item.getArea().getUrl());
        query.setPrefecture(new Prefecture());
        query.getPrefecture().setName(item.getPrefecture().getName());
        query.getPrefecture().setUrl(item.getPrefecture().getUrl());
        query.setLine(new Line());
        query.getLine().setCompany(item.getLine().getCompany());
        query.getLine().setName(item.getLine().getName());
        query.getLine().setUrl(item.getLine().getUrl());
        query.setStation(new Station());
        query.getStation().setName(item.getStation().getName());
        query.getStation().setUrl(item.getStation().getUrl());
        new TimeTableTask(this, item).execute(query);
    }
    
    private void updateList(TimeTableItem item) {
        if (item instanceof ParentBackItem) {
            handleBack((ParentBackItem) item);
        }
        else if (item.getPrefecture() == null) {
            showPrefectures(item.getArea());
        }
        else if (item.getLine() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<LineEx> lines = dao.getLines(item.getPrefecture().getId());
            if (lines.isEmpty()) {
                searchLines(item);
            }
            else {
                item.getPrefecture().setLines(lines);
                showLines(item.getArea(), item.getPrefecture());
            }
        }
        else if (item.getStation() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<StationEx> stations = dao.getStations(item.getLine().getId());
            if (stations.isEmpty()) {
                searchStations(item);
            }
            else {
                item.getLine().setStations(stations);
                showStations(item.getArea(), item.getPrefecture(), item.getLine());
            }
        }
        else if (item.getTimeTable() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<TimeTableEx> timeTables = dao.getTimeTables(item.getStation().getId(), false);
            if (timeTables.isEmpty()) {
                searchTimeTables(item);
            }
            else {
                item.getStation().setTimeTables(timeTables);
                showTimeTables(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation());
            }
        }
        else if (item.getTimeLine() == null) {
            TimeTableResultDao dao = new TimeTableResultDao(this);
            List<TimeLineEx> timeLines = dao.getTimeLines(item.getTimeTable().getId());
            item.getTimeTable().setTimeLines(timeLines);
            showTimeLines(item.getArea(), item.getPrefecture(), item.getLine(), item.getStation(), item.getTimeTable());
        }
    }
    
    private CharSequence getLastUpdate(long updateAt) {
        return Preferences.getText(this, "更新日:" + DateUtils.format(updateAt, "yyyy/MM/dd"));
    }
    
    public void showAreas(TimeTableResultEx result) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list);
        currentItem = null;
        backItem = null;

        for (AreaEx a : result.getAreas()) {
            TimeTableItem item = new TimeTableItem(a);
            adapter.add(item);
        }

        star.setVisibility(View.GONE);
        title.setText(Preferences.getText(this, "地域"));
        list.setAdapter(adapter);
        lastUpdate.setText(getLastUpdate(result.getAreas().get(0).getUpdatedAt()));
        updateControlButtons(null);
    }

    public void showPrefectures(AreaEx a) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list);
        currentItem = new TimeTableItem(a);
        backItem = new ParentBackItem();

        adapter.insert(backItem, 0);
        for (int i = 0; i < a.getPrefectures().size(); i ++) {
            PrefectureEx p = a.getPrefectures().get(i);
            TimeTableItem item = new TimeTableItem(a, p);
            adapter.insert(item, i+1);
        }

        star.setVisibility(View.GONE);
        title.setText(Preferences.getText(this, a.getName()));
        list.setAdapter(adapter);
        lastUpdate.setText(getLastUpdate(a.getPrefectures().get(0).getUpdatedAt()));
        updateControlButtons(null);
    }
    
    public void showLines(AreaEx a, PrefectureEx p) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list);
        currentItem = new TimeTableItem(a, p);
        backItem = new ParentBackItem(a);

        adapter.insert(backItem, 0);
        for (int i = 0; i < p.getLines().size(); i ++) {
            LineEx l = p.getLines().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l);
            adapter.insert(item, i+1);
        }

        star.setVisibility(View.GONE);
        title.setText(Preferences.getText(this, p.getName()));
        list.setAdapter(adapter);
        lastUpdate.setText(getLastUpdate(p.getLines().get(0).getUpdatedAt()));
        updateControlButtons(null);
    }
    
    public void showStations(AreaEx a, PrefectureEx p, LineEx l) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list);
        currentItem = new TimeTableItem(a, p, l);
        backItem = new ParentBackItem(a, p);

        adapter.insert(backItem, 0);
        for (int i = 0; i < l.getStations().size(); i ++) {
            StationEx s = l.getStations().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l, s);
            adapter.insert(item, i+1);
        }

        star.setVisibility(View.GONE);
        title.setText(Preferences.getText(this, l.getCompany() + "　" + l.getName()));
        list.setAdapter(adapter);
        lastUpdate.setText(getLastUpdate(l.getStations().get(0).getUpdatedAt()));
        updateControlButtons(null);
    }
    
    public void showTimeTables(AreaEx a, PrefectureEx p, LineEx l, StationEx s) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list);
        currentItem = new TimeTableItem(a, p, l, s);
        backItem = new ParentBackItem(a, p, l);

        adapter.insert(backItem, 0);
        for (int i = 0; i < s.getTimeTables().size(); i ++) {
            TimeTableEx tt = s.getTimeTables().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l, s, tt);
            adapter.insert(item, i+1);
        }

        star.setVisibility(View.GONE);
        title.setText(Preferences.getText(this, l.getCompany() + "　" + l.getName() + "　" + s.getName()));
        list.setAdapter(adapter);
        lastUpdate.setText(getLastUpdate(s.getTimeTables().get(0).getUpdatedAt()));
        updateControlButtons(null);
    }
    
    private void showTimeLines(AreaEx a, PrefectureEx p, LineEx l, StationEx s, TimeTableEx tt) {
        TimeTableArrayAdapter adapter = new TimeTableArrayAdapter(this, R.layout.time_table_list);
        currentItem = new TimeTableItem(a, p, l, s, tt);

        boolean hasBack = false;
        if (a != null && p != null) {
            backItem = new ParentBackItem(a, p, l, s);
            adapter.insert(backItem, 0);
            hasBack = true;
        }
        else {
            backItem = null;
        }

        if (tt.getTimeLines().isEmpty()) {
            tt.setTimeLines(new TimeTableResultDao(this).getTimeLines(tt.getId()));
        }

        for (int i = 0; i < tt.getTimeLines().size(); i ++) {
            TimeLineEx tl = tt.getTimeLines().get(i);
            TimeTableItem item = new TimeTableItem(a, p, l, s, tt, tl);
            adapter.insert(item, i + (hasBack ? 1 : 0));
        }

        star.setVisibility(View.VISIBLE);
        star.setImageBitmap(tt.isFavorite() ? images[1] : images[0]);
        star.setOnClickListener(new StarListener(images, tt));
        String lineName = tt.getLineName();
        if (StringUtils.isEmpty(lineName)) {
            lineName = l.getName();
        }
        title.setText(Preferences.getText(this, l.getCompany() + "　" + lineName + "　" + s.getName() + "　" + tt.getDirection() + "　" + tt.getTypeString()));
        list.setAdapter(adapter);
        lastUpdate.setText(getLastUpdate(tt.getUpdatedAt()));
        updateControlButtons(tt);
    }
    
    private void updateControlButtons(TimeTableEx tt) {
        RelativeLayout controlButtons = (RelativeLayout) findViewById(R.id.control_buttons);
        controlButtons.setVisibility(tt != null ? View.VISIBLE : View.GONE);
    }
}
