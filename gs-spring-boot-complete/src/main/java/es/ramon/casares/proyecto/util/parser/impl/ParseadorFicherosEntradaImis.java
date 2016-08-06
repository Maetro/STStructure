/**
 * ParseadorFicherosEntradaImis.java 03-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util.parser.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.ramon.casares.proyecto.util.parser.LineaEntradaParseada;
import es.ramon.casares.proyecto.util.parser.ParseadorFicherosEntrada;

public class ParseadorFicherosEntradaImis implements ParseadorFicherosEntrada {

    private Date fechaInicio;

    private final Integer secondsBetweenInstants;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Instancia un nuevo parseador ficheros entrada imis.
     * 
     * @param secondsBetweenInstants
     *            seconds between instants
     */
    public ParseadorFicherosEntradaImis(final Integer secondsBetweenInstants) {
        super();
        this.secondsBetweenInstants = secondsBetweenInstants;
    }

    @Override
    // En imis las lineas de texto tienen el siguiente formato.
    // 2009-07-01 00:00:00,26.0949066666667,35.9973733333333,133,3,1,0,1,7718769,Port of Heraklion
    public LineaEntradaParseada parsearLineaEntrada(final String lineaTexto) {

        final LineaEntradaParseada respuesta = new LineaEntradaParseada();
        final String[] parametros = lineaTexto.split(",");
        obtenerInstanteLinea(respuesta, parametros);
        final Double longitud = Double.valueOf(parametros[1]);
        final Double latitud = Double.valueOf(parametros[2]);
        final Integer idObjeto = Integer.valueOf(parametros[3]);
        respuesta.setPosX(longitud);
        respuesta.setPosY(latitud);
        respuesta.setIdObjeto(idObjeto);
        return respuesta;

    }

    /**
     * Obtener instante linea.
     * 
     * @param respuesta
     *            respuesta
     * @param parametros
     *            parametros
     */
    private void obtenerInstanteLinea(final LineaEntradaParseada respuesta, final String[] parametros) {
        try {
            if (this.fechaInicio == null) {
                // Siempre se presupone que el fichero de entrada esta ordenado por fecha, el primer registro es el mas
                // antiguo

                final Date date = this.formatter.parse(parametros[0]);
                this.fechaInicio = date;
                respuesta.setInstante(0);

            } else {

                final Date fechaActual = this.formatter.parse(parametros[0]);
                final long seconds = (fechaActual.getTime() - this.fechaInicio.getTime()) / 1000;
                respuesta.setInstante((int) seconds / this.secondsBetweenInstants);

            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
    }
}
