/**
 * ConfiguracionHelper.java 03-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util;

import java.util.ArrayList;
import java.util.List;

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

    /** The name. */
    private String name;

    /** The env. */
    private String env;

    /** The servers. */
    private List<String> servers = new ArrayList<String>();

    /** The numbers. */
    private List<Integer> numbers = new ArrayList<Integer>();

    /** The minimum square. */
    private Integer minimumSquare;

    /** The limites. */
    private Integer limites;

    /** The distancia entre snapshots. */
    private Integer distanciaEntreSnapshots;

    /** Los segundos entre instantes. */
    private Integer segundosEntreInstantes;

    /** The metros por celda. */
    private Integer metrosPorCelda;

    /** En metros segundo */
    private Double velocidadMaxima;

    /** The instantes hasta desaparicion. */
    private int instantesHastaDesaparicion;

    /**
     * Gets the numbers.
     * 
     * @return the numbers
     */
    public List<Integer> getNumbers() {
        return this.numbers;
    }

    /**
     * Sets the numbers.
     * 
     * @param numbers
     *            the new numbers
     */
    public void setNumbers(final List<Integer> numbers) {
        this.numbers = numbers;
    }

    /**
     * Gets the servers.
     * 
     * @return the servers
     */
    public List<String> getServers() {
        return this.servers;
    }

    /**
     * Sets the servers.
     * 
     * @param servers
     *            the new servers
     */
    public void setServers(final List<String> servers) {
        this.servers = servers;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the env.
     * 
     * @return the env
     */
    public String getEnv() {
        return this.env;
    }

    /**
     * Sets the env.
     * 
     * @param env
     *            the new env
     */
    public void setEnv(final String env) {
        this.env = env;
    }

    /**
     * Gets the minimum square.
     * 
     * @return the minimum square
     */
    public Integer getMinimumSquare() {
        return this.minimumSquare;
    }

    /**
     * Sets the minimum square.
     * 
     * @param minimumSquare
     *            the new minimum square
     */
    public void setMinimumSquare(final Integer minimumSquare) {
        this.minimumSquare = minimumSquare;
    }

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
     * Gets the limites.
     * 
     * @return the limites
     */
    public Integer getLimites() {
        return this.limites;
    }

    /**
     * Sets the limites.
     * 
     * @param limites
     *            the new limites
     */
    public void setLimites(final Integer limites) {
        this.limites = limites;
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
}
