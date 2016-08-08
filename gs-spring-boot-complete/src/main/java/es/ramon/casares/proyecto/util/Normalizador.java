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

    private final Double mayorLat; // LAt * 1000
    private final Double menorLat; // LAt * 1000
    private final Double mayorLong; // LAt * 1000
    private final Double menorLong; // LAt * 1000

    private final int numeroCuadrados; // LAt * 1000

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
    public Normalizador(final Double menorLat, final Double mayorLat,
            final Double menorLong, final Double mayorLong,
            final int numeroCuadrados) {
        this.mayorLat = mayorLat;
        this.menorLat = menorLat;
        this.menorLong = menorLong;
        this.mayorLong = mayorLong;
        this.numeroCuadrados = numeroCuadrados;
    }


  public int calcularXnormalizado(final Double longitud) {
      
        return (int) Math.floor(((longitud - this.menorLong) * this.numeroCuadrados)
                / (this.mayorLong - this.menorLong));

    }

  public int calcularYnormalizado(final Double latitud) {
 
        return (int) Math.floor(((latitud  - this.menorLat) * this.numeroCuadrados)
                / (this.mayorLat - this.menorLat));

    }




}
