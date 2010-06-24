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

import java.io.IOException;

import jp.co.hybitz.csv.CsvException;
import jp.co.hybitz.csv.CsvReader;
import jp.co.hybitz.csv.CsvWriter;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import jp.co.hybitz.util.StringUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    public static boolean isUseExpress(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_express", false);
    }
    
    public static boolean isUseAirline(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_airline", false);
    }
    
    public static AlarmSoundItem getAlarmSoundFile(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String s = sp.getString("alarm_sound_file", null);
        if (StringUtils.isNotEmpty(s)) {
            try {
                String[] csv = new CsvReader(s).read();
                return new AlarmSoundItem(Integer.parseInt(csv[0]), csv[1], csv[2]);
            } catch (CsvException e) {
                Log.e("SimpleTransit", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("SimpleTransit", e.getMessage(), e);
            }
        }
        return null;
    }
    
    public static void setAlarmSoundFile(Context context, AlarmSoundItem item) throws IOException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (item != null) {
            String[] csv = new String[]{ String.valueOf(item.getId()), item.getArtist(), item.getTitle() };
            sp.edit().putString("alarm_sound_file", CsvWriter.toString(csv)).commit();
        }
        else {
            sp.edit().putString("alarm_sound_file", null).commit();
        }
    }
    
    /**
     * バイブレーションで通知するかどうか
     * @param context
     * @return
     */
    public static boolean isNoSoundButVibration(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("no_sound_but_vibration", false);
    }
}
