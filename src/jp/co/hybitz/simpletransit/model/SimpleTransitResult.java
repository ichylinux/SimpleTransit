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
import java.util.List;

import jp.co.hybitz.googletransit.model.Time;
import jp.co.hybitz.googletransit.model.TimeType;
import jp.co.hybitz.googletransit.model.Transit;
import jp.co.hybitz.googletransit.model.TransitResult;

/**
 * @author ichy <ichylinux@gmail.com>
 */
public class SimpleTransitResult implements Serializable {

    private long id;
    private int alarmStatus;
    private long alarmAt;
    private long createdAt;
    private TransitResult transitResult;

    public SimpleTransitResult() {
        this(new TransitResult());
    }
    
    public SimpleTransitResult(TransitResult transitResult) {
        this.transitResult = transitResult;
    }
    
    public TransitResult getTransitResult() {
        return transitResult;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public long getAlarmAt() {
        return alarmAt;
    }

    public void setAlarmAt(long alarmAt) {
        this.alarmAt = alarmAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void addTransit(Transit transit) {
        transitResult.addTransit(transit);
    }

    public String getFrom() {
        return transitResult.getFrom();
    }

    public String getPrefecture() {
        return transitResult.getPrefecture();
    }

    public int getResponseCode() {
        return transitResult.getResponseCode();
    }

    public Date getQueryDate() {
        return transitResult.getQueryDate();
    }

    public void setQueryDate(Date queryDate) {
        transitResult.setQueryDate(queryDate);
    }

    public Time getTime() {
        return transitResult.getTime();
    }

    public TimeType getTimeType() {
        return transitResult.getTimeType();
    }

    public String getTo() {
        return transitResult.getTo();
    }

    public int getTransitCount() {
        return transitResult.getTransitCount();
    }

    public List<Transit> getTransits() {
        return transitResult.getTransits();
    }

    public void setFrom(String from) {
        transitResult.setFrom(from);
    }

    public void setPrefecture(String prefecture) {
        transitResult.setPrefecture(prefecture);
    }

    public void setResponseCode(int responseCode) {
        transitResult.setResponseCode(responseCode);
    }

    public void setTime(Time time) {
        transitResult.setTime(time);
    }

    public void setTimeType(TimeType timeType) {
        transitResult.setTimeType(timeType);
    }

    public void setTo(String to) {
        transitResult.setTo(to);
    }

    public void setTransits(List<Transit> transits) {
        transitResult.setTransits(transits);
    }

    public String toString() {
        return transitResult.toString();
    }
    
}
