/**
 * ParametrosCompresionLogsBean.java 02-sep-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.parametros;

/**
 * The Class ParametrosCompresionLogsBean.
 */
public class ParametrosCompresionLogsBean {

    /** The numero bloques. */
    private int numeroBloques;

    /** The parametro s. */
    private int parametroS;

    /** The parametro c. */
    private int parametroC;

    /** The posicion reaparicion relativa. */
    private int posicionReaparicionRelativa;

    /** The posicion reaparicion fuera limites. */
    private int posicionReaparicionFueraLimites;

    /** The posicion reaparicion absoluta. */
    private int posicionReaparicionAbsoluta;

    /**
     * Instantiates a new parametros compresion logs bean.
     */
    public ParametrosCompresionLogsBean() {
    }

    /**
     * Gets the numero bloques.
     *
     * @return the numero bloques
     */
    public int getNumeroBloques() {
        return this.numeroBloques;
    }

    /**
     * Sets the numero bloques.
     *
     * @param numeroBloques
     *            the new numero bloques
     */
    public void setNumeroBloques(final int numeroBloques) {
        this.numeroBloques = numeroBloques;
    }

    /**
     * Gets the parametro s.
     *
     * @return the parametro s
     */
    public int getParametroS() {
        return this.parametroS;
    }

    /**
     * Sets the parametro s.
     *
     * @param parametroS
     *            the new parametro s
     */
    public void setParametroS(final int parametroS) {
        this.parametroS = parametroS;
    }

    /**
     * Gets the parametro c.
     *
     * @return the parametro c
     */
    public int getParametroC() {
        return this.parametroC;
    }

    /**
     * Sets the parametro c.
     *
     * @param parametroC
     *            the new parametro c
     */
    public void setParametroC(final int parametroC) {
        this.parametroC = parametroC;
    }

    /**
     * Gets the posicion reaparicion relativa.
     *
     * @return the posicion reaparicion relativa
     */
    public int getPosicionReaparicionRelativa() {
        return this.posicionReaparicionRelativa;
    }

    /**
     * Sets the posicion reaparicion relativa.
     *
     * @param posicionReaparicionRelativa
     *            the new posicion reaparicion relativa
     */
    public void setPosicionReaparicionRelativa(final int posicionReaparicionRelativa) {
        this.posicionReaparicionRelativa = posicionReaparicionRelativa;
    }

    /**
     * Gets the posicion reaparicion fuera limites.
     *
     * @return the posicion reaparicion fuera limites
     */
    public int getPosicionReaparicionFueraLimites() {
        return this.posicionReaparicionFueraLimites;
    }

    /**
     * Sets the posicion reaparicion fuera limites.
     *
     * @param posicionReaparicionFueraLimites
     *            the new posicion reaparicion fuera limites
     */
    public void setPosicionReaparicionFueraLimites(final int posicionReaparicionFueraLimites) {
        this.posicionReaparicionFueraLimites = posicionReaparicionFueraLimites;
    }

    /**
     * Gets the posicion reaparicion absoluta.
     *
     * @return the posicion reaparicion absoluta
     */
    public int getPosicionReaparicionAbsoluta() {
        return this.posicionReaparicionAbsoluta;
    }

    /**
     * Sets the posicion reaparicion absoluta.
     *
     * @param posicionReaparicionAbsoluta
     *            the new posicion reaparicion absoluta
     */
    public void setPosicionReaparicionAbsoluta(final int posicionReaparicionAbsoluta) {
        this.posicionReaparicionAbsoluta = posicionReaparicionAbsoluta;
    }

}
