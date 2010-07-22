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
package jp.co.hybitz.simpletransit.action;

import jp.co.hybitz.googletransit.model.Maybe;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.EditText;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class MaybeListener implements View.OnClickListener {
    private Activity activity;
    private Maybe maybe;

    public MaybeListener(Activity activity, Maybe maybe) {
        this.activity = activity;
        this.maybe = maybe;
    }
    
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(Preferences.getText(activity, "もしかして"));
        builder.setMessage(Preferences.getText(activity, maybe.toString()));
        builder.setCancelable(true);
        builder.setPositiveButton("はい", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText from = (EditText) activity.findViewById(R.id.from);
                from.setText(maybe.getFrom());
                EditText to = (EditText) activity.findViewById(R.id.to);
                to.setText(maybe.getTo());
            }
        });
        builder.setNegativeButton("いいえ", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
