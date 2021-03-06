/**
 * PreparadorDatos.java 26-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.controlador.helpers.ConfiguracionHelper;
import es.ramon.casares.proyecto.controlador.limpieza.Normalizador;
import es.ramon.casares.proyecto.controlador.limpieza.SolucionadorColisiones;
import es.ramon.casares.proyecto.controlador.limpieza.SolucionadorColisiones.ImpossibleToSolveColisionException;
import es.ramon.casares.proyecto.controlador.limpieza.SolucionadorRepeticiones;
import es.ramon.casares.proyecto.controlador.limpieza.parser.LineaEntradaParseada;
import es.ramon.casares.proyecto.controlador.limpieza.parser.ParseadorFicherosEntrada;
import es.ramon.casares.proyecto.controlador.limpieza.parser.impl.ParseadorFicherosEntradaImis;
import es.ramon.casares.proyecto.util.FunctionUtils;

/**
 * La clase PreparadorDatos.
 */
@RestController
public class PreparadorDatos {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(PreparadorDatos.class);

    /** The configuracion. */
    @Autowired
    private ConfiguracionHelper configuracion;

    /** The limite superior. */
    private int limiteMovimiento;

    /** The numero objetos. */
    private int numeroObjetos;

    /** The normalizador. */
    private Normalizador normalizador;

    /**
     * Instantiates a new preparador datos.
     */
    public PreparadorDatos() {

    }

    /**
     * En el primer analisis se establecen los limites del mapa y el numero de objetos diferentes que existen.
     *
     * @throws ImpossibleToSolveColisionException
     * @throws NumberFormatException
     */
    @RequestMapping("/analizar")
    public String prepararDatosFichero() throws NumberFormatException, ImpossibleToSolveColisionException {

        // Inicializamos el lector del fichero
        final InputStream is;
        try {
            is = new FileInputStream("src/main/resources/imis1day");

            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            final ParseadorFicherosEntrada parseador = new ParseadorFicherosEntradaImis(
                    this.configuracion.getSegundosEntreInstantes());

            Double menorLatitud = new Double(Integer.MAX_VALUE);
            Double mayorLatitud = 0D;
            Double menorLongitud = new Double(Integer.MAX_VALUE);
            Double mayorLongitud = 0D;
            final Set<Integer> idsObjetos = new HashSet<Integer>();
            long numeroLineas = 0L;
            int ultimoInstante = 0;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                final LineaEntradaParseada linea = parseador.parsearLineaEntrada(line);

                if (linea.getPosX() > mayorLongitud) {
                    mayorLongitud = linea.getPosX();
                }
                if (linea.getPosY() > mayorLatitud) {
                    mayorLatitud = linea.getPosY();
                }
                if (linea.getPosX() < menorLongitud) {
                    menorLongitud = linea.getPosX();
                }
                if (linea.getPosY() < menorLatitud) {
                    menorLatitud = linea.getPosY();
                }
                idsObjetos.add(linea.getIdObjeto());
                ultimoInstante = linea.getInstante();
                numeroLineas++;
            }
            logger.info("Analizando limites");
            analizadorDeLimites(menorLatitud, mayorLatitud, menorLongitud, mayorLongitud, idsObjetos, numeroLineas,
                    ultimoInstante);
            this.limiteMovimiento = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                    * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));
            logger.info("Velocidad máxima (cuadrados/instante): " + this.limiteMovimiento);
            br.close();
            is.close();
            logger.info("Crear fichero normalizado");
            crearFicheroNormalizado();
            final File ficheroNormalizado = new File("src/main/resources/ficheroNormalizado");

            // Si un objeto produce varias notificaciones entre instantes habra datos de mas
            final SolucionadorRepeticiones solucionadorRepetidos = new SolucionadorRepeticiones();
            logger.info("Eliminar datos repetidos");
            solucionadorRepetidos.resolverRepeticiones(ficheroNormalizado);

            final File ficheroSinRepeticiones = new File("src/main/resources/ficheroSinRepetidos");

            final File ficheroSinColisiones = tratarColisionesEnDataSet(ficheroSinRepeticiones);

            crearFicheroFrecuencias(ficheroSinColisiones);
            is.close();
            return "DONE";
        } catch (final Exception e) {
            throw new InternalError(e.getMessage());
        }

    }

    /**
     * Tratar colisiones en data set.
     *
     * @param ficheroSinRepeticiones
     *            the fichero sin repeticiones
     * @return the resource
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ImpossibleToSolveColisionException
     *             the impossible to solve colision exception
     */
    private File tratarColisionesEnDataSet(final File ficheroSinRepeticiones)
            throws IOException, ImpossibleToSolveColisionException {
        final SolucionadorColisiones solucionadorColisiones = new SolucionadorColisiones();

        logger.info("Solucionar colisiones");
        int numColisiones = solucionadorColisiones.resolverColisiones(ficheroSinRepeticiones);

        logger.info("Numero colisiones: " + numColisiones);

        final File ficheroSinColisiones = new File("src/main/resources/datafileSinColisiones");

        final SolucionadorColisiones revisorColisiones = new SolucionadorColisiones();
        logger.info("Revisar colisiones");
        numColisiones = revisorColisiones.detectarColisiones(ficheroSinColisiones);

        logger.info("Numero colisiones final : " + numColisiones);
        return ficheroSinColisiones;
    }

    private void crearFicheroFrecuencias(final File ficheroSinColisiones)
            throws IOException, ImpossibleToSolveColisionException {
        final CreadorFicheroFrecuencias frecuenciasCreador = new CreadorFicheroFrecuencias(this.limiteMovimiento);
        frecuenciasCreador.inicializar();
        frecuenciasCreador.crearFicheroFrecuencias(this.configuracion, ficheroSinColisiones);
        logger.info("Fichero frecuencias generado");
    }

    /**
     * Crear fichero normalizado.
     */
    private void crearFicheroNormalizado() {

        // Inicializamos el lector del fichero
        InputStream is;

        try {

            final File file = new File("src/main/resources/ficheroNormalizado");

            // si el fichero no eiste lo creamos
            if (!file.exists()) {
                file.createNewFile();
            }

            final FileWriter fw = new FileWriter(file);
            final BufferedWriter bw = new BufferedWriter(fw);

            is = new FileInputStream("src/main/resources/imis1day");

            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            final ParseadorFicherosEntrada parseador = new ParseadorFicherosEntradaImis(
                    this.configuracion.getSegundosEntreInstantes());

            line = br.readLine();

            while ((line = br.readLine()) != null) {
                final LineaEntradaParseada datos = parseador.parsearLineaEntrada(line);

                final String lineaActual = datos.getInstante() + " " + datos.getIdObjeto() + " "
                        + this.normalizador.calcularXnormalizado(datos.getPosX()) + " "
                        + this.normalizador.calcularYnormalizado(datos.getPosY()) + "\n";

                bw.write(lineaActual);
                bw.flush();
            }
            bw.close();
            fw.close();
            br.close();
            is.close();
            logger.info("Fichero normalizado creado");
        } catch (final IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Analizador de limites.
     *
     * @param menorLatitud
     *            menor latitud
     * @param mayorLatitud
     *            mayor latitud
     * @param menorLongitud
     *            menor longitud
     * @param mayorLongitud
     *            mayor longitud
     * @param idsObjetos
     *            ids objetos
     * @param numeroLineas
     *            numero lineas
     * @param ultimoInstante
     *            ultimo instante
     */
    private void analizadorDeLimites(final Double menorLatitud, final Double mayorLatitud, final Double menorLongitud,
            final Double mayorLongitud, final Set<Integer> idsObjetos, final long numeroLineas,
            final int ultimoInstante) {
        double ladoSuperior = FunctionUtils.haversine_km(mayorLatitud, menorLongitud, mayorLatitud, mayorLongitud);
        double ladoInferior = FunctionUtils.haversine_km(menorLatitud, menorLongitud, menorLatitud, mayorLongitud);
        double menorLongitudCorregida = menorLongitud;
        double mayorLongitudCorregida = mayorLongitud;
        if (ladoSuperior >= ladoInferior) {
            // Hemisferio Sur
            while (ladoSuperior >= ladoInferior) {
                menorLongitudCorregida -= this.configuracion.getParametroCorrecionCurvatura();
                mayorLongitudCorregida += this.configuracion.getParametroCorrecionCurvatura();
                ladoInferior = FunctionUtils.haversine_km(menorLatitud, menorLongitudCorregida, menorLatitud,
                        mayorLongitudCorregida);
            }
        } else {
            // Hemisferio Norte
            while (ladoInferior >= ladoSuperior) {
                menorLongitudCorregida -= this.configuracion.getParametroCorrecionCurvatura();
                mayorLongitudCorregida += this.configuracion.getParametroCorrecionCurvatura();
                ladoSuperior = FunctionUtils.haversine_km(menorLatitud, menorLongitudCorregida, menorLatitud,
                        mayorLongitudCorregida);
            }
        }

        final double ladoLateral = FunctionUtils.haversine_km(menorLatitud, mayorLongitud, mayorLatitud,
                mayorLongitud);
        final int numeroCeldasLado = (int) Math
                .ceil((Math.max(ladoLateral, ladoSuperior) * 1000) / this.configuracion.getMetrosPorCelda());
        escribirInformacionObtenida(menorLatitud, mayorLatitud, menorLongitud, mayorLongitud, idsObjetos, numeroLineas,
                ultimoInstante, ladoSuperior, ladoInferior, menorLongitudCorregida, mayorLongitudCorregida,
                ladoLateral,
                numeroCeldasLado);
        this.numeroObjetos = 0;
        for (final Integer integer : idsObjetos) {
            if (this.numeroObjetos < integer) {
                this.numeroObjetos = integer;
            }
        }

        this.normalizador = new Normalizador(menorLatitud, mayorLatitud, menorLongitudCorregida,
                mayorLongitudCorregida,
                numeroCeldasLado);

    }

    /**
     * Escribir informacion obtenida.
     *
     * @param menorLatitud
     *            the menor latitud
     * @param mayorLatitud
     *            the mayor latitud
     * @param menorLongitud
     *            the menor longitud
     * @param mayorLongitud
     *            the mayor longitud
     * @param idsObjetos
     *            the ids objetos
     * @param numeroLineas
     *            the numero lineas
     * @param ultimoInstante
     *            the ultimo instante
     * @param ladoSuperior
     *            the lado superior
     * @param ladoInferior
     *            the lado inferior
     * @param menorLongitudCorregida
     *            the menor longitud corregida
     * @param mayorLongitudCorregida
     *            the mayor longitud corregida
     * @param ladoLateral
     *            the lado lateral
     * @param numeroCeldasLado
     *            the numero celdas lado
     */
    private void escribirInformacionObtenida(final Double menorLatitud, final Double mayorLatitud,
            final Double menorLongitud, final Double mayorLongitud, final Set<Integer> idsObjetos,
            final long numeroLineas, final int ultimoInstante, final double ladoSuperior, final double ladoInferior,
            final double menorLongitudCorregida, final double mayorLongitudCorregida, final double ladoLateral,
            final int numeroCeldasLado) {
        logger.info("***********************************");
        logger.info("Número Lineas:              " + numeroLineas);
        logger.info("Mayor Longitud:             " + mayorLongitud);
        logger.info("Menor Longitud:             " + menorLongitud);
        logger.info("Mayor Longitud Corregida:   " + mayorLongitudCorregida);
        logger.info("Menor Longitud Corregida:   " + menorLongitudCorregida);
        logger.info("Mayor Latitud:              " + mayorLatitud);
        logger.info("Menor Latitud:              " + menorLatitud);
        logger.info("Último instante:            " + ultimoInstante);
        logger.info("Número objetos:             " + idsObjetos.size());
        logger.info("Distancias (Lado superior): " + ladoSuperior);
        logger.info("Distancias (Lado inferior): " + ladoInferior);
        logger.info("Distancias (Lado Izq):      " + ladoLateral);
        logger.info("Distancias (Lado Dcho):     " + ladoLateral);
        logger.info("Distancias (Hipotenusa):    "
                + FunctionUtils.haversine_km(menorLatitud, menorLongitud, mayorLatitud, mayorLongitud));
        logger.info("Numero cuadrados lado:      " + numeroCeldasLado);
        logger.info("Numero cuadrados totales:   " + (numeroCeldasLado * numeroCeldasLado));
        logger.info("***********************************");
    }
}
