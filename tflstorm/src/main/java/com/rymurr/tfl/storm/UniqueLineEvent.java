package com.rymurr.tfl.storm;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

import java.io.Serializable;

public class UniqueLineEvent implements Serializable {
    private static final Joiner JOINER = Joiner.on(":");
    private final String stationKey;
    private final String trainKey;
    private final int secondsToStation;
    private final String destination;
    private final String location;
    private final String line;
    private final DateTime createTime;

    public UniqueLineEvent(String station, int platform, String lcid, int setNumber, int tripNumber, int secondsToStation, String destination, String location, String line, DateTime createTime) {
        this.destination = destination;
        this.location = location;
        this.line = line;
        this.createTime = createTime;
        this.stationKey = JOINER.join(station, platform);
        this.trainKey = JOINER.join(lcid, setNumber, tripNumber);
        this.secondsToStation = secondsToStation;
    }

    public String getStationKey() {
        return stationKey;
    }

    public String getTrainKey() {
        return trainKey;
    }

    public int getSecondsToStation() {
        return secondsToStation;
    }

    public String getDestination() {
        return destination;
    }

    public String getLocation() {
        return location;
    }

    public static UniqueLineEvent fromLineEvent(LineEvent lineEvent) {
        return new UniqueLineEvent(lineEvent.getStation(), lineEvent.getPlatformNumber(), lineEvent.getLcid(), lineEvent.getSetNumber(), lineEvent.getTripNumber(), lineEvent.getSecondsTo(), lineEvent.getDestinationCode(), lineEvent.getLocation(), lineEvent.getLine(), lineEvent.getDateCreated());
    }

    public String getLine() {
        return line;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("stationKey", stationKey)
                .add("trainKey", trainKey)
                .add("secondsToStation", secondsToStation)
                .add("destination", destination)
                .add("location", location)
                .add("line", line)
                .add("createTime", createTime)
                .toString();
    }
}
