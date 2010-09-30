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

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.common.Searcher;
import jp.co.hybitz.simpletransit.action.MaybeListener;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import jp.co.hybitz.transit.goo.GooTransitSearcherFactory;
import jp.co.hybitz.transit.google.GoogleTransitSearcher;
import jp.co.hybitz.transit.google.GoogleTransitSearcherFactory;
import jp.co.hybitz.transit.model.TransitQuery;
import jp.co.hybitz.transit.model.TransitResult;
import android.view.View;
import android.widget.Button;

class TransitSearchTask extends WebSearchTask<TransitQuery, TransitResult> implements SimpleTransitConst {
    private int searchType;
    
    public TransitSearchTask(SimpleTransit activity, int searchType) {
        super(activity);
        this.searchType = searchType;
    }
    
    protected SimpleTransit getActivity() {
        return (SimpleTransit) super.getActivity();
    }

    @Override
    protected TransitResult search(TransitQuery in) throws HttpSearchException {
        if (searchType == SEARCH_TYPE_STATIONS) {
            Searcher<TransitQuery, TransitResult> searcher = GooTransitSearcherFactory.createSearcher();
            return searcher.search(in);
        }
        else {
            GoogleTransitSearcher searcher = GoogleTransitSearcherFactory.createSearcher(Platform.ANDROID);
            return searcher.search(in);
        }
    }
    
    @Override
    protected void updateView(TransitResult out) {
        getActivity().hideInputMethod();
        
        if (!out.isOK()) {
            showResponseCode(out.getResponseCode());
            return;
        }

        if (searchType == SEARCH_TYPE_STATIONS) {
            // 駅候補を表示
            new ResultRenderer(getActivity()).renderStations(out);
            // 前の時刻と次の時刻を設定
            getActivity().updatePreviousTimeAndNextTime(searchType, out);
        }
        else {
            getActivity().hideSearchCondition();
            
            // 検索結果を表示
            new ResultRenderer(getActivity()).render(out);
            
            // 前の時刻と次の時刻を設定
            getActivity().updatePreviousTimeAndNextTime(searchType, out);
            
            // もしかしてを更新
            updateMaybe(out);
            
            if (isNew() && out.getTransitCount() > 0) {
                getActivity().saveHistory();
            }
        }
    }
    
    private boolean isNew() {
        return searchType == SEARCH_TYPE_NEW;
    }
    
    private void showResponseCode(int responseCode) {
        DialogUtils.showMessage(getActivity(), "連絡", "Googleの応答が「" + responseCode + "」でした。。", "しかたないね");
    }

    private void updateMaybe(TransitResult result) {
        Button maybe = (Button) getActivity().findViewById(R.id.maybe);

        if (Preferences.isUseMaybe(getActivity()) && result.getMaybe() != null) {
            maybe.setVisibility(View.VISIBLE);
            maybe.setOnClickListener(new MaybeListener(getActivity(), result.getMaybe()));
        }
        else {
            maybe.setVisibility(View.INVISIBLE);
        }
    }
    

}
