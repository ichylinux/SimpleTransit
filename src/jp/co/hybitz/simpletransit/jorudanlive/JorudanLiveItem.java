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

import java.io.Serializable;

import jp.co.hybitz.jorudanlive.model.LiveInfo;

class JorudanLiveItem implements Serializable {
    private LiveInfo liveInfo;

    public JorudanLiveItem(LiveInfo liveInfo) {
        this.liveInfo = liveInfo;
    }
    
    public LiveInfo getLiveInfo() {
        return liveInfo;
    }
    
    public String getTitle() {
        return liveInfo.getTime() + "　" + liveInfo.getLine();
    }
    
    public String getSummary() {
        return liveInfo.getSummary();
    }
    
    public String getDetail() {
        return liveInfo.getDetail();
    }
}
