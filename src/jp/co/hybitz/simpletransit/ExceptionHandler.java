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

import java.net.SocketException;
import java.net.UnknownHostException;

import jp.co.hybitz.googletransit.TransitSearchException;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import android.content.Context;
import android.util.Log;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class ExceptionHandler {
    private Context context;
    
    public ExceptionHandler(Context context) {
        this.context = context;
    }

    public void handleException(TransitSearchException e) {
        Log.e("SimpleTransit", e.getMessage(), e);
        
        if (e.getCause() instanceof UnknownHostException) {
            DialogUtils.showMessage(context, R.string.error_unknow_host_exception);
        }
        else if (e.getCause() instanceof SocketException) {
            if ("The operation timed out".equals(e.getCause().getMessage())) {
                DialogUtils.showMessage(context, R.string.error_operation_timed_out);
            }
            else {
                DialogUtils.showMessage(context, R.string.error_socket_exception);
            }
        }
        else {
            throw new IllegalStateException(e.getMessage() + "\n[\n" + e.getHtml() + "\n]", e);
        }
    }
    
}
