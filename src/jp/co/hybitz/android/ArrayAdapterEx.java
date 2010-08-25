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
package jp.co.hybitz.android;

import java.util.List;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public abstract class ArrayAdapterEx<T> extends ArrayAdapter<T> implements ContextMenuAware {

    private LayoutInflater inflater;
    private int textViewResourceId;
    private List<T> items;

    public ArrayAdapterEx(Context context, int textViewResourceId, List<T> items) {
        super(context, textViewResourceId, items);
        this.textViewResourceId = textViewResourceId;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public List<T> getItems() {
        return items;
    }
    
    /**
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(textViewResourceId, null);
        }

        updateView(view, getItem(position));

        return view;
    }
    
    /**
     * @see jp.co.hybitz.android.ContextMenuAware#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    }
    
    protected abstract void updateView(View view, T item);
}
