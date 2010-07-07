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
package jp.co.hybitz.simpletransit.alarm.model;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class AlarmSoundItem {
    private int id;
    private String artist;
    private String title;
    
    public AlarmSoundItem(int id, String artist, String title) {
        this.id = id;
        this.title = title;
    }
    
    public int getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }
    
    public String getTitle() {
        return title;
    }

    /**
     * TODO アーティスト名の表示
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return (artist != null ? artist + " - " : "") + title;
    }
}
