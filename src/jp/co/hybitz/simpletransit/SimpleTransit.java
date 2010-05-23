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

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import jp.co.hybitz.googletransit.Platform;
import jp.co.hybitz.googletransit.TransitSearchException;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitDetail;
import jp.co.hybitz.googletransit.model.TransitQuery;
import jp.co.hybitz.googletransit.model.TransitResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransit extends Activity {
	private TimeType timeType = TimeType.DEPARTURE;
	private String hour;
	private String minute;

	/**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView time = (TextView) findViewById(R.id.time);
        time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTimeDialog();
			}
		});
        
        CheckBox last = (CheckBox) findViewById(R.id.last);
        last.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TextView time = (TextView) findViewById(R.id.time);
				time.setEnabled(!isChecked);
			}
		});
        
        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				search();
			}
		});
    }
    
    private void showTimeDialog() {
    	final TimeDialog dialog = new TimeDialog(this);
    	dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface di) {
				TextView time = (TextView) findViewById(R.id.time);
				Integer selectedTime = dialog.getTime();
				if (selectedTime != null) {
                    timeType = dialog.getTimeType();
                    hour = String.valueOf(selectedTime / 100);
					if (hour.length() == 1) {
						hour = "0" + hour;
					}
					minute = String.valueOf(selectedTime % 100);
					if (minute.length() == 1) {
						minute = "0" + minute;
					}
				}
				else {
				    timeType = null;
				    hour = null;
				    minute = null;
				}
				
				if (hour != null && minute != null) {
	                time.setText(hour + ":" + minute + "に" + (timeType == TimeType.DEPARTURE ? "出発" : "到着"));
				}
				else {
				    time.setText(null);
				}
			}
		});
    	
    	if (hour != null && minute != null) {
    	    dialog.setTimeType(timeType);
    		dialog.setTime(Integer.parseInt(hour), Integer.parseInt(minute));
    	}
    	dialog.show();
    }
    
	private void renderResult(TransitResult result) {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.listview);
        aa.add(createSummary(result));
        
        for (Iterator<Transit> it = result.getTransits().iterator(); it.hasNext();) {
            Transit transit = it.next();
            aa.add(createResult(transit));
        }
        
        ListView lv = (ListView) findViewById(R.id.results);
        lv.setAdapter(aa);
    }
    
    private TransitQuery createQuery() {
        EditText from = (EditText) findViewById(R.id.from);
        EditText to = (EditText) findViewById(R.id.to);
        CheckBox last = (CheckBox) findViewById(R.id.last);

        TransitQuery query = new TransitQuery();
        query.setFrom(from.getText().toString());
        query.setTo(to.getText().toString());
        if (last.isChecked()) {
            query.setTimeType(TimeType.LAST);
        } else {
            query.setTimeType(timeType);

            if (hour != null && minute != null) {
            	query.setDate(getDate());
            	query.setTime(hour + minute);
            }
        }

        return query;
    }
    
    private String getDate() {
        Calendar c = Calendar.getInstance();

        String now = new SimpleDateFormat("hhmm").format(c.getTime());
        if (now.compareTo(hour + minute) < 0) {
        	return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
        }
        else {
        	c.add(Calendar.DATE, 1);
        	return new SimpleDateFormat("yyyyMMdd").format(c.getTime());
        }
    }
    
    private void search() {
        try {
            TransitSearcher searcher = TransitSearcherFactory.createSearcher(Platform.ANDROID);
            TransitResult result = searcher.search(createQuery());
            
            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                renderResult(result);
            } else {
                showMessage(String.valueOf(result.getResponseCode()));
            }
            
        } catch (TransitSearchException e) {
            Log.e("SimpleTransit", e.getMessage(), e);
            apologize(e);
        }
        
    }
    
    private void apologize(TransitSearchException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ごめん！！");
        builder.setMessage("こんなエラー出た。。\n" + e.getCause().getClass().getSimpleName() + "\n" + e.getMessage());
        builder.setPositiveButton("許す", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    
    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("連絡");
        builder.setMessage("Googleの応答が「" + message + "」でした。。");
        builder.setPositiveButton("許す", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private String createSummary(TransitResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getTransitCount() > 0) {
            sb.append(result.getTitle()).append("\n");
            sb.append("検索結果は " + result.getTransitCount() + " 件です。");
        } else {
            sb.append("該当するルートが見つかりませんでした。");
        }
        return sb.toString();
    }
    
    private String createResult(Transit transit) {
    	StringBuilder sb = new StringBuilder();

    	sb.append(transit.getTimeAndFare());
    	if (transit.getTransferCount() > 0) {
    		sb.append(" - 乗り換え" + transit.getTransferCount() + "回");
    	}
    	sb.append("\n");

    	for (int i = 0; i < transit.getDetails().size(); i ++) {
    		TransitDetail detail = transit.getDetails().get(i);
    		
    		sb.append(detail.getRoute());

    		if (!detail.isWalking()) {
    			sb.append("\n");
	    		sb.append(detail.getDeparture().getTime()).append("発　").append(detail.getDeparture().getPlace()).append("\n");
	    		sb.append(detail.getArrival().getTime()).append("着　").append(detail.getArrival().getPlace());
    		}

    		if (i < transit.getDetails().size() - 1) {
    			sb.append("\n");
    		}
    	}
    	return sb.toString();
    }
}