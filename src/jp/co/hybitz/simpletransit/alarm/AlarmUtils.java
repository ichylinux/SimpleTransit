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

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmUtils {

    public static String toDateTimeString(long createdAt) {
        long yyyyMMdd = createdAt / 1000000;
        long yyyy = yyyyMMdd / 10000;
        long MMdd = yyyyMMdd % 10000;
        long MM = MMdd / 100;
        long dd = MMdd % 100;
        long HHmmss = createdAt % 1000000;
        long HHmm = HHmmss / 100;
        long HH = HHmm/ 100;
        long mm = HHmm % 100;
        
        return "" + yyyy + "/" + (MM >= 10 ? "" : "0") + MM + "/" + (dd >= 10 ? "" : "0") + dd + " " + (HH >= 10 ? "" : "0") + HH + ":" + (mm >= 10 ? "" : "0") + mm;
    }
}
