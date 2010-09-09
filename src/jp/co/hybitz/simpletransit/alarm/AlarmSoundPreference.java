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

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmSoundPreference extends DialogPreference implements SimpleTransitConst {
    private AlarmSoundItem selectedItem;

    public AlarmSoundPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @see android.preference.DialogPreference#onCreateDialogView()
     */
    @Override
    protected View onCreateDialogView() {
        setDialogLayoutResource(R.layout.alarm_sound_dialog);
        return super.onCreateDialogView();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        initAction(view);
        restorePreference(view);
    }
    
    private void initAction(View view) {
        Button b = (Button) view.findViewById(R.id.pick_up_music);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                ((Activity)getContext()).startActivityForResult(Intent.createChooser(intent, "音楽を検索"), REQUEST_CODE_SELECT_MUSIC);
                getDialog().dismiss();
            }
        });
    }
    
    private void restorePreference(View view) {
        TextView tv = (TextView) view.findViewById(R.id.selected_sound);
        selectedItem = Preferences.getAlarmSoundFile(getContext());
        if (selectedItem != null) {
            tv.setText(selectedItem.getTitle());
        }
    }
}
