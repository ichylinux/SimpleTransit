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
package jp.co.hybitz.simpletransit.action;

import android.graphics.Rect;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class FromToDragListener implements View.OnTouchListener {
    private boolean dragging;
    private EditText src;
    private EditText dest;

    public FromToDragListener(EditText src, EditText dest) {
        this.src = src;
        this.dest = dest;
    }
    
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            dragging = true;
        }
        else if (e.getAction() == MotionEvent.ACTION_UP) {
            if (dragging) {
                Rect r = new Rect();
                dest.getGlobalVisibleRect(r);
                if (r.contains((int)e.getRawX(), (int)e.getRawY())) {
                    Editable text = src.getText();
                    src.setText(dest.getText());
                    dest.setText(text);
                }
                dragging = false;
            }
        }
        return false;
    }
}
