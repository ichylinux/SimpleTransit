package jp.co.hybitz.simpletransit.timetable.model;

import java.util.ArrayList;
import java.util.List;

import jp.co.hybitz.simpletransit.common.model.Entity;
import jp.co.hybitz.timetable.model.Line;

public class LineEx extends Entity {
    private long prefectureId;
    private Line line;
    private List<StationEx> stations = new ArrayList<StationEx>();
    
    public LineEx() {
        this(new Line());
    }
    
    public LineEx(Line line) {
        this.line = line;
    }

    public long getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(long prefectureId) {
        this.prefectureId = prefectureId;
    }

    public Line getLine() {
        return line;
    }

    public String getCompany() {
        return line.getCompany();
    }

    public String getName() {
        return line.getName();
    }

    public String getUrl() {
        return line.getUrl();
    }

    public void setCompany(String company) {
        line.setCompany(company);
    }

    public void setName(String name) {
        line.setName(name);
    }

    public void setUrl(String url) {
        line.setUrl(url);
    }

    public List<StationEx> getStations() {
        return stations;
    }

    public void addStation(StationEx station) {
        stations.add(station);
    }
    
    public void setStations(List<StationEx> stations) {
        this.stations = stations;
    }
}
