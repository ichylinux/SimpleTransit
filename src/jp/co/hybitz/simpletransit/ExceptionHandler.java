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

import java.net.UnknownHostException;

import jp.co.hybitz.android.DialogUtils;
import jp.co.hybitz.googletransit.TransitSearchException;
import android.app.Activity;
import android.util.Log;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class ExceptionHandler {
    private Activity activity;
    
    public ExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    public void handleException(TransitSearchException e) {
        Log.e("SimpleTransit", e.getMessage(), e);
        
        if (e.getCause() instanceof UnknownHostException) {
            DialogUtils.showMessage(activity, R.string.error_unknow_host_exception);
        }
        else {
            apologize(e);
        }
    }
    
    /**
     * エラーが出たので謝ります。。
     * 
     * @param e
     */
    private void apologize(TransitSearchException e) {
        DialogUtils.showMessage(
                activity,
                "ごめん！！",
                "こんなエラー出た。。\n" + e.getCause().getClass().getSimpleName() + "\n" + e.getMessage(),
                "許す");
    }
    
}
