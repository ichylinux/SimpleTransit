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

import java.util.Iterator;

import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitDetail;
import jp.co.hybitz.googletransit.model.TransitResult;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author ichy <ichylinux@gmail.com>
 */
class ResultRenderer {
    
    private Activity activity;
    
    ResultRenderer(Activity activity) {
        this.activity = activity;
    }

    void render(TransitResult result) {
        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity, R.layout.listview);
        aa.add(createSummary(result));
        
        for (Iterator<Transit> it = result.getTransits().iterator(); it.hasNext();) {
            Transit transit = it.next();
            aa.add(createResult(transit));
        }
        
        ListView lv = (ListView) activity.findViewById(R.id.results);
        lv.setAdapter(aa);
    }
    

    private String createSummary(TransitResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getTransitCount() > 0) {
            sb.append(result.getFrom() + " ～ " + result.getTo());
            if (result.getTimeType() == TimeType.DEPARTURE) {
                sb.append("　" + result.getTime().getTimeAsString(true) + "発\n");
                
            }
            else if (result.getTimeType() == TimeType.ARRIVAL) {
                sb.append("　" + result.getTime().getTimeAsString(true) + "着\n");
                
            }
            else if (result.getTimeType() == TimeType.LAST) {
                sb.append("　終電\n");
            }
            else {
                throw new IllegalStateException("予期していない時刻タイプです。timeType=" + result.getTimeType());
            }
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
                sb.append(detail.getDeparture().getTime().getTimeAsString(true)).append("発　");
                sb.append(detail.getDeparture().getPlace()).append("\n");
                sb.append(detail.getArrival().getTime().getTimeAsString(true)).append("着　");
                sb.append(detail.getArrival().getPlace());
            }

            if (i < transit.getDetails().size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
