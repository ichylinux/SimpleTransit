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
 * 検索結果を描画するクラス
 * 
 * @author ichy <ichylinux@gmail.com>
 */
class ResultRenderer {
    
    private Activity activity;
    
    /**
     * コンストラクタ
     * 
     * @param activity
     */
    ResultRenderer(Activity activity) {
        this.activity = activity;
    }

    void render(TransitResult result) {
        String prefecture = "";
        if (isSamePrefecture(result.getFrom(), result.getTo())) {
            prefecture = "（" + result.getFrom().split("（")[1];
        }
        
        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity, R.layout.listview);
        aa.add(createSummary(prefecture, result));
        
        for (Iterator<Transit> it = result.getTransits().iterator(); it.hasNext();) {
            Transit transit = it.next();
            aa.add(createResult(prefecture, transit));
        }
        
        ListView lv = (ListView) activity.findViewById(R.id.results);
        lv.setAdapter(aa);
    }
    
    private boolean isSamePrefecture(String from, String to) {
        if (from == null || to == null) {
            return false;
        }

        String[] fromSplit = from.split("（");
        String[] toSplit = to.split("（");
        
        if (fromSplit.length != 2 || toSplit.length != 2) {
            return false;
        }
        
        return fromSplit[1].equals(toSplit[1]);
    }

    private String createSummary(String prefecture, TransitResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getTransitCount() > 0) {
            sb.append(result.getFrom().replaceAll(prefecture, ""));
            sb.append(" ～ ");
            sb.append(result.getTo().replaceAll(prefecture, ""));
            sb.append("　");
            
            if (result.getTimeType() == TimeType.DEPARTURE) {
                sb.append(result.getTime() + "発");
                
            }
            else if (result.getTimeType() == TimeType.ARRIVAL) {
                sb.append(result.getTime() + "着");
                
            }
            else if (result.getTimeType() == TimeType.FIRST) {
                sb.append("始発");
            }
            else if (result.getTimeType() == TimeType.LAST) {
                sb.append("終電");
            }
            else {
                throw new IllegalStateException("予期していない時刻タイプです。timeType=" + result.getTimeType());
            }
            
            sb.append("\n");
            sb.append("検索結果は " + result.getTransitCount() + " 件です。");
        } else {
            sb.append(activity.getString(R.string.no_route_found));
        }
        return sb.toString();
    }
    
    private String createResult(String prefecture, Transit transit) {
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
                sb.append(detail.getDeparture().getTime()).append("発　");
                sb.append(detail.getDeparture().getPlace().replaceAll(prefecture, "")).append("\n");
                sb.append(detail.getArrival().getTime()).append("着　");
                sb.append(detail.getArrival().getPlace().replaceAll(prefecture, ""));
            }

            if (i < transit.getDetails().size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
