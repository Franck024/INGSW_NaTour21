package com.example.natour21;

import com.example.natour21.exceptions.InvalidGeoPointStringFormatException;
import com.example.natour21.map.MapConverter;

import org.junit.Assert;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MapConverterTest {

    @Test
    public void testConversioneDiInputStreamRappresentanteTestoFormattatoCorrettamenteAListaDiGeoPoint(){
        String testo = "41.890513698459486$12.492230900227057#"
                +"48.85844772540616$2.294481298309358#";
        GeoPoint geoPoint1 = new GeoPoint(41.890513698459486, 12.492230900227057);
        GeoPoint geoPoint2 = new GeoPoint(48.85844772540616, 2.294481298309358);
        try{
            List<GeoPoint> output = MapConverter.byteArrayInputStreamToGeoPoints(new ByteArrayInputStream
                    (testo.getBytes(MapConverter.getCHARSET())));
            if (output == null) Assert.fail("Nessun output");
            if (output.size() != 2){
                Assert.fail("Dimensione output è " + output.size() + ", deve essere 2");
            }
            if (geoPoint1.equals(output.get(0)) && geoPoint2.equals(output.get(1))) return;
            else Assert.fail("GeoPoint non uguali\nGeoPoint1:\n" + geoPoint1 +"\n"+output.get(0)
            +"\nGeoPoint2:\n" + geoPoint2 +"\n"+output.get(1));
        }
        catch (InvalidGeoPointStringFormatException igpsfe){
            Assert.fail("Testo invalido");
        }
    }

    @Test(expected = InvalidGeoPointStringFormatException.class)
    public void testConversioneDiInputStreamReappresentanteTestoNonFormattatoCorrettamenteAListaDiGeoPoint()
            throws InvalidGeoPointStringFormatException{
        String testo = "41.890513698459486$12.492230900227057#"
                +"48.85844772540616$2.294481298309358";
        MapConverter.byteArrayInputStreamToGeoPoints(new ByteArrayInputStream
                (testo.getBytes(MapConverter.getCHARSET())));
        Assert.fail("Convertito senza errore");
    }

    @Test
    public void testConversioneDiInputStreamRappresentanteTestoFormattatoCorrettamenteAListaDiListeDiGeoPoint(){
        String testo = "41.890513698459486$12.492230900227057#"
                +"48.85844772540616$2.294481298309358#%"
                +"69$70.21121233#%"
                +"50.1222212$78.1912921#"
                +"69.0$21#"
                +"41.32392$15.01212#%";
        ByteArrayInputStream input = new ByteArrayInputStream(testo.getBytes(MapConverter.getCHARSET()));
        GeoPoint geoPoint1Track1 = new GeoPoint(41.890513698459486, 12.492230900227057);
        GeoPoint geoPoint2Track1 = new GeoPoint(48.85844772540616, 2.294481298309358);
        GeoPoint geoPoint1Track2 = new GeoPoint(69, 70.21121233);
        GeoPoint geoPoint1Track3 = new GeoPoint(50.1222212, 78.1912921);
        GeoPoint geoPoint2Track3 = new GeoPoint(69.0, 21);
        GeoPoint geoPoint3Track3 = new GeoPoint(41.32392, 15.01212);
        try{
            List<List<GeoPoint>> output = MapConverter.byteArrayInputStreamToGeoPointLists(input);
            if (output.size() != 3) Assert.fail("Dimensione output è " + output.size() + ", deve essere 3");
            List<GeoPoint> geoPointList = output.get(0);
            if (!(geoPointList.get(0).equals(geoPoint1Track1)) ||
            !(geoPointList.get(1).equals(geoPoint2Track1))) Assert.fail("Mismatch punti 1");
            geoPointList = output.get(1);
            if (!(geoPointList.get(0).equals(geoPoint1Track2))) Assert.fail("Mismatch punti 2");
            geoPointList = output.get(2);
            if (!(geoPointList.get(0).equals(geoPoint1Track3)) ||
                    !(geoPointList.get(1).equals(geoPoint2Track3)) ||
                    !(geoPointList.get(2).equals(geoPoint3Track3)))
                Assert.fail("Mismatch punti 3");
        }
        catch (InvalidGeoPointStringFormatException igpsfe){
            Assert.fail("Testo invalido");
        }
    }



}
