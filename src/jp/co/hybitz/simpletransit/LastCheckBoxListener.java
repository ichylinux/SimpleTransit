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

import android.app.Activity;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class LastCheckBoxListener implements OnCheckedChangeListener {
    private Activity activity;
    
    LastCheckBoxListener(Activity activity) {
        this.activity = activity;
    }
    
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TextView timeView = (TextView) activity.findViewById(R.id.time);
        timeView.setEnabled(!isChecked);
        if (timeView.isEnabled()) {
            timeView.setTextColor(Color.BLACK);
            timeView.setBackgroundResource(R.layout.time_border_enabled);
        }
        else {
            timeView.setTextColor(Color.GRAY);
            timeView.setBackgroundResource(R.layout.time_border_disabled);
        }
    }
}
