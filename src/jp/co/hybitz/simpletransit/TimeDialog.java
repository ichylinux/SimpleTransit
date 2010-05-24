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

import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TimePicker;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TimeDialog implements DialogInterface {
	private AlertDialog dialog;
	private View layout;
	private TimeType timeType;
	private Time time;
	
	public TimeDialog(Activity activity) {
		dialog = createInnerDialog(activity);
	}

	private AlertDialog createInnerDialog(Activity activity) {
		Context context = activity.getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.time_dialog, (ViewGroup) activity.findViewById(R.id.time_dialog_root));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(layout);
		AlertDialog dialog = builder.create();
		
		final RadioGroup timeTypeRadioGroup = (RadioGroup) layout.findViewById(R.id.time_type);
		timeTypeRadioGroup.check(R.id.departure);
		
		final TimePicker tp = (TimePicker) layout.findViewById(R.id.time_select);
		tp.setIs24HourView(true);
		
		Button ok = (Button) layout.findViewById(R.id.time_ok);
        ok.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		if (timeTypeRadioGroup.getCheckedRadioButtonId() == R.id.departure) {
        			timeType = TimeType.DEPARTURE;
        		}
        		else if (timeTypeRadioGroup.getCheckedRadioButtonId() == R.id.arrival) {
        			timeType = TimeType.ARRIVAL;
        		}
        		
        		time = new Time(tp.getCurrentHour(), tp.getCurrentMinute());
        		dismiss();
			}
		});

		Button clear = (Button) layout.findViewById(R.id.time_clear);
        clear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				timeType = null;
				time = null;
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
	
	public Time getTime() {
		return time;
	}
	
	public void setTime(Time time) {
	    this.time = time;
	    
	    if (time != null) {
    	    TimePicker tp = (TimePicker) layout.findViewById(R.id.time_select);
    	    tp.setCurrentHour(time.getHour());
    	    tp.setCurrentMinute(time.getMinute());
	    }
	}
	
	public TimeType getTimeType() {
		return timeType;
	}
	
	public void setTimeType(TimeType timeType) {
	    this.timeType = timeType;
	    
	    RadioGroup rg = (RadioGroup) layout.findViewById(R.id.time_type);
	    if (timeType == TimeType.DEPARTURE) {
	        rg.check(R.id.departure);
	    }
	    else if (timeType == TimeType.ARRIVAL) {
	        rg.check(R.id.arrival);
	    }
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
