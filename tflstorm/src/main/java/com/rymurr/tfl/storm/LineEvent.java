package com.rymurr.tfl.storm;

import org.joda.time.DateTime;

import java.io.Serializable;

public class LineEvent implements Cloneable, Serializable {
    private DateTime dateCreated;
    private String line;
    private String lineName;
    private String station;
    private String stationName;
    private String stationMessage;
    private String platformName;
    private int platformNumber;
    private String platformTrackCode;
    private boolean platformNextTrain;
    private String lcid;
    private int setNumber;
    private int tripNumber;
    private int secondsTo;
    private String timeTo;
    private String location;
    private String destination;
    private String destinationCode;
    private int order;
    private String departTime;
    private int departInterval;
    private boolean departed;
    private String direction;
    private boolean isStalled;
    private String trainTrackCode;
    private String trainPlatformCode;

    public String getLcid() {
        return lcid;
    }

    public void setLcid(String lcid) {
        this.lcid = lcid;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(int tripNumber) {
        this.tripNumber = tripNumber;
    }

    public int getSecondsTo() {
        return secondsTo;
    }

    public void setSecondsTo(int secondsTo) {
        this.secondsTo = secondsTo;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public int getDepartInterval() {
        return departInterval;
    }

    public void setDepartInterval(int departInterval) {
        this.departInterval = departInterval;
    }

    public boolean getDeparted() {
        return departed;
    }

    public void setDeparted(boolean departed) {
        this.departed = departed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean getIsStalled() {
        return isStalled;
    }

    public void setIsStalled(boolean isStalled) {
        this.isStalled = isStalled;
    }

    public String getTrainTrackCode() {
        return trainTrackCode;
    }

    public void setTrainTrackCode(String trainTrackCode) {
        this.trainTrackCode = trainTrackCode;
    }

    public String getTrainPlatformCode() {
        return trainPlatformCode;
    }

    public void setTrainPlatformCode(String trainPlatformCode) {
        this.trainPlatformCode = trainPlatformCode;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public int getPlatformNumber() {
        return platformNumber;
    }

    public void setPlatformNumber(int platformNumber) {
        this.platformNumber = platformNumber;
    }

    public String getPlatformTrackCode() {
        return platformTrackCode;
    }

    public void setPlatformTrackCode(String platformTrackCode) {
        this.platformTrackCode = platformTrackCode;
    }

    public boolean isPlatformNextTrain() {
        return platformNextTrain;
    }

    public void setPlatformNextTrain(boolean platformNextTrain) {
        this.platformNextTrain = platformNextTrain;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationMessage() {
        return stationMessage;
    }

    public void setStationMessage(String stationMessage) {
        this.stationMessage = stationMessage;
    }

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public LineEvent clone() throws CloneNotSupportedException {
        return (LineEvent)super.clone();
    }

    @Override
    public String toString() {
        return "LineEvent{" +
                "dateCreated=" + dateCreated +
                ", line='" + line + '\'' +
                ", lineName='" + lineName + '\'' +
                ", station='" + station + '\'' +
                ", stationName='" + stationName + '\'' +
                ", stationMessage='" + stationMessage + '\'' +
                ", platformName='" + platformName + '\'' +
                ", platformNumber=" + platformNumber +
                ", platformTrackCode='" + platformTrackCode + '\'' +
                ", platformNextTrain=" + platformNextTrain +
                ", lcid='" + lcid + '\'' +
                ", setNumber='" + setNumber + '\'' +
                ", tripNumber='" + tripNumber + '\'' +
                ", secondsTo=" + secondsTo +
                ", timeTo='" + timeTo + '\'' +
                ", location='" + location + '\'' +
                ", destination='" + destination + '\'' +
                ", destinationCode='" + destinationCode + '\'' +
                ", order='" + order + '\'' +
                ", departTime='" + departTime + '\'' +
                ", departInterval='" + departInterval + '\'' +
                ", departed='" + departed + '\'' +
                ", direction='" + direction + '\'' +
                ", isStalled=" + isStalled +
                ", trainTrackCode='" + trainTrackCode + '\'' +
                ", trainPlatformCode='" + trainPlatformCode + '\'' +
                '}';
    }
}
