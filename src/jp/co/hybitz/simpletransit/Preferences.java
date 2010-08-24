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

import jp.co.hybitz.common.StringUtils;
import jp.co.hybitz.csv.CsvException;
import jp.co.hybitz.csv.CsvReader;
import jp.co.hybitz.csv.CsvWriter;
import jp.co.hybitz.simpletransit.alarm.model.AlarmSoundItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class Preferences extends PreferenceActivity implements SimpleTransitConst {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    public static boolean isFullInput(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("full_input", false);
    }

    public static boolean isUseLatestQueryHistory(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_latest_query_history", false);
    }

    public static boolean isUseExpress(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_express", false);
    }
    
    public static boolean isUseAirline(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_airline", false);
    }
    
    public static boolean isUseMaybe(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("use_maybe", false);
    }

    public static int getColorSetting(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String colorSetting = sp.getString("color", String.valueOf(COLOR_BLACK));
        return Integer.parseInt(colorSetting);
    }

    public static int getOrientation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String orientation = sp.getString("orientation", String.valueOf(ORIENTATION_PORTRAIT));
        return Integer.parseInt(orientation);
    }

    public static int getTextColor(Context context) {
        switch (getColorSetting(context)) {
        case COLOR_BLACK :
            return Color.WHITE;
        case COLOR_WHITE :
            return Color.BLACK;
        case COLOR_BEIGE :
            return Color.rgb(0x41, 0x69, 0xE1);
        default :
            return Color.WHITE;
        }
    }
    
    public static int getBackgroundColor(Context context) {
        switch (getColorSetting(context)) {
        case COLOR_BLACK :
            return Color.BLACK;
        case COLOR_WHITE :
            return Color.WHITE;
        case COLOR_BEIGE :
            return Color.rgb(0xF5, 0xF5, 0xDC);
        default :
            return Color.BLACK;
        }
    }
    
    public static int getBackgroundResource(Context context) {
        switch (Preferences.getColorSetting(context)) {
        case COLOR_BLACK :
            return R.xml.selector_background_for_black;
        case COLOR_WHITE :
            return R.xml.selector_background_for_white;
        case COLOR_BEIGE :
            return R.xml.selector_background_for_beige;
        default :
            return R.xml.selector_background_for_black;
        }
    }

    public static CharSequence getText(Context context, String text) {
        if (text == null) {
            return null;
        }

        ForegroundColorSpan fcs = new ForegroundColorSpan(getTextColor(context));
        SpannableString ret = new SpannableString(text);
        ret.setSpan(fcs, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ret;
    }

    public static void initTheme(Context context) {
    	
        int color = getColorSetting(context);
        if (color == COLOR_BLACK) {
        	context.setTheme(R.style.black);
        }
        else if (color == COLOR_WHITE) {
        	context.setTheme(R.style.white);
        }
        else if (color == COLOR_BEIGE) {
            context.setTheme(R.style.beige);
        }
    }

    public static int getFontSizeSetting(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSize = sp.getString("font_size", String.valueOf(FONT_SIZE_SMALL));
        return Integer.parseInt(fontSize);
    }
    
    public static int getTextSize(Context context) {
        int fontSize = Preferences.getFontSizeSetting(context);
        switch (fontSize) {
        case FONT_SIZE_SMALL :
            return 14;
        case FONT_SIZE_MEDIUM :
            return 16;
        case FONT_SIZE_LARGE :
            return 18;
        default :
            return 14;
        }
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
