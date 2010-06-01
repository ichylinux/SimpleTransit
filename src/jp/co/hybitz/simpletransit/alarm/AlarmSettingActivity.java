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

import java.util.Date;

import jp.co.hybitz.android.DialogUtils;
import jp.co.hybitz.googletransit.TransitUtil;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_setting);

        TimePicker tp = (TimePicker) findViewById(R.id.alarm_time_select);
        tp.setIs24HourView(true);

        Button button = (Button)findViewById(R.id.alarm_set);
        button.setOnClickListener(alartSet);
    }
    
    private boolean validate() {
        AlarmSoundItem item = Preferences.getAlarmSoundFile(this);
        if (item == null) {
            DialogUtils.showMessage(this, R.string.error_alarm_sound_required);
            return false;
        }
        
        return true;
    }
    
    private void startIntent() {
        Intent intent = new Intent(AlarmSettingActivity.this, OneTimeAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(AlarmSettingActivity.this, 0, intent, 0);

        TimePicker tp = (TimePicker) findViewById(R.id.alarm_time_select);
        Time selected = new Time(tp.getCurrentHour(), tp.getCurrentMinute());

        Date alarmTime = TransitUtil.getRelativeDate(selected, true);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTime(), sender);

        Toast t = Toast.makeText(AlarmSettingActivity.this, "アラームを" + selected + "に設定しました。", Toast.LENGTH_LONG);
        t.show();
    }
    
    private OnClickListener alartSet = new OnClickListener() {
        public void onClick(View v) {
            if (validate()) {
                startIntent();
            }
            
            finish();
        }
    };
}
