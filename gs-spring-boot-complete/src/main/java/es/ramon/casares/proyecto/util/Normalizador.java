/**
 * Normalizador.java 03-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Normalizador {

    private final int metrosLadoCelda;
    private static final String FILE_DATA = "./imis1day";
    private static RandomAccessFile datareader;

    private final int mayorLat; // LAt * 1000
    private final int menorLat; // LAt * 1000
    private final int mayorLong; // LAt * 1000
    private final int menorLong; // LAt * 1000

    /**
     * Instancia un nuevo normalizador.
     * 
     * @param menorLat
     *            menor lat
     * @param mayorLat
     *            mayor lat
     * @param menorLong
     *            menor long
     * @param mayorLong
     *            mayor long
     * @param metrosLadoCelda
     *            metros lado celda
     */
    public Normalizador(final int menorLat, final int mayorLat,
            final int menorLong, final int mayorLong,
            final int metrosLadoCelda) {
        this.mayorLat = mayorLat;
        this.menorLat = menorLat;
        this.menorLong = menorLong;
        this.mayorLong = mayorLong;
        this.metrosLadoCelda = metrosLadoCelda;
    }

    private double distanciaLongitudMetros = 0;
    private double distanciaLatitudMetros = 0;

    private int numeroCuadradosX = 0;
    private int numeroCuadradosY = 0;

    private Date firstInstant;

    public void run() throws ParseException {

        try {
            datareader = new RandomAccessFile(FILE_DATA, "r");
            escribirResultados();
            datareader.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    // "t,lon,lat,obj_id,flag,subtraj_id,subtraj_type,traj_id,port_id,port_title"
    // 2009-07-01 00:00:00,26.0949066666667,35.9973733333333,133,3,1,0,1,7718769,Port of Heraklion
    private void escribirResultados() throws IOException, ParseException {
        String currentLine;
        final int linea = 0;

        this.distanciaLongitudMetros = (distance(34.6, 19, 34.6, 30) + distance(42, 19, 42, 30)) / 2;
        // distanciaLongitudMetros = distance(34.6,19,34.6,30);
        this.numeroCuadradosX = (int) Math.floor(this.distanciaLongitudMetros / this.metrosLadoCelda) + 1;
        this.distanciaLatitudMetros = (distance(34.6, 19, 42, 19) + distance(34.6, 30, 42, 30)) / 2;
        // distanciaLatitudMetros = distance(34.6,19,42,19);
        this.numeroCuadradosY = (int) Math.floor(this.distanciaLatitudMetros / this.metrosLadoCelda) + 1;
        final File tempFile = new File("./imis1day.norm10000m2");
        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        // La primera línea no nos vale
        currentLine = datareader.readLine();
        currentLine = datareader.readLine();
        String[] result = currentLine.trim().split(",");
        final String fecha = result[0];
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = format.parse(fecha);
        this.firstInstant = date;
        long seconds = (date.getTime() - this.firstInstant.getTime()) / 1000;
        writer.write(seconds + " " + result[3] + " " + calcularYnormalizado(result[2]) + " " +
                calcularXnormalizado(result[1]) + System.getProperty("line.separator"));
        while ((currentLine = datareader.readLine()) != null) {

            result = currentLine.trim().split(",");
            date = format.parse(result[0]);
            seconds = (date.getTime() - this.firstInstant.getTime()) / 1000;
            System.out.println(seconds);
            writer.write(seconds + " " + result[3] + " " + calcularYnormalizado(result[2]) + " " +
                    calcularXnormalizado(result[1]) + System.getProperty("line.separator"));

        }

        writer.close();

    }

    private int calcularXnormalizado(final String longitud) {
        final double longitude = Double.valueOf(longitud);

        return (int) Math.floor((((longitude * 1000) - this.menorLong) * this.numeroCuadradosX)
                / (this.mayorLong - this.menorLong));

    }

    private int calcularYnormalizado(final String latitud) {
        final double latitude = Double.valueOf(latitud);

        return (int) Math.floor((((latitude * 1000) - this.menorLat) * this.numeroCuadradosY)
                / (this.mayorLat - this.menorLat));

    }

    /* Devuelve la distancia en m */
    private double distance(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double theta = lon1 - lon2;
        double dist = (Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)))
                + (Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta)));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;
        return dist;
    }

    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    /* :: This function converts decimal degrees to radians : */
    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    private double deg2rad(final double deg) {
        return ((deg * Math.PI) / 180.0);
    }

    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    /* :: This function converts radians to decimal degrees : */
    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    private double rad2deg(final double rad) {
        return ((rad * 180) / Math.PI);
    }

}
