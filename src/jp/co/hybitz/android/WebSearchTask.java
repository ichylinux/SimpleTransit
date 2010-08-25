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
package jp.co.hybitz.android;

import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.simpletransit.ExceptionHandler;
import jp.co.hybitz.simpletransit.Preferences;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public abstract class WebSearchTask<IN, OUT> extends AsyncTask<IN, Integer, OUT> {
    private Activity activity;
    private ProgressDialog dialog;
    private HttpSearchException e;

    public WebSearchTask(Activity activity) {
        this.activity = activity;
    }

    protected abstract OUT search(IN in) throws HttpSearchException;
    protected abstract void updateView(OUT out);

    protected Activity getActivity() {
        return activity;
    }
    
    protected boolean useProgressDialog() {
        return true;
    }
    
    protected boolean isCancelable() {
        return true;
    }
    
    @Override
    protected void onPreExecute() {
        if (useProgressDialog()) {
            dialog = new ProgressDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setTitle(Preferences.getText(activity, "通信中"));
            dialog.setMessage(Preferences.getText(activity, "しばらくお待ち下さい"));
            
            if (isCancelable()) {
                dialog.setCancelable(isCancelable());
                dialog.setOnCancelListener(new OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        cancel(true);
                    }
                });
            }
            
            dialog.show();
        }
    }
    
    @Override
    protected OUT doInBackground(IN... in) {
        try {
            return search(in.length > 0 ? in[0] : null);
        }
        catch (HttpSearchException e) {
            this.e = e;
        }
        
        return null;
    }
    
    @Override
    protected void onPostExecute(OUT result) {
        dialog.dismiss();
        
        if (e != null) {
            new ExceptionHandler(activity).handleException(e);
        }
        else {
            updateView(result);
        }
    }

    @Override
    protected void onCancelled() {
    }

}
