package jp.co.hybitz.simpletransit;

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.common.Platform;
import jp.co.hybitz.googletransit.TransitSearcher;
import jp.co.hybitz.googletransit.TransitSearcherFactory;
import jp.co.hybitz.googletransit.model.TransitQuery;
import jp.co.hybitz.googletransit.model.TransitResult;
import jp.co.hybitz.simpletransit.action.MaybeListener;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import android.view.View;
import android.widget.Button;

class TransitSearchTask extends WebSearchTask<TransitQuery, TransitResult> implements SimpleTransitConst {
    private TransitResult result;
    private SimpleTransit st;
    private boolean isNew;

    public TransitSearchTask(SimpleTransit activity, boolean isNew) {
        super(activity);
        st = (SimpleTransit) getActivity();
        this.isNew = isNew;
    }

    @Override
    protected TransitResult search(TransitQuery in) throws HttpSearchException {
        TransitSearcher searcher = TransitSearcherFactory.createSearcher(Platform.ANDROID);
        return searcher.search(in);
    }

    @Override
    protected void updateView(TransitResult out) {
        result = out;
        
        st.hideInputMethod();
        
        if (out.isOK()) {
            // 検索結果を表示
            new ResultRenderer(st).render(out);
            
            // 前の時刻と次の時刻を取得
            st.updatePreviousTimeAndNextTime(out);
            
            // もしかしてを更新
            updateMaybe(out);
            
            if (isNew && result.getTransitCount() > 0) {
                st.saveHistory();
            }
        }
        else {
            showResponseCode(out.getResponseCode());
        }
        
    }
    
    private void showResponseCode(int responseCode) {
        DialogUtils.showMessage(st, "連絡", "Googleの応答が「" + responseCode + "」でした。。", "しかたないね");
    }

    private void updateMaybe(TransitResult result) {
        Button maybe = (Button) st.findViewById(R.id.maybe);

        if (Preferences.isUseMaybe(st) && result.getMaybe() != null) {
            maybe.setVisibility(View.VISIBLE);
            maybe.setOnClickListener(new MaybeListener(st, result.getMaybe()));
        }
        else {
            maybe.setVisibility(View.INVISIBLE);
        }
    }
    

}
