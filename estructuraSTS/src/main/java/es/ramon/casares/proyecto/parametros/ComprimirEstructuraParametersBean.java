/**
 * ComprimirEstructuraParametersBean.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.parametros;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Class ComprimirEstructuraParametersBean.
 */
public class ComprimirEstructuraParametersBean {

    /** The separacion snapshots. */
    private Integer separacionSnapshots;

    /** The numero objetos. */
    private Integer numeroObjetos;

    /** The parametro s. */
    private Integer parametroS;

    /** The parametro c. */
    private Integer parametroC;

    /** The latitud inferior mapa. */
    private Integer latitudInferiorMapa;

    /** The latitud superior mapa. */
    private Integer latitudSuperiorMapa;

    /** The longitud inferior mapa. */
    private Integer longitudInferiorMapa;

    /** The longitud superior mapa. */
    private Integer longitudSuperiorMapa;

    /** The fecha instante inicial. */
    private Integer fechaInstanteInicial;

    /** The segundos por instante. */
    private Integer segundosPorInstante;

    /** The codigo reaparicion absoluta. */
    private Integer limiteMovimiento;

    /** The limite cuadrados. */
    private Integer limiteCuadrados;

    /**
     * Gets the separacion snapshots.
     *
     * @return the separacion snapshots
     */
    public final Integer getSeparacionSnapshots() {
        return this.separacionSnapshots;
    }

    /**
     * Sets the separacion snapshots.
     *
     * @param separacionSnapshots
     *            the new separacion snapshots
     */
    public final void setSeparacionSnapshots(final Integer separacionSnapshots) {
        this.separacionSnapshots = separacionSnapshots;
    }

    /**
     * Gets the numero objetos.
     *
     * @return the numero objetos
     */
    public final Integer getNumeroObjetos() {
        return this.numeroObjetos;
    }

    /**
     * Sets the numero objetos.
     *
     * @param numeroObjetos
     *            the new numero objetos
     */
    public final void setNumeroObjetos(final Integer numeroObjetos) {
        this.numeroObjetos = numeroObjetos;
    }

    /**
     * Gets the parametro s.
     *
     * @return the parametro s
     */
    public final Integer getParametroS() {
        return this.parametroS;
    }

    /**
     * Sets the parametro s.
     *
     * @param parametroS
     *            the new parametro s
     */
    public final void setParametroS(final Integer parametroS) {
        this.parametroS = parametroS;
    }

    /**
     * Gets the parametro c.
     *
     * @return the parametro c
     */
    public final Integer getParametroC() {
        return this.parametroC;
    }

    /**
     * Sets the parametro c.
     *
     * @param parametroC
     *            the new parametro c
     */
    public final void setParametroC(final Integer parametroC) {
        this.parametroC = parametroC;
    }

    /**
     * Gets the latitud inferior mapa.
     *
     * @return the latitud inferior mapa
     */
    public final Integer getLatitudInferiorMapa() {
        return this.latitudInferiorMapa;
    }

    /**
     * Sets the latitud inferior mapa.
     *
     * @param latitudInferiorMapa
     *            the new latitud inferior mapa
     */
    public final void setLatitudInferiorMapa(final Integer latitudInferiorMapa) {
        this.latitudInferiorMapa = latitudInferiorMapa;
    }

    /**
     * Gets the latitud superior mapa.
     *
     * @return the latitud superior mapa
     */
    public final Integer getLatitudSuperiorMapa() {
        return this.latitudSuperiorMapa;
    }

    /**
     * Sets the latitud superior mapa.
     *
     * @param latitudSuperiorMapa
     *            the new latitud superior mapa
     */
    public final void setLatitudSuperiorMapa(final Integer latitudSuperiorMapa) {
        this.latitudSuperiorMapa = latitudSuperiorMapa;
    }

    /**
     * Gets the longitud inferior mapa.
     *
     * @return the longitud inferior mapa
     */
    public final Integer getLongitudInferiorMapa() {
        return this.longitudInferiorMapa;
    }

    /**
     * Sets the longitud inferior mapa.
     *
     * @param longitudInferiorMapa
     *            the new longitud inferior mapa
     */
    public final void setLongitudInferiorMapa(final Integer longitudInferiorMapa) {
        this.longitudInferiorMapa = longitudInferiorMapa;
    }

    /**
     * Gets the longitud superior mapa.
     *
     * @return the longitud superior mapa
     */
    public final Integer getLongitudSuperiorMapa() {
        return this.longitudSuperiorMapa;
    }

    /**
     * Sets the longitud superior mapa.
     *
     * @param longitudSuperiorMapa
     *            the new longitud superior mapa
     */
    public final void setLongitudSuperiorMapa(final Integer longitudSuperiorMapa) {
        this.longitudSuperiorMapa = longitudSuperiorMapa;
    }

    /**
     * Gets the fecha instante inicial.
     *
     * @return the fecha instante inicial
     */
    public final Integer getFechaInstanteInicial() {
        return this.fechaInstanteInicial;
    }

    /**
     * Sets the fecha instante inicial.
     *
     * @param fechaInstanteInicial
     *            the new fecha instante inicial
     */
    public final void setFechaInstanteInicial(final Integer fechaInstanteInicial) {
        this.fechaInstanteInicial = fechaInstanteInicial;
    }

    /**
     * Gets the segundos por instante.
     *
     * @return the segundos por instante
     */
    public final Integer getSegundosPorInstante() {
        return this.segundosPorInstante;
    }

    /**
     * Sets the segundos por instante.
     *
     * @param segundosPorInstante
     *            the new segundos por instante
     */
    public final void setSegundosPorInstante(final Integer segundosPorInstante) {
        this.segundosPorInstante = segundosPorInstante;
    }

    /**
     * Gets the limite movimiento.
     *
     * @return the limite movimiento
     */
    public final Integer getLimiteMovimiento() {
        return this.limiteMovimiento;
    }

    /**
     * Sets the limite movimiento.
     *
     * @param limiteMovimiento
     *            the new limite movimiento
     */
    public final void setLimiteMovimiento(final Integer limiteMovimiento) {
        this.limiteMovimiento = limiteMovimiento;
    }

    /**
     * Gets the limite cuadrados.
     *
     * @return the limite cuadrados
     */
    public Integer getLimiteCuadrados() {
        return this.limiteCuadrados;
    }

    /**
     * Sets the limite cuadrados.
     *
     * @param limiteCuadrados
     *            the new limite cuadrados
     */
    public void setLimiteCuadrados(final Integer limiteCuadrados) {
        this.limiteCuadrados = limiteCuadrados;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object other) {
        if (!(other instanceof ComprimirEstructuraParametersBean)) {
            return false;
        }
        final ComprimirEstructuraParametersBean castOther = (ComprimirEstructuraParametersBean) other;
        return new EqualsBuilder()
                .append(this.separacionSnapshots, castOther.separacionSnapshots)
                .append(this.numeroObjetos, castOther.numeroObjetos).append(this.parametroS, castOther.parametroS)
                .append(this.parametroC, castOther.parametroC)
                .append(this.latitudInferiorMapa, castOther.latitudInferiorMapa)
                .append(this.latitudSuperiorMapa, castOther.latitudSuperiorMapa)
                .append(this.longitudInferiorMapa, castOther.longitudInferiorMapa)
                .append(this.longitudSuperiorMapa, castOther.longitudSuperiorMapa)
                .append(this.fechaInstanteInicial, castOther.fechaInstanteInicial)
                .append(this.segundosPorInstante, castOther.segundosPorInstante)
                .append(this.limiteMovimiento, castOther.limiteMovimiento)
                .append(this.limiteCuadrados, castOther.limiteCuadrados).isEquals();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append(this.separacionSnapshots)
                .append(this.numeroObjetos)
                .append(this.parametroS).append(this.parametroC).append(this.latitudInferiorMapa)
                .append(this.latitudSuperiorMapa)
                .append(this.longitudInferiorMapa).append(this.longitudSuperiorMapa).append(this.fechaInstanteInicial)
                .append(this.segundosPorInstante).append(this.limiteMovimiento).append(this.limiteCuadrados)
                .toHashCode();
    }

    @Override
    public final String toString() {
        return new ToStringBuilder(this)
                .append("separacionSnapshots", this.separacionSnapshots).append("numeroObjetos", this.numeroObjetos)
                .append("parametroS", this.parametroS).append("parametroC", this.parametroC)
                .append("latitudInferiorMapa", this.latitudInferiorMapa)
                .append("latitudSuperiorMapa", this.latitudSuperiorMapa)
                .append("longitudInferiorMapa", this.longitudInferiorMapa)
                .append("longitudSuperiorMapa", this.longitudSuperiorMapa)
                .append("fechaInstanteInicial", this.fechaInstanteInicial)
                .append("segundosPorInstante", this.segundosPorInstante)
                .append("codigoReaparicionAbsoluta", this.limiteMovimiento)
                .append("limiteCuadrados", this.limiteCuadrados).toString();
    }

}
