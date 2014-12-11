package com.rymurr.tfl.storm;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class XmlParser {

    private static final DateTimeFormatter format = DateTimeFormat.forPattern("dd MMM yyyy HH:mm:ss");

    public static void main(String[] args) {
        String filename = "/data/xxx.xml";
        //InputStream in = IOUtils.toInputStream(source, "UTF-8");
        try {
            InputStream is = new FileInputStream(filename);
            new XmlParser().run(is);
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    List<LineEvent> run(InputStream is) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(is);

        List<LineEvent> lineEvents = new ArrayList<>();
        LineEvent lineEvent = new LineEvent();
        while(eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("WhenCreated")) {
                    String dateStr = eventReader.nextEvent().asCharacters().getData();
                    lineEvent.setDateCreated(format.parseDateTime(dateStr));
                }

                if (startElement.getName().getLocalPart().equals("Line")) {
                    String lineStr = eventReader.nextEvent().asCharacters().getData();
                    lineEvent.setLine(lineStr);
                }

                if (startElement.getName().getLocalPart().equals("LineName")) {
                    String lineStr = eventReader.nextEvent().asCharacters().getData();
                    lineEvent.setLineName(lineStr);
                }

                if (startElement.getName().getLocalPart().equals("S")) {
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    while(attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        if (attribute.getName().toString().equals("Code")) {
                            lineEvent.setStation(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Mess")) {
                            lineEvent.setStationMessage(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("N")) {
                            lineEvent.setStationName(attribute.getValue());
                        }
                    }
                }

                if (startElement.getName().getLocalPart().equals("P")) {
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    while(attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        if (attribute.getName().toString().equals("N")) {
                            lineEvent.setPlatformName(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Num")) {
                            lineEvent.setPlatformNumber(Integer.parseInt(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("TrackCode")) {
                            lineEvent.setPlatformTrackCode(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("NextTrain")) {
                            lineEvent.setPlatformNextTrain(Boolean.parseBoolean(attribute.getValue()));
                        }
                    }
                }

                if (startElement.getName().getLocalPart().equals("T")) {
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    while(attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        if (attribute.getName().toString().equals("LCID")) {
                            lineEvent.setLcid(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("SetNo")) {
                            lineEvent.setSetNumber(Integer.parseInt(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("TripNo")) {
                            lineEvent.setTripNumber(Integer.parseInt(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("SecondsTo")) {
                            lineEvent.setSecondsTo(Integer.parseInt(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("TimeTo")) {
                            lineEvent.setTimeTo(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Location")) {
                            lineEvent.setLocation(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Destination")) {
                            lineEvent.setDestination(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("DestCode")) {
                            lineEvent.setDestinationCode(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Order")) {
                            lineEvent.setOrder(Integer.parseInt(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("DepartTime")) {
                            lineEvent.setDepartTime(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Direction")) {
                            lineEvent.setDirection(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("IsStalled")) {
                            lineEvent.setIsStalled(Boolean.parseBoolean(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("TrackCode")) {
                            lineEvent.setTrainTrackCode(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("LN")) {
                            lineEvent.setTrainPlatformCode(attribute.getValue());
                        }
                        if (attribute.getName().toString().equals("Departed")) {
                            lineEvent.setDeparted(Boolean.parseBoolean(attribute.getValue()));
                        }
                        if (attribute.getName().toString().equals("DepartInterval")) {
                            lineEvent.setDepartInterval(Integer.parseInt(attribute.getValue()));
                        }

                    }
                    try {
                        LineEvent completedLineEvent = lineEvent.clone();
                        lineEvents.add(completedLineEvent);
                    } catch (CloneNotSupportedException e) {
                    }

                }
            }
        }
        return lineEvents;
    }

}
