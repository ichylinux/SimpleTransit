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

import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.simpletransit.model.TimeTypeAndDate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TimeDialog implements DialogInterface {
    private Activity activity;
	private AlertDialog dialog;
	private View layout;
	private TimeTypeAndDate timeTypeAndDate;
	
	public TimeDialog(Activity activity) {
	    this.activity = activity;
		dialog = createInnerDialog();
	}

	private AlertDialog createInnerDialog() {
		Context context = activity.getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.time_dialog, (ViewGroup) activity.findViewById(R.id.time_dialog_root));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(layout);
		AlertDialog dialog = builder.create();
		
		final RadioGroup timeTypeRadioGroup = (RadioGroup) layout.findViewById(R.id.time_type);
		timeTypeRadioGroup.check(R.id.departure);
		
		final DatePicker dp = (DatePicker) layout.findViewById(R.id.date_select);

		final TimePicker tp = (TimePicker) layout.findViewById(R.id.time_select);
		tp.setIs24HourView(true);
		
		Button ok = (Button) layout.findViewById(R.id.time_ok);
        ok.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		TimeType tt;
        		if (timeTypeRadioGroup.getCheckedRadioButtonId() == R.id.departure) {
        			tt = TimeType.DEPARTURE;
        		}
        		else if (timeTypeRadioGroup.getCheckedRadioButtonId() == R.id.arrival) {
        			tt = TimeType.ARRIVAL;
        		}
        		else {
        			throw new IllegalStateException("出発・到着が正しく指定されませんでした。");
        		}

        		Calendar c = Calendar.getInstance();
        		c.set(Calendar.YEAR, dp.getYear());
        		c.set(Calendar.MONTH, dp.getMonth());
        		c.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
        		c.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
        		c.set(Calendar.MINUTE, tp.getCurrentMinute());
        		timeTypeAndDate = new TimeTypeAndDate(tt, c.getTime());
        		dismiss();
			}
		});

		Button clear = (Button) layout.findViewById(R.id.time_clear);
        clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				timeTypeAndDate = null;
				cancel();
			}
		});

		Button cancel = (Button) layout.findViewById(R.id.time_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});
		
		return dialog;
	}
	
	public TimeTypeAndDate getTimeTypeAndDate() {
		return timeTypeAndDate;
	}
	
	public void setTimeTypeAndDate(TimeTypeAndDate timeTypeAndDate) {
	    this.timeTypeAndDate = timeTypeAndDate;
	    if (this.timeTypeAndDate == null) {
	        Calendar c = Calendar.getInstance();
	        c.add(Calendar.MINUTE, 1);
	        this.timeTypeAndDate = new TimeTypeAndDate(TimeType.DEPARTURE, c.getTime());
	    }
	 
	    updateTimeView();
	}
	
	private void updateTimeView() {
	    RadioButton dep = (RadioButton) layout.findViewById(R.id.departure);
	    dep.setTextColor(Preferences.getTextColor(activity));
        RadioButton arr = (RadioButton) layout.findViewById(R.id.arrival);
        arr.setTextColor(Preferences.getTextColor(activity));
	    
	    RadioGroup rg = (RadioGroup) layout.findViewById(R.id.time_type);
	    if (timeTypeAndDate.getTimeType() == TimeType.DEPARTURE) {
	        rg.check(R.id.departure);
	    }
	    else if (timeTypeAndDate.getTimeType() == TimeType.ARRIVAL) {
	        rg.check(R.id.arrival);
	    }

	    DatePicker dp = (DatePicker) layout.findViewById(R.id.date_select);
	    dp.updateDate(timeTypeAndDate.getYear(), timeTypeAndDate.getMonth(), timeTypeAndDate.getDay());

	    TimePicker tp = (TimePicker) layout.findViewById(R.id.time_select);
        tp.setCurrentHour(timeTypeAndDate.getHour());
        tp.setCurrentMinute(timeTypeAndDate.getMinute());
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
