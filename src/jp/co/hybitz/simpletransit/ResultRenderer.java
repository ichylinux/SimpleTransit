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
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.simpletransit.model.TransitItem;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 検索結果を描画するクラス
 * 
 * @author ichy <ichylinux@gmail.com>
 */
public class ResultRenderer {
    
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
        TextView summary = (TextView) activity.findViewById(R.id.tv_summary);
        summary.setText(createSummary(result));

        ArrayAdapter<TransitItem> aa = new ArrayAdapter<TransitItem>(activity, R.layout.listview);
        for (Iterator<Transit> it = result.getTransits().iterator(); it.hasNext();) {
            Transit transit = it.next();
            aa.add(new TransitItem(result, transit));
        }
        
        ListView lv = (ListView) activity.findViewById(R.id.results);
        lv.setAdapter(aa);
    }
    
    public static String createTitle(TransitResult result) {
        String prefecture = result.getPrefecture() == null ? "" : "（" + result.getPrefecture() + "）";

        StringBuilder sb = new StringBuilder();
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

        return sb.toString();
    }
    
    private String createSummary(TransitResult result) {
        StringBuilder sb = new StringBuilder();
        if (result.getTransitCount() > 0) {
            sb.append(createTitle(result));
            sb.append("\n");
            sb.append("検索結果は " + result.getTransitCount() + " 件です。");
        } else {
            sb.append(activity.getString(R.string.no_route_found));
        }
        return sb.toString();
    }
}
