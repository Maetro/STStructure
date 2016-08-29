/**
 * EstructuraComprimidaBean.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.modelo.parametros;

import java.util.List;

/**
 * The Class EstructuraComprimidaBean.
 */
public class EstructuraComprimidaBean {

    /** The cabeceras. */
    private byte[] cabeceras;

    /** The frecuencias. */
    private byte[] frecuencias;

    /** The estructura comprimida. */
    private byte[] estructuraComprimida;

    /** The punteros. */
    private List<Integer> punteros;

    /**
     * Gets the cabeceras.
     *
     * @return the cabeceras
     */
    public final byte[] getCabeceras() {
        return this.cabeceras;
    }

    /**
     * Sets the cabeceras.
     *
     * @param cabeceras
     *            the new cabeceras
     */
    public final void setCabeceras(final byte[] cabeceras) {
        this.cabeceras = cabeceras;
    }

    /**
     * Gets the frecuencias.
     *
     * @return the frecuencias
     */
    public final byte[] getFrecuencias() {
        return this.frecuencias;
    }

    /**
     * Sets the frecuencias.
     *
     * @param frecuencias
     *            the new frecuencias
     */
    public final void setFrecuencias(final byte[] frecuencias) {
        this.frecuencias = frecuencias;
    }

    /**
     * Gets the estructura comprimida.
     *
     * @return the estructura comprimida
     */
    public final byte[] getEstructuraComprimida() {
        return this.estructuraComprimida;
    }

    /**
     * Sets the estructura comprimida.
     *
     * @param estructuraComprimida
     *            the new estructura comprimida
     */
    public final void setEstructuraComprimida(final byte[] estructuraComprimida) {
        this.estructuraComprimida = estructuraComprimida;
    }

    /**
     * Gets the punteros.
     *
     * @return the punteros
     */
    public final List<Integer> getPunteros() {
        return this.punteros;
    }

    /**
     * Sets the punteros.
     *
     * @param punteros
     *            the new punteros
     */
    public final void setPunteros(final List<Integer> punteros) {
        this.punteros = punteros;
    }

}
