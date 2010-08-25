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
package jp.co.hybitz.simpletransit.station;

import jp.co.hybitz.android.WebSearchTask;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransit;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import jp.co.hybitz.stationapi.StationApiSearcherFactory;
import jp.co.hybitz.stationapi.model.StationApiQuery;
import jp.co.hybitz.stationapi.model.StationApiResult;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class SearchNearStationsTask extends WebSearchTask<Void, StationApiResult> implements SimpleTransitConst, LocationListener {
    private LocationManager locationManager;
    private boolean gpsFinished;
    private Location location;

    public SearchNearStationsTask(SimpleTransit activity) {
        super(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected StationApiResult search(Void in) throws HttpSearchException {
        while (!gpsFinished) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        if (location != null) {
            StationApiQuery query = new StationApiQuery();
            query.setLatitude(location.getLatitude());
            query.setLongitude(location.getLongitude());
            StationApiResult result = StationApiSearcherFactory.createSearcher().search(query);
            
            if (result.isOK()) {
                return result;
            }
        }

        return null;
    }

    @Override
    protected void updateView(StationApiResult result) {
        if (isCancelled()) {
            return;
        }
        
        if (result != null) {
            if (result.getStations().isEmpty()) {
                DialogUtils.showMessage(getActivity(), Preferences.getText(getActivity(), "最寄駅が見つかりませんでした。"));
            }
            else {
                SimpleTransit st = (SimpleTransit) getActivity();
                TextView summary = (TextView) st.findViewById(R.id.tv_summary);
                summary.setText("最寄駅");
                st.removeFavoriteList();
                ListView results = (ListView) st.findViewById(R.id.results);
                results.setAdapter(new StationArrayAdapter(st, result.getStations()));
            }
        }
    }
    
    @Override
    protected void onCancelled() {
        stopGps();
    }

    private void stopGps() {
        locationManager.removeUpdates(this);
        gpsFinished = true;
    }

    public void onLocationChanged(Location location) {
        this.location = location;
        stopGps();
    }

    public void onProviderDisabled(String provider) {
        stopGps();
    }

    public void onProviderEnabled(String provider) {
        stopGps();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        stopGps();
    }
}
