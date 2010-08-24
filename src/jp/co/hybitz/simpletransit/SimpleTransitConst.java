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

import android.view.Menu;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public interface SimpleTransitConst {
    public static final String APP = "SimpleTransit";
    public static final String APP_ID = "cecab237-aa57-46cd-ad26-3cc3a6135da4";

    public static final int ALARM_STATUS_NONE = 0;
    public static final int ALARM_STATUS_BEING_SET = 1;
    public static final int ALARM_STATUS_FINISHED = 2;

    public static final int COLOR_BLACK = 1;
    public static final int COLOR_WHITE = 2;
    public static final int COLOR_BEIGE = 3;

    public static final String EXTRA_KEY_ALARM_ONLY = "alarmOnly";
    public static final String EXTRA_KEY_LOCATION = "location";
    public static final String EXTRA_KEY_START_ALARM = "startAlarm";
    public static final String EXTRA_KEY_TRANSIT = "transit";
    public static final String EXTRA_KEY_TRANSIT_QUERY = "transit_query";
    public static final String EXTRA_KEY_TRAVEL_DELAY_RESULT = "travel_delay_result";
    
    public static final int FONT_SIZE_SMALL = 1;
    public static final int FONT_SIZE_MEDIUM = 2;
    public static final int FONT_SIZE_LARGE = 3;

    public static final int MENU_ITEM_PREFERENCES = Menu.FIRST + 1;
    public static final int MENU_ITEM_QUERY_HISTORY = Menu.FIRST + 2;
    public static final int MENU_ITEM_ALARM = Menu.FIRST + 3;
    public static final int MENU_ITEM_MEMO = Menu.FIRST + 4;
    public static final int MENU_ITEM_QUIT = Menu.FIRST + 5;
    public static final int MENU_ITEM_DELETE = Menu.FIRST + 6;
    public static final int MENU_ITEM_VOICE = Menu.FIRST + 7;
    public static final int MENU_ITEM_MEMO_CREATE = Menu.FIRST + 8;
    public static final int MENU_ITEM_ALARM_CREATE = Menu.FIRST + 9;
    public static final int MENU_ITEM_CANCEL = Menu.FIRST + 10;
    public static final int MENU_ITEM_COPY_TEXT = Menu.FIRST + 11;
    public static final int MENU_ITEM_SET_FROM = Menu.FIRST + 12;
    public static final int MENU_ITEM_SET_TO = Menu.FIRST + 13;
    public static final int MENU_ITEM_SET_FAVORITE = Menu.FIRST + 14;
    public static final int MENU_ITEM_SET_FAVORITE_REVERSE = Menu.FIRST + 15;
    public static final int MENU_ITEM_SELECT_LOCATION = Menu.FIRST + 16;
    public static final int MENU_ITEM_REVERSE_LOCATION = Menu.FIRST + 17;
    public static final int MENU_ITEM_TRAVEL_DELAY = Menu.FIRST + 18;
    public static final int MENU_ITEM_DELETE_OLD_MEMO = Menu.FIRST + 19;
    
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;

    public static final int REQUEST_CODE_PREFERENCE = 1;
    public static final int REQUEST_CODE_SELECT_TRANSIT_QUERY = 2;
    public static final int REQUEST_CODE_VOICE_INPUT = 3;
    
    public static final int RESULT_CODE_ROUTE_SELECTED = 1;
    public static final int RESULT_CODE_FROM_SELECTED = 2;
    public static final int RESULT_CODE_TO_SELECTED = 3;
    
}
