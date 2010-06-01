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

import java.io.IOException;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import android.content.Context;
import android.database.Cursor;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmSoundPreference extends DialogPreference {
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
        initActions(view);
        restorePreference(view);
        loadSoundFiles(view);
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            try {
                Preferences.setAlarmSoundFile(getContext(), selectedItem);
            } catch (IOException e) {
                // TODO
            }
        }
    }
    
    private void restorePreference(View view) {
        TextView tv = (TextView) view.findViewById(R.id.selected_sound);
        selectedItem = Preferences.getAlarmSoundFile(getContext());
        if (selectedItem != null) {
            tv.setText(selectedItem.getTitle());
        }
    }

    private void initActions(View view) {
        ListView lv = (ListView) view.findViewById(R.id.sound_files);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (AlarmSoundItem) parent.getItemAtPosition(position);
                TextView tv = (TextView) getDialog().findViewById(R.id.selected_sound);
                tv.setText(selectedItem.getTitle());
            }
        });
    }

    /**
     * TODO アーティスト名の取得
     * 
     * @param view
     */
    private void loadSoundFiles(View view) {
        String[] projection = new String[]{
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.TITLE,
        };
        
        Cursor cursor = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        ArrayAdapter<AlarmSoundItem> aa = new ArrayAdapter<AlarmSoundItem>(getContext(), R.layout.listview);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            String artist = null;
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE));
            aa.add(new AlarmSoundItem(id, artist, title));
            if (aa.getCount() > 30) {
                break;
            }
        }
        
        ListView lv = (ListView) view.findViewById(R.id.sound_files);
        lv.setAdapter(aa);
    }
}