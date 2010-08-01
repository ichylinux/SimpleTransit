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
package jp.co.hybitz.simpletransit.model;

import java.io.Serializable;
import java.util.Date;

import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.TransitQuery;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransitQuery implements Serializable {
    private long id;
    private int useCount;
    private boolean favorite;
    private long createdAt;
    private long updatedAt;
    private TransitQuery transitQuery;
    
    public SimpleTransitQuery() {
        this(new TransitQuery());
    }

    public SimpleTransitQuery(TransitQuery transitQuery) {
        this.transitQuery = transitQuery;
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TransitQuery getTransitQuery() {
        return transitQuery;
    }

    public Date getDate() {
        return transitQuery.getDate();
    }

    public String getFrom() {
        return transitQuery.getFrom();
    }

    public TimeType getTimeType() {
        return transitQuery.getTimeType();
    }

    public String getTo() {
        return transitQuery.getTo();
    }

    public boolean isUseAirline() {
        return transitQuery.isUseAirline();
    }

    public boolean isUseExpress() {
        return transitQuery.isUseExpress();
    }

    public void setDate(Date date) {
        transitQuery.setDate(date);
    }

    public void setFrom(String from) {
        transitQuery.setFrom(from);
    }

    public void setTimeType(TimeType timeType) {
        transitQuery.setTimeType(timeType);
    }

    public void setTo(String to) {
        transitQuery.setTo(to);
    }

    public void setUseAirline(boolean useAirline) {
        transitQuery.setUseAirline(useAirline);
    }

    public void setUseExpress(boolean useExpress) {
        transitQuery.setUseExpress(useExpress);
    }
    
    public String getFromTo() {
        return getFrom() + " ～ " + getTo();
    }

}
