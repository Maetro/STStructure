/**
 * ParseadorFicherosEntrada.java 02-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.controlador.limpieza.parser;

public interface ParseadorFicherosEntrada {

    /**
     * Parsear linea entrada.
     * 
     * @param lineaTexto
     *            linea texto
     * @return the linea entrada parseada
     */
    LineaEntradaParseada parsearLineaEntrada(String lineaTexto);

}
