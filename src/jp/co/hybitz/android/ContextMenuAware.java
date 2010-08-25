package jp.co.hybitz.android;

import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

public interface ContextMenuAware {

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo);
}
