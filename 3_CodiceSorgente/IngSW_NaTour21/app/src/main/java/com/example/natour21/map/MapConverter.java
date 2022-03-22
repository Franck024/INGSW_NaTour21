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
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;

public class MapConverter {
    private enum ReadMode{
        LATITUDE,
        LONGITUDE
    }
    final private static char LATITUDE_SEPARATOR = '$';
    final private static char LONGITUDE_SEPARATOR = '#';
    final private static Charset CHARSET = StandardCharsets.UTF_8;

    public static Charset getCHARSET() {
        return CHARSET;
    }

    //Formato di un punto:
    //<latitudine>$<longitudine>#
    //Charset UTF-8
    public static InputStream geoPointsToInputStream(List<GeoPoint> geoPointList){

        String outputString = "";
        for (GeoPoint gp: geoPointList){
            outputString += gp.getLatitude()+
                    +gp.getLongitude()+LONGITUDE_SEPARATOR;
        }
        return new ByteArrayInputStream(outputString.getBytes(CHARSET));
    }

    public static List<GeoPoint> inputStreamToGeoPoint(ByteArrayInputStream input) throws
            IOException, InvalidGeoPointStringFormatException{
        byte[] byteArray = new byte[input.available()];
        if (byteArray.length < 4) throw new InvalidGeoPointStringFormatException();
        input.read(byteArray);
        String string = new String(byteArray, CHARSET);
        double[] values = new double[2];
        int index;
        ReadMode readMode = ReadMode.LATITUDE;
        List<GeoPoint> output = new LinkedList<GeoPoint>();
        char discriminator;
        int fieldsCounter = 0;
        while(string.length() != 0){
            if (readMode.equals(ReadMode.LATITUDE))
                discriminator = LATITUDE_SEPARATOR;
            else
                discriminator = LONGITUDE_SEPARATOR;
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

    public static List<LinkedList<GeoPoint>> gpxToGeoPoints(Gpx gpx){
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
        return output;
    }
}
