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
package jp.co.hybitz.simpletransit.jorudanlive;

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.common.Searcher;
import jp.co.hybitz.jorudanlive.JorudanLiveSearcherFactory;
import jp.co.hybitz.jorudanlive.model.JorudanLiveQuery;
import jp.co.hybitz.jorudanlive.model.JorudanLiveResult;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import android.app.Activity;
import android.content.Intent;

public class JorudanLiveTask extends WebSearchTask<JorudanLiveQuery, JorudanLiveResult> implements SimpleTransitConst {

    public JorudanLiveTask(Activity activity) {
        super(activity);
    }

    @Override
    protected JorudanLiveResult search(JorudanLiveQuery in) throws HttpSearchException {
        Searcher<JorudanLiveQuery, JorudanLiveResult> searcher = JorudanLiveSearcherFactory.createSearcher(Platform.ANDROID);
        JorudanLiveResult result = searcher.search(in);

        if (isCancelled()) {
            return null;
        }
        
        if (result.isOK()) {
            Intent intent = new Intent(getActivity(), JorudanLiveListActivity.class);
            intent.putExtra(EXTRA_KEY_JORUDAN_LIVE_RESULT, result);
            getActivity().startActivity(intent);
        }

        return null;
    }

    @Override
    protected void updateView(JorudanLiveResult out) {
    }
}
