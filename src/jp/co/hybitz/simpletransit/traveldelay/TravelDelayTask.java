package jp.co.hybitz.simpletransit.traveldelay;

import java.net.HttpURLConnection;

import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.simpletransit.ExceptionHandler;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.traveldelay.TravelDelaySearcher;
import jp.co.hybitz.traveldelay.TravelDelaySearcherFactory;
import jp.co.hybitz.traveldelay.model.TravelDelayQuery;
import jp.co.hybitz.traveldelay.model.TravelDelayResult;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

public class TravelDelayTask extends AsyncTask<TravelDelayQuery, Integer, Boolean> implements SimpleTransitConst {
    private Activity activity;
    private ExceptionHandler exceptionHandler;
    private ProgressDialog dialog;
    private HttpSearchException e;
    private boolean canceled;

    public TravelDelayTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        exceptionHandler = new ExceptionHandler(activity); 

        dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setTitle(Preferences.getText(activity, "通信中"));
        dialog.setMessage(Preferences.getText(activity, "しばらくお待ち下さい"));
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(TravelDelayQuery... query) {
        try {
            TravelDelaySearcher searcher = TravelDelaySearcherFactory.createSearcher(Platform.LOOSE_HTML);
            TravelDelayResult result = searcher.search(query[0]);

            if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
                if (!canceled) {
                    Intent intent = new Intent(activity, TravelDelayListActivity.class);
                    intent.putExtra(EXTRA_KEY_TRAVEL_DELAY_RESULT, result);
                    activity.startActivity(intent);
                }
            }

            return true;

        } catch (HttpSearchException e) {
            this.e = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
        
        if (!result) {
            exceptionHandler.handleException(e);
        }
    }

    @Override
    protected void onCancelled() {
        canceled = true;
    }
}
