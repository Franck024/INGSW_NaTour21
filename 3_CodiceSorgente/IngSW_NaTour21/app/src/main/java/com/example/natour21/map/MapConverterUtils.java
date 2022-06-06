package com.example.natour21.map;

import android.util.Log;

import com.example.natour21.exceptions.InvalidGeoPointStringFormatException;

import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
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

public class MapConverterUtils {
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
        input.reset();
        return output;
    }

    public static GeoPoint getPuntoInizialeFromByteArrayInputStream
            (ByteArrayInputStream input) throws InvalidGeoPointStringFormatException{
        byte[] byteArray = new byte[input.available()];
        if (byteArray.length < 5) throw new InvalidGeoPointStringFormatException();
        input.read(byteArray, 0, byteArray.length);
        String string = new String(byteArray, CHARSET);
        int index = string.indexOf((LATITUDE_SEPARATOR).toCharArray()[0]);
        double[] values = new double[2];
        if (index == -1) throw new InvalidGeoPointStringFormatException();
        values[0] = Double.valueOf(string.substring(0, index));
        string = string.substring(index+1);
        index = string.indexOf((LONGITUDE_SEPARATOR).toCharArray()[0]);
        if (index == -1) throw new InvalidGeoPointStringFormatException();
        values[1] = Double.valueOf(string.substring(0, index));
        input.reset();
        return new GeoPoint(values[0], values[1]);
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
        input.reset();
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
            geoPoints = new LinkedList<GeoPoint>();
            for (TrackSegment s: segments){
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

    public static List<GeoPoint> geoPointListEPSG3857ToGeoPointListEPSG4326(List<GeoPoint> geoPointsEPSG3857){
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem srcCrs = crsFactory.createFromName("EPSG:3857");
        CoordinateReferenceSystem dstCrs = crsFactory.createFromName("EPSG:4326");

        BasicCoordinateTransform transform = new BasicCoordinateTransform(srcCrs, dstCrs);

        List<GeoPoint> output = new LinkedList<>();
        for (GeoPoint gp: geoPointsEPSG3857){
            ProjCoordinate srcCoord = new ProjCoordinate(gp.getLongitude(), gp.getLatitude());
            ProjCoordinate dstCoord = new ProjCoordinate();
            transform.transform(srcCoord, dstCoord);
            output.add(new GeoPoint(dstCoord.y, dstCoord.x));
        }
        return output;
    }

    public static GeoPoint geoPointEPSG4326ToEPSG3857(GeoPoint geoPointEPSG4326){
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem dstCrs = crsFactory.createFromName("EPSG:3857");
        CoordinateReferenceSystem srcCrs = crsFactory.createFromName("EPSG:4326");
        ProjCoordinate srcCoord = new ProjCoordinate(geoPointEPSG4326.getLongitude(), geoPointEPSG4326.getLatitude());
        ProjCoordinate dstCoord = new ProjCoordinate();
        new BasicCoordinateTransform(srcCrs, dstCrs).transform(srcCoord, dstCoord);
        return new GeoPoint(dstCoord.y, dstCoord.x);
    }
}
