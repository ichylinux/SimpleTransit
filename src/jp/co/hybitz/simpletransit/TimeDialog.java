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

import jp.co.hybitz.android.DialogBase;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.simpletransit.model.TimeTypeAndDate;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TimeDialog extends DialogBase implements SimpleTransitConst {
	private TimeTypeAndDate timeTypeAndDate;
	
	public TimeDialog(Context context) {
	    super(context);
	}
	
	protected int getLayoutId() {
	    int orientation = Preferences.getOrientation(getContext());
	    if (orientation == ORIENTATION_PORTRAIT) {
	        return R.layout.time_dialog_portrait;
	    }
	    else if (orientation == ORIENTATION_LANDSCAPE) {
	        return R.layout.time_dialog_landscape;
	    }
	    else {
            throw new IllegalStateException("予期していないレイアウトの向きです。orientation=" + orientation);
	    }
	}

	protected void onCreate() {
		setTitle(Preferences.getText(getContext(), "時刻を選択"));
		
		final RadioGroup timeTypeRadioGroup = (RadioGroup) findViewById(R.id.time_type);
		timeTypeRadioGroup.check(R.id.departure);
		
		final DatePicker dp = (DatePicker) findViewById(R.id.date_select);

		final TimePicker tp = (TimePicker) findViewById(R.id.time_select);
		tp.setIs24HourView(true);
		
		Button ok = (Button) findViewById(R.id.time_ok);
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

		Button clear = (Button) findViewById(R.id.time_clear);
        clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				timeTypeAndDate = null;
				cancel();
			}
		});

		Button cancel = (Button) findViewById(R.id.time_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});
		
	}
	
	public TimeTypeAndDate getTimeTypeAndDate() {
		return timeTypeAndDate;
	}
	
	public void setTimeTypeAndDate(TimeTypeAndDate timeTypeAndDate) {
	    this.timeTypeAndDate = timeTypeAndDate;
	    if (this.timeTypeAndDate == null) {
	        Calendar c = Calendar.getInstance();
	        c.add(Calendar.MINUTE, 1);
		    updateTimeView(new TimeTypeAndDate(TimeType.DEPARTURE, c.getTime()));
	    }
	    else {
		    updateTimeView(this.timeTypeAndDate);
	    }
	 
	}
	
	private void updateTimeView(TimeTypeAndDate timeTypeAndDate) {
	    RadioButton dep = (RadioButton) findViewById(R.id.departure);
	    dep.setTextColor(Preferences.getTextColor(getContext()));
        RadioButton arr = (RadioButton) findViewById(R.id.arrival);
        arr.setTextColor(Preferences.getTextColor(getContext()));
	    
	    RadioGroup rg = (RadioGroup) findViewById(R.id.time_type);
	    if (timeTypeAndDate.getTimeType() == TimeType.DEPARTURE) {
	        rg.check(R.id.departure);
	    }
	    else if (timeTypeAndDate.getTimeType() == TimeType.ARRIVAL) {
	        rg.check(R.id.arrival);
	    }

	    DatePicker dp = (DatePicker) findViewById(R.id.date_select);
	    dp.updateDate(timeTypeAndDate.getYear(), timeTypeAndDate.getMonth(), timeTypeAndDate.getDay());

	    TimePicker tp = (TimePicker) findViewById(R.id.time_select);
        tp.setCurrentHour(timeTypeAndDate.getHour());
        tp.setCurrentMinute(timeTypeAndDate.getMinute());
	}
	
}
