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
package jp.co.hybitz.simpletransit.util;

import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class ToastUtils implements SimpleTransitConst {

    public static void toastLong(Activity activity, String message) {
        if (Preferences.getColorSetting(activity) == COLOR_BLACK) {
            Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            toast(activity, message, Toast.LENGTH_LONG);
        }
    }
    
    public static void toast(Activity activity, String message) {
        if (Preferences.getColorSetting(activity) == COLOR_BLACK) {
            Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            toast(activity, message, Toast.LENGTH_SHORT);
        }
    }
    
    private static void toast(Activity activity, String message, int duration) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_for_white_theme, (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(activity);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
}
