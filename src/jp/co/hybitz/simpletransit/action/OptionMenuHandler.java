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
package jp.co.hybitz.simpletransit.action;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.alarm.AlarmPlayActivity;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.history.QueryHistoryTabActivity;
import jp.co.hybitz.simpletransit.memo.MemoListActivity;
import jp.co.hybitz.simpletransit.util.ToastUtils;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.MenuItem;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class OptionMenuHandler implements SimpleTransitConst {
    
    private Activity activity;

    public OptionMenuHandler(Activity activity) {
        this.activity = activity;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_PREFERENCES :
            activity.startActivityForResult(new Intent(activity, Preferences.class), REQUEST_CODE_PREFERENCE);
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
            activity.finish();
            return true;
        }
        
        return false;
    }
    
    private void voiceInput() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "出発地・到着地を叫んでください！");
            activity.startActivityForResult(intent, REQUEST_CODE_VOICE_INPUT);
        }
        catch (ActivityNotFoundException e) {
            ToastUtils.toastLong(activity, "音声入力に対応していません。");
        }        
    }
    
    private void showQueryHistoryList() {
        Intent intent = new Intent(activity, QueryHistoryTabActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_TRANSIT_QUERY);
    }
    
    private void showMemoList(boolean alarmOnly) {
        if (alarmOnly) {
            TransitResultDao dao = new TransitResultDao(activity);
            int count = dao.getTransitResultCountByAlarmStatus(ALARM_STATUS_BEING_SET);
            if (count == 0) {
                ToastUtils.toast(activity, "アラームは設定されていません。");
                return;
            }
            else if (count == 1) {
                Intent intent = new Intent(activity, AlarmPlayActivity.class);
                intent.putExtra(EXTRA_KEY_TRANSIT, dao.getTransitResultIdForAlarm());
                activity.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(activity, MemoListActivity.class);
        intent.putExtra(EXTRA_KEY_ALARM_ONLY, alarmOnly);
        activity.startActivity(intent);
    }

}
