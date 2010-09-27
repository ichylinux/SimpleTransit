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

import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.db.TransitResultDao;
import jp.co.hybitz.simpletransit.model.TransitResultEx;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class DisplayNameSettingDialog extends Dialog implements SimpleTransitConst {
	private TransitResultEx result;
	private EditText displayName;
	
	public DisplayNameSettingDialog(Context context, TransitResultEx result) {
	    super(context);
	    this.result = result;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

	private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.display_name_setting_dialog);
		
		displayName = (EditText) findViewById(R.id.display_name);
		displayName.setText(result.getDisplayName());

		Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        	    String text = displayName.getText().toString();
        	    
        	    TransitResultDao dao = new TransitResultDao(getContext());
        	    if (dao.updateDisplayName(result.getId(), text) == 1) {
        	        result.setDisplayName(text);
        	    }
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
