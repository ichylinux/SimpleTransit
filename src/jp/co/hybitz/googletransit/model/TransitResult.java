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
package jp.co.hybitz.googletransit.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class TransitResult implements Serializable {

    private String title;
    private List<Transit> transits = new ArrayList<Transit>();
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Transit> getTransits() {
        return transits;
    }
    
    public void addTransit(Transit transit) {
        transits.add(transit);
    }
    
    public int getTransitCount() {
        return transits.size();
    }
}
