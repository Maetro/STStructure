/**
 * ConfiguracionHelper.java 03-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.controlador.helpers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The Class ConfiguracionHelper.
 *
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class ConfiguracionHelper {

    /** The distancia entre snapshots. */
    private Integer distanciaEntreSnapshots;

    /** Los segundos entre instantes. */
    private Integer segundosEntreInstantes;

    /** The metros por celda. */
    private Integer metrosPorCelda;

    /** En metros segundo. */
    private Double velocidadMaxima;

    /** The instantes hasta desaparicion. */
    private int instantesHastaDesaparicion;

    private int S;

    private int C;

    private float parametroCorrecionCurvatura;

    /**
     * Gets the distancia entre snapshots.
     *
     * @return the distancia entre snapshots
     */
    public Integer getDistanciaEntreSnapshots() {
        return this.distanciaEntreSnapshots;
    }

    /**
     * Sets the distancia entre snapshots.
     *
     * @param distanciaEntreSnapshots
     *            the new distancia entre snapshots
     */
    public void setDistanciaEntreSnapshots(final Integer distanciaEntreSnapshots) {
        this.distanciaEntreSnapshots = distanciaEntreSnapshots;
    }

    /**
     * Obtiene segundos entre instantes.
     *
     * @return segundos entre instantes
     */
    public Integer getSegundosEntreInstantes() {
        return this.segundosEntreInstantes;
    }

    /**
     * Establece segundos entre instantes.
     *
     * @param segundosEntreInstantes
     *            nuevo segundos entre instantes
     */
    public void setSegundosEntreInstantes(final Integer segundosEntreInstantes) {
        this.segundosEntreInstantes = segundosEntreInstantes;
    }

    /**
     * Obtiene metros por celda.
     *
     * @return metros por celda
     */
    public Integer getMetrosPorCelda() {
        return this.metrosPorCelda;
    }

    /**
     * Establece metros por celda.
     *
     * @param metrosPorCelda
     *            nuevo metros por celda
     */
    public void setMetrosPorCelda(final Integer metrosPorCelda) {
        this.metrosPorCelda = metrosPorCelda;
    }

    /**
     * Obtiene velocidad maxima.
     *
     * @return velocidad maxima
     */
    public Double getVelocidadMaxima() {
        return this.velocidadMaxima;
    }

    /**
     * Establece velocidad maxima.
     *
     * @param velocidadMaxima
     *            nuevo velocidad maxima
     */
    public void setVelocidadMaxima(final Double velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
    }

    /**
     * Obtiene instantes hasta desaparicion.
     *
     * @return instantes hasta desaparicion
     */
    public int getInstantesHastaDesaparicion() {
        return this.instantesHastaDesaparicion;
    }

    /**
     * Establece instantes hasta desaparicion.
     *
     * @param instantesHastaDesaparicion
     *            nuevo instantes hasta desaparicion
     */
    public void setInstantesHastaDesaparicion(final int instantesHastaDesaparicion) {
        this.instantesHastaDesaparicion = instantesHastaDesaparicion;
    }

    public int getC() {
        return this.C;
    }

    public void setC(final int c) {
        this.C = c;
    }

    public int getS() {
        return this.S;
    }

    public void setS(final int s) {
        this.S = s;
    }

    /**
     * Gets the parametro correcion curvatura.
     *
     * @return the parametro correcion curvatura
     */
    public float getParametroCorrecionCurvatura() {
        return this.parametroCorrecionCurvatura;
    }

    /**
     * Sets the parametro correcion curvatura.
     *
     * @param parametroCorrecionCurvatura
     *            the new parametro correcion curvatura
     */
    public void setParametroCorrecionCurvatura(final float parametroCorrecionCurvatura) {
        this.parametroCorrecionCurvatura = parametroCorrecionCurvatura;
    }

}
