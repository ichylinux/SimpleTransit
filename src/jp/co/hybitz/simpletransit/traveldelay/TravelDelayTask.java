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
package jp.co.hybitz.simpletransit.traveldelay;

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.traveldelay.TravelDelaySearcher;
import jp.co.hybitz.traveldelay.TravelDelaySearcherFactory;
import jp.co.hybitz.traveldelay.model.TravelDelayQuery;
import jp.co.hybitz.traveldelay.model.TravelDelayResult;
import android.app.Activity;
import android.content.Intent;

public class TravelDelayTask extends WebSearchTask<TravelDelayQuery, TravelDelayResult> implements SimpleTransitConst {

    public TravelDelayTask(Activity activity) {
        super(activity);
    }

    @Override
    protected TravelDelayResult search(TravelDelayQuery in) throws HttpSearchException {
        TravelDelaySearcher searcher = TravelDelaySearcherFactory.createSearcher(Platform.LOOSE_HTML);
        TravelDelayResult result = searcher.search(in);

        if (isCancelled()) {
            return null;
        }
        
        if (result.isOK()) {
            Intent intent = new Intent(getActivity(), TravelDelayListActivity.class);
            intent.putExtra(EXTRA_KEY_TRAVEL_DELAY_RESULT, result);
            getActivity().startActivity(intent);
        }

        return null;
    }

    @Override
    protected void updateView(TravelDelayResult out) {
    }
}
