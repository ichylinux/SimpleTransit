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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

public abstract class DialogBase implements DialogInterface {
    private Context context;
    private AlertDialog dialog;
    private View layout;

    public DialogBase(Context context) {
        this.context = context;
        dialog = createInnerDialog();
        onCreate();
    }
    
    public void setTitle(CharSequence title) {
        dialog.setTitle(title);
    }
    
    protected abstract int getLayoutId();
    protected abstract void onCreate();
        
    protected View findViewById(int id) {
        return layout.findViewById(id);
    }
    
    protected Context getContext() {
        return context;
    }
    
    private AlertDialog createInnerDialog() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(getLayoutId(), null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        return dialog;
    }
    
    public void show() {
        dialog.show();
    }

    public void cancel() {
        dialog.cancel();
    }

    public void dismiss() {
        dialog.dismiss();
    }
    
    public void setOnDismissListener(OnDismissListener listener) {
        dialog.setOnDismissListener(listener);
    }
}
