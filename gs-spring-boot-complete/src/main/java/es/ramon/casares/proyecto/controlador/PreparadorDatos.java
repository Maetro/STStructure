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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.modelo.objetos.ObjectDataConGaps;
import es.ramon.casares.proyecto.modelo.objetos.ObjectInformation;
import es.ramon.casares.proyecto.modelo.objetos.Point;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.CreadorFicheroFrecuencias;
import es.ramon.casares.proyecto.util.Normalizador;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;
import es.ramon.casares.proyecto.util.SolucionadorRepetidosHelper;
import es.ramon.casares.proyecto.util.parser.LineaEntradaParseada;
import es.ramon.casares.proyecto.util.parser.ParseadorFicherosEntrada;
import es.ramon.casares.proyecto.util.parser.impl.ParseadorFicherosEntradaImis;

/**
 * La clase PreparadorDatos.
 */
@RestController
public class PreparadorDatos {

    /** The configuracion. */
    @Autowired
    private ConfiguracionHelper configuracion;

    /** The resource loader. */
    @Autowired
    private ResourceLoader resourceLoader;

    /** The limite superior. */
    private int limiteSuperior;

    /** The numero objetos. */
    private int numeroObjetos;

    /** The mapa ids. */
    private final HashMap<Integer, ObjectDataConGaps> mapaIds = new HashMap<Integer, ObjectDataConGaps>();

    /** The map information ids. */
    private final HashMap<Integer, ObjectInformation> mapInformationIds = new HashMap<Integer, ObjectInformation>();

    /** The map ids desaparecidos. */
    private final HashMap<Integer, String> mapIdsDesaparecidos = new HashMap<Integer, String>();

    /** The last known position. */
    private final HashMap<Integer, Point> lastKnownPosition = new HashMap<Integer, Point>();

    /** The normalizador. */
    private Normalizador normalizador;

    /**
     * Instantiates a new preparador datos.
     */
    public PreparadorDatos() {

    }

    /**
     * Inicializar posiciones.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void inicializarPosiciones() throws IOException {
        final Point newPoint = new Point(0, -1, -1, this.configuracion.getMetrosPorCelda());
        final ObjectDataConGaps data = new ObjectDataConGaps();
        data.setPoint(newPoint);
        for (int i = 1; i <= this.numeroObjetos; i++) {
            data.setObjectId(i);
            this.mapaIds.put(i, data);
            this.mapInformationIds.put(i, new ObjectInformation(0d, 0d));
            this.mapIdsDesaparecidos.put(i, "-1:-1");
            this.lastKnownPosition.put(i, new Point(0, -1, -1, this.configuracion.getMetrosPorCelda())); // Desconocido
                                                                                                         // es
                                                                                                         // -1:-1
        }

    }

    /**
     * En el primer analisis se establecen los limites del mapa y el numero de objetos diferentes que existen.
     *
     * @throws ImpossibleToSolveColisionException
     * @throws NumberFormatException
     */
    @RequestMapping("/analizar")
    private void primerAnalisis() throws NumberFormatException, ImpossibleToSolveColisionException {
        final Resource ficheroEntrada = this.resourceLoader.getResource("classpath:imis1day");
        // Inicializamos el lector del fichero
        final InputStream is;
        try {
            is = ficheroEntrada.getInputStream();

            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            final ParseadorFicherosEntrada parseador = new ParseadorFicherosEntradaImis(
                    this.configuracion.getSegundosEntreInstantes());

            Double menorLatitud = 50000D;
            Double mayorLatitud = 0D;
            Double menorLongitud = 50000D;
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
            System.out.println("Analizando limites");
            analizadorDeLimites(menorLatitud, mayorLatitud, menorLongitud, mayorLongitud, idsObjetos, numeroLineas,
                    ultimoInstante);
            System.out.println("Inicializar posiciones");
            inicializarPosiciones();
            this.limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                    * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));
            System.out.println("Velocidad máxima (cuadrados/instante): " + this.limiteSuperior);
            br.close();
            is.close();
            System.out.println("Crear fichero normalizado");
            crearFicheroNormalizado();
            final Resource ficheroNormalizado = this.resourceLoader.getResource("classpath:imis1dayNormalizado");

            // Si un objeto produce varias notificaciones entre instantes habra datos de mas
            final SolucionadorRepetidosHelper solucionadorRepetidos = new SolucionadorRepetidosHelper();
            System.out.println("Eliminar datos repetidos");
            solucionadorRepetidos.resolverRepeticiones(ficheroNormalizado);

            final Resource ficheroSinRepeticiones = this.resourceLoader.getResource("classpath:datafileSinRepetidos");

            final SolucionadorColisionesHelper solucionadorColisiones = new SolucionadorColisionesHelper();
            System.out.println("Solucionar colisiones");
            int numColisiones = solucionadorColisiones.resolverColisiones(ficheroSinRepeticiones);

            System.out.println("Numero colisiones: " + numColisiones);

            final Resource ficheroSinColisiones = this.resourceLoader.getResource("classpath:datafileSinColisiones");

            final SolucionadorColisionesHelper revisorColisiones = new SolucionadorColisionesHelper();
            System.out.println("Revisar colisiones");
            numColisiones = revisorColisiones.detectarColisiones(ficheroSinColisiones);

            System.out.println("Numero colisiones final : " + numColisiones);

            final CreadorFicheroFrecuencias frecuenciasCreador = new CreadorFicheroFrecuencias(this.limiteSuperior);
            frecuenciasCreador.inicializar();
            frecuenciasCreador.crearFicheroFrecuencias(this.configuracion, ficheroSinColisiones);
            System.out.println("Fichero frecuencias generado");
        } catch (final Exception e) {
            throw new InternalError(e);
        }

    }

    private void crearFicheroNormalizado() {
        final Resource ficheroEntrada = this.resourceLoader.getResource("classpath:imis1day");

        // Inicializamos el lector del fichero
        InputStream is;

        try {
            // create new file

            final File file = new File("src/main/resources/imis1dayNormalizado");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            final FileWriter fw = new FileWriter(file.getAbsoluteFile());
            final BufferedWriter bw = new BufferedWriter(fw);
            // write in file

            // close connection

            is = ficheroEntrada.getInputStream();

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
            }
            bw.close();
            br.close();
            is.close();
            System.out.println("Fichero normalizado creado");
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
        double ladoSuperior = ControladorHelper.haversine_km(mayorLatitud, menorLongitud, mayorLatitud, mayorLongitud);
        double ladoInferior = ControladorHelper.haversine_km(menorLatitud, menorLongitud, menorLatitud, mayorLongitud);
        double menorLongitudCorregida = menorLongitud;
        double mayorLongitudCorregida = mayorLongitud;
        if (ladoSuperior >= ladoInferior) {
            // Hemisferio Sur
            while (ladoSuperior >= ladoInferior) {
                menorLongitudCorregida -= 0.001;
                mayorLongitudCorregida += 0.001;
                ladoInferior = ControladorHelper.haversine_km(menorLatitud, menorLongitudCorregida, menorLatitud,
                        mayorLongitudCorregida);
            }
        } else {
            // Hemisferio Norte
            while (ladoInferior >= ladoSuperior) {
                menorLongitudCorregida -= 0.001;
                mayorLongitudCorregida += 0.001;
                ladoSuperior = ControladorHelper.haversine_km(menorLatitud, menorLongitudCorregida, menorLatitud,
                        mayorLongitudCorregida);
            }
        }

        final double ladoLateral = ControladorHelper.haversine_km(menorLatitud, mayorLongitud, mayorLatitud,
                mayorLongitud);
        final int numeroCeldasLado = (int) Math
                .ceil((Math.max(ladoLateral, ladoSuperior) * 1000) / this.configuracion.getMetrosPorCelda());
        System.out.println("***********************************");
        System.out.println("Número Lineas:              " + numeroLineas);
        System.out.println("Mayor Longitud:             " + mayorLongitud);
        System.out.println("Menor Longitud:             " + menorLongitud);
        System.out.println("Mayor Longitud Corregida:   " + mayorLongitudCorregida);
        System.out.println("Menor Longitud Corregida:   " + menorLongitudCorregida);
        System.out.println("Mayor Latitud:              " + mayorLatitud);
        System.out.println("Menor Latitud:              " + menorLatitud);
        System.out.println("Último instante:            " + ultimoInstante);
        System.out.println("Número objetos:             " + idsObjetos.size());
        System.out.println("Distancias (Lado superior): " + ladoSuperior);
        System.out.println("Distancias (Lado inferior): " + ladoInferior);
        System.out.println("Distancias (Lado Izq):      " + ladoLateral);
        System.out.println("Distancias (Lado Dcho):     " + ladoLateral);
        System.out.println("Distancias (Hipotenusa):    "
                + ControladorHelper.haversine_km(menorLatitud, menorLongitud, mayorLatitud, mayorLongitud));
        System.out.println("Numero cuadrados lado:      " + numeroCeldasLado);
        System.out.println("Numero cuadrados totales:   " + (numeroCeldasLado * numeroCeldasLado));
        System.out.println("***********************************");
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
}
