package com.example.natour21;

import com.example.natour21.exceptions.InvalidGeoPointStringFormatException;
import com.example.natour21.map.MapConverter;

import org.junit.Assert;
import org.junit.Test;
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayInputStream;
import java.util.List;

public class MapConverterTest {

    @Test
    public void testConversioneDiInputStreamRappresentanteTestoFormattatoCorrettamenteAListaDiGeoPoint(){
        String testo = "41.890513698459486$12.492230900227057#"
                +"48.85844772540616$2.294481298309358#";
        GeoPoint geoPoint1 = new GeoPoint(41.890513698459486, 12.492230900227057);
        GeoPoint geoPoint2 = new GeoPoint(48.85844772540616, 2.294481298309358);
        try{
            List<GeoPoint> output = MapConverter.byteArrayInputStreamToGeoPoint(new ByteArrayInputStream
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
        MapConverter.byteArrayInputStreamToGeoPoint(new ByteArrayInputStream
                (testo.getBytes(MapConverter.getCHARSET())));
        Assert.fail("Convertito senza errore");
    }



}
