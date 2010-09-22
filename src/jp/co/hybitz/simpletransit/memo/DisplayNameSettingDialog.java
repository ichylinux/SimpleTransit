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
package jp.co.hybitz.simpletransit.memo;

import jp.co.hybitz.android.DialogBase;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.model.SimpleTransitResult;
import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class DisplayNameSettingDialog extends DialogBase implements SimpleTransitConst {
	private SimpleTransitResult result;
	
	public DisplayNameSettingDialog(Context context, SimpleTransitResult result) {
	    super(context);
	    this.result = result;
	}
	
	@Override
	protected int getLayoutId() {
	    return R.layout.display_name_setting_dialog;
	}

	@Override
	protected void onCreate() {
		setTitle(Preferences.getText(getContext(), "表示名を設定"));

		Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		dismiss();
			}
		});

		Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});
		
	}
}
