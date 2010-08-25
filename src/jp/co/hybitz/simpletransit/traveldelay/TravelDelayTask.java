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
    protected TravelDelayResult search(TravelDelayQuery query) throws HttpSearchException {
        TravelDelaySearcher searcher = TravelDelaySearcherFactory.createSearcher(Platform.LOOSE_HTML);
        TravelDelayResult result = searcher.search(query);

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
