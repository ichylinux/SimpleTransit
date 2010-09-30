package jp.co.hybitz.simpletransit;

import jp.co.hybitz.transit.model.Station;

public class StationItem {
    private boolean isFrom;
    private Station station;

    public StationItem(boolean isFrom, Station station) {
        this.isFrom = isFrom;
        this.station = station;
    }
    
    public boolean isFrom() {
        return isFrom;
    }
    
    public Station getStation() {
        return station;
    }
}
