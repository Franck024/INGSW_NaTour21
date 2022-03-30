package com.example.natour21.map;

import com.example.natour21.exceptions.InvalidGeoPointStringFormatException;

import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Route;
import io.ticofab.androidgpxparser.parser.domain.RoutePoint;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;
import io.ticofab.androidgpxparser.parser.domain.WayPoint;

public class MapConverter {
    private enum ReadMode{
        LATITUDE,
        LONGITUDE
    }
    final private static String LATITUDE_SEPARATOR = "$";
    final private static String LONGITUDE_SEPARATOR = "#";
    final private static String TRACK_END_SEPARATOR = "%";
    final private static Charset CHARSET = StandardCharsets.UTF_8;

    public static Charset getCHARSET() {
        return CHARSET;
    }

    //Formato di un punto:
    //<latitudine>$<longitudine>#
    //Charset UTF-8
    public static ByteArrayInputStream geoPointsToByteArrayInputStream(List<GeoPoint> geoPointList){
        if (geoPointList == null) return null;
        String outputString = "";
        for (GeoPoint gp: geoPointList){
            outputString += gp.getLatitude()+LATITUDE_SEPARATOR
                    +gp.getLongitude()+LONGITUDE_SEPARATOR;
        }
        outputString += TRACK_END_SEPARATOR;
        return new ByteArrayInputStream(outputString.getBytes(CHARSET));
    }

    public static String geoPointsToString(List<GeoPoint> geoPointList){
        if (geoPointList == null) return null;
        String outputString = "";
        for (GeoPoint gp: geoPointList){
            outputString += gp.getLatitude()+LATITUDE_SEPARATOR
                    +gp.getLongitude()+LONGITUDE_SEPARATOR;
        }
        outputString += TRACK_END_SEPARATOR;
        System.out.println("SUSOUTPUTSTRING: " + outputString);
        return outputString;
    }

    public static List<List<GeoPoint>> byteArrayInputStreamToGeoPointLists
            (ByteArrayInputStream input) throws InvalidGeoPointStringFormatException{
        byte[] byteArray = new byte[input.available()];
        if (byteArray.length < 5) throw new InvalidGeoPointStringFormatException();
        input.read(byteArray, 0, byteArray.length);
        String string = new String(byteArray, CHARSET);
        String bufferString;
        List<List<GeoPoint>> output = new LinkedList<List<GeoPoint>>();
        int endTrackSeparatorIndex;
        while (string.length() != 0){
            endTrackSeparatorIndex = string.indexOf(TRACK_END_SEPARATOR);
            if (endTrackSeparatorIndex == -1) throw new InvalidGeoPointStringFormatException();
            bufferString = string.substring(0, endTrackSeparatorIndex);
            output.add(geoPointStringToGeoPointList(bufferString));
            string = string.substring(endTrackSeparatorIndex+1);
        }
        return output;
    }

    private static List<GeoPoint> geoPointStringToGeoPointList(String string)
            throws InvalidGeoPointStringFormatException{
        double[] values = new double[2];
        int index;
        ReadMode readMode = ReadMode.LATITUDE;
        List<GeoPoint> output = new LinkedList<GeoPoint>();
        char discriminator;
        int fieldsCounter = 0;
        while(string.length() != 0){
            if (readMode.equals(ReadMode.LATITUDE))
                discriminator = LATITUDE_SEPARATOR.toCharArray()[0];
            else
                discriminator = LONGITUDE_SEPARATOR.toCharArray()[0];
            index = string.indexOf(discriminator);
            if (index == -1) throw new InvalidGeoPointStringFormatException();
            values[fieldsCounter] = Double.valueOf(string.substring(0, index));
            fieldsCounter++;
            string = string.substring(index+1);
            readMode = (readMode.equals(ReadMode.LATITUDE)) ?
                    ReadMode.LONGITUDE : ReadMode.LATITUDE;
            if (fieldsCounter == 2){
                fieldsCounter = 0;
                GeoPoint newGeoPoint = new GeoPoint(values[0], values[1]);
                output.add(newGeoPoint);
            }
        }
        return output;
    }

    public static List<GeoPoint> byteArrayInputStreamToGeoPoints(ByteArrayInputStream input) throws InvalidGeoPointStringFormatException{
        byte[] byteArray = new byte[input.available()];
        if (byteArray.length < 5) throw new InvalidGeoPointStringFormatException();
        input.read(byteArray, 0, byteArray.length);
        String string = new String(byteArray, CHARSET);
        double[] values = new double[2];
        int index;
        ReadMode readMode = ReadMode.LATITUDE;
        List<GeoPoint> output = new LinkedList<GeoPoint>();
        char discriminator;
        int fieldsCounter = 0;
        while(string.length() != 0){
            if (readMode.equals(ReadMode.LATITUDE))
                discriminator = LATITUDE_SEPARATOR.toCharArray()[0];
            else
                discriminator = LONGITUDE_SEPARATOR.toCharArray()[0];
            index = string.indexOf(discriminator);
            if (index == -1) throw new InvalidGeoPointStringFormatException();
            values[fieldsCounter] = Double.valueOf(string.substring(0, index));
            fieldsCounter++;
            string = string.substring(index+1);
            readMode = (readMode.equals(ReadMode.LATITUDE)) ?
                    ReadMode.LONGITUDE : ReadMode.LATITUDE;
            if (fieldsCounter == 2){
                fieldsCounter = 0;
                GeoPoint newGeoPoint = new GeoPoint(values[0], values[1]);
                output.add(newGeoPoint);
            }
        }
        return output;
    }

    private static List<LinkedList<GeoPoint>> gpxToGeoPointLists(Gpx gpx){
        List<Track> tracks = gpx.getTracks();
        List<TrackSegment> segments;
        List<TrackPoint> trackPoints;
        LinkedList<GeoPoint> geoPoints;
        LinkedList<LinkedList<GeoPoint>>  output = new LinkedList<LinkedList<GeoPoint>>();
        for (Track t : tracks){
            segments = t.getTrackSegments();
            for (TrackSegment s: segments){
                geoPoints = new LinkedList<GeoPoint>();
                trackPoints = s.getTrackPoints();
                for (TrackPoint tp : trackPoints){
                    geoPoints.add(new GeoPoint(tp.getLatitude(), tp.getLongitude()));
                }
                output.add(geoPoints);
            }
        }
        List<WayPoint> wayPoints = gpx.getWayPoints();
        for (WayPoint wp: wayPoints) {
            geoPoints = new LinkedList<GeoPoint>();
            geoPoints.add(new GeoPoint(wp.getLatitude(), wp.getLongitude()));
            output.add(geoPoints);
        }
        List<Route> routes = gpx.getRoutes();
        for (Route r : routes){
            geoPoints = new LinkedList<GeoPoint>();
            List<RoutePoint> routePoints = r.getRoutePoints();
            for (RoutePoint rp : routePoints){
                geoPoints.add(new GeoPoint(rp.getLatitude(), rp.getLongitude()));
            }
            output.add(geoPoints);
        }
        return output;
    }

    public static ByteArrayInputStream gpxToByteArrayInputStream(Gpx gpx){
        List<LinkedList<GeoPoint>> geoPointsList = gpxToGeoPointLists(gpx);
        String bufferString = "";
        for (LinkedList<GeoPoint> gl : geoPointsList){
            bufferString += geoPointsToString(gl);
        }
        return new ByteArrayInputStream((bufferString.getBytes(CHARSET)));
    }
}
