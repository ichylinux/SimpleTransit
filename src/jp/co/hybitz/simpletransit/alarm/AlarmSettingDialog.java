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

import jp.co.hybitz.android.DateUtils;
import jp.co.hybitz.android.DialogUtils;
import jp.co.hybitz.android.ToastUtils;
import jp.co.hybitz.googletransit.TransitUtil;
import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmSettingDialog implements DialogInterface, SimpleTransitConst {
    private Activity activity;
    private AlertDialog dialog;
    private View layout;
    private TransitResult transitResult;
    private Transit transit;
    
    public AlarmSettingDialog(Activity activity, TransitResult transitResult, Transit transit) {
        this.activity = activity;
        this.transitResult = transitResult;
        this.transit = transit;
        dialog = createInnerDialog();
    }

    private AlertDialog createInnerDialog() {
        Context context = activity.getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.alarm_setting, (ViewGroup) activity.findViewById(R.id.alart_setting_root));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        
        TimePicker tp = (TimePicker) layout.findViewById(R.id.alarm_time_select);
        tp.setIs24HourView(true);
        
        Button button = (Button) layout.findViewById(R.id.alarm_set);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validate()) {
                    startIntent();
                }
                dismiss();
            }
        });
        
        Button cancel = (Button) layout.findViewById(R.id.alarm_set_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancel();
            }
        });

        return dialog;
    }
    
    private boolean validate() {
        AlarmSoundItem item = Preferences.getAlarmSoundFile(activity);
        if (item == null) {
            DialogUtils.showMessage(activity, R.string.error_alarm_sound_required);
            return false;
        }
        
        return true;
    }
    
    private void startIntent() {
        TimePicker tp = (TimePicker) layout.findViewById(R.id.alarm_time_select);
        Time selected = new Time(tp.getCurrentHour(), tp.getCurrentMinute());
        Date alarmTime = TransitUtil.getRelativeDate(selected, true);

        SimpleTransitResult atr = new SimpleTransitResult(transitResult);
        atr.setAlarmStatus(ALARM_STATUS_SET);
        atr.setAlarmAt(DateUtils.toLong(alarmTime));
        long id = new TransitResultDao(activity).createTransitResult(atr, transit);

        Intent intent = new Intent(activity, OneTimeAlarm.class);
        intent.putExtra(EXTRA_KEY_START_ALARM, true);
        intent.putExtra(EXTRA_KEY_TRANSIT, id);
        PendingIntent sender = PendingIntent.getBroadcast(activity, 0, intent, 0);
        AlarmManager am = (AlarmManager)activity.getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTime(), sender);

        ToastUtils.toastLong(activity, "アラームを" + selected + "に設定しました。");
    }
    
    public void show() {
        dialog.show();
    }

    public void cancel() {
        dialog.cancel();
    }

    public void dismiss() {
        dialog.dismiss();
    }
    
    public void setOnDismissListener(OnDismissListener listener) {
        dialog.setOnDismissListener(listener);
    }
}
