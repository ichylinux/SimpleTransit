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
import jp.co.hybitz.common.GeoLocation;
import jp.co.hybitz.common.HttpSearchException;
import jp.co.hybitz.rgeocode.RGeocodeSearcherFactory;
import jp.co.hybitz.rgeocode.model.RGeocodeQuery;
import jp.co.hybitz.rgeocode.model.RGeocodeResult;
import jp.co.hybitz.simpletransit.Preferences;
import jp.co.hybitz.simpletransit.R;
import jp.co.hybitz.simpletransit.SimpleTransit;
import jp.co.hybitz.simpletransit.SimpleTransitConst;
import jp.co.hybitz.simpletransit.util.DialogUtils;
import jp.co.hybitz.stationapi.StationApiSearcherFactory;
import jp.co.hybitz.stationapi.model.StationApiQuery;
import jp.co.hybitz.stationapi.model.StationApiResult;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Criteria;
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
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            DialogUtils.showMessage(getActivity(), Preferences.getText(getActivity(), "GPS機能を利用できません。"));
            cancel(true);
            return;
        }
        
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            askReuse();
        }
        else {
            startGps();
        }
        
    }
    
    private void startGps() {
        showProgressDialog();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) {
            provider = LocationManager.GPS_PROVIDER;
        }
        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }
    
    private void askReuse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Preferences.getText(getActivity(), "前回のGPS情報を利用しますか？"));
        builder.setCancelable(false);
        builder.setPositiveButton("はい", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                gpsFinished = true;
            }
        });
        builder.setNegativeButton("いいえ", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startGps();
            }
        });
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected StationApiResult search(Void in) throws HttpSearchException {
        while (!gpsFinished) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        if (location != null) {
            GeoLocation gl = new GeoLocation();
            gl.setLatitude(location.getLatitude());
            gl.setLongitude(location.getLongitude());

            StationApiQuery query = new StationApiQuery();
            query.setGeoLocation(gl);
            StationApiResult result = StationApiSearcherFactory.createSearcher().search(query);
            
            if (result.isOK()) {
                try {
                    RGeocodeQuery rquery = new RGeocodeQuery();
                    rquery.setGeoLocation(gl);
                    RGeocodeResult rresult = RGeocodeSearcherFactory.createSearcher().search(rquery);
                    if (result.isOK()) {
                        result.setRGeocodeResult(rresult);
                    }
                }
                catch (HttpSearchException e) {
                }
                
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
                st.hideInputMethod();
                TextView summary = (TextView) st.findViewById(R.id.tv_summary);
                if (result.getRGeocodeResult() != null) {
                    summary.setText(result.getRGeocodeResult().toString() + "付近の最寄駅");
                }
                else {
                    summary.setText("最寄駅");
                }
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
