package jp.co.hybitz.simpletransit.common;

import jp.co.hybitz.simpletransit.action.OptionMenuHandler;
import android.app.Activity;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {
    private OptionMenuHandler optionMenuHandler = new OptionMenuHandler(this);

    /**
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (optionMenuHandler.onMenuItemSelected(featureId, item)) {
            return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    
}
