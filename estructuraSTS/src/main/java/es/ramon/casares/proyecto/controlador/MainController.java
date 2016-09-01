/**
 * MainController.java 26-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.Test;
import es.ramon.casares.proyecto.modelo.estructura.Estructura;
import es.ramon.casares.proyecto.modelo.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.modelo.parametros.LimitesBean;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2Tree;
import es.ramon.casares.proyecto.modelo.snapshot.k2tree.K2TreeHelper;
import es.ramon.casares.proyecto.util.CompresorEstructuraHelper;
import es.ramon.casares.proyecto.util.ConfiguracionHelper;
import es.ramon.casares.proyecto.util.ControladorHelper;
import es.ramon.casares.proyecto.util.SolucionadorColisionesHelper.ImpossibleToSolveColisionException;

/**
 * The Class MainController.
 */
@RestController
public class MainController { // NO_UCD (test only)

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(MainController.class);

    /** The Constant CICLOS_TEST. */
    private static final int CICLOS_TEST = 20000;

    /** The configuracion. */
    @Autowired
    private ConfiguracionHelper configuracion;

    /**
     * Crear estructura.
     * 
     * @return the string
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws NumberFormatException
     *             the number format exception
     * @throws ImpossibleToSolveColisionException
     *             the impossible to solve colision exception
     */
    @RequestMapping("/")
    public final String crearEstructura() throws ClassNotFoundException, FileNotFoundException, // NO_UCD (unused code)
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        this.logger.info("Creando estructura");

        final int limiteSuperior = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));

        final File ficheroDataSet = new File("src/main/resources/datafileSinColisiones");

        this.logger.info("Analizando límites");
        final LimitesBean limites = ControladorHelper.analizadorDeLimites(ficheroDataSet);

        this.logger.info("Creando Modelo estructura");
        final CreadorEstructura creador = new CreadorEstructura(limiteSuperior, limites.getLimiteCuadrado(),
                limites.getNumeroObjetos());

        final File ficheroFrecuencias = new File("src/main/resources/frecuencias");

        creador.inicializar(ficheroFrecuencias, this.configuracion);
        final List<Integer> punteros = creador.crearEstructura(ficheroDataSet, this.configuracion);

        final File ficheroCuerpo = new File("src/main/resources/EstructuraTemporal");

        final ComprimirEstructuraParametersBean parametros = new ComprimirEstructuraParametersBean();

        crearParametrosParaComprimir(limites, parametros);

        this.logger.info("Escribiendo estructura comprimida en fichero");

        CompresorEstructuraHelper.comprimirEstructura(ficheroFrecuencias, ficheroCuerpo, punteros,
                parametros);

        this.logger.info("Todo correcto, función terminada");
        return "DONE";
    }

    /**
     * Crear parametros para comprimir.
     * 
     * @param limites
     *            the limites
     * @param parametros
     *            the parametros
     */
    private void crearParametrosParaComprimir(final LimitesBean limites,
            final ComprimirEstructuraParametersBean parametros) {

        final int limite = (int) Math.ceil(this.configuracion.getVelocidadMaxima()
                * this.configuracion.getSegundosEntreInstantes() * (1D / this.configuracion.getMetrosPorCelda()));

        final int numeroEspiral = ControladorHelper.unidimensionar(limite,
                -limite);

        parametros.setSeparacionSnapshots(this.configuracion.getDistanciaEntreSnapshots());
        parametros.setNumeroObjetos(limites.getNumeroObjetos());
        parametros.setLimiteCuadrados(limites.getLimiteCuadrado());
        parametros.setParametroC(this.configuracion.getC());
        parametros.setParametroS(this.configuracion.getS());
        parametros.setSegundosPorInstante(this.configuracion.getSegundosEntreInstantes());
        parametros.setCodigoReaparicionAbsoluta(numeroEspiral);
    }

    /**
     * Descomprimir estructura.
     * 
     * @return the string
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws NumberFormatException
     *             the number format exception
     * @throws ImpossibleToSolveColisionException
     *             the impossible to solve colision exception
     */
    @RequestMapping("/descomprimir")
    public final String descomprimirEstructura() throws ClassNotFoundException, FileNotFoundException, // NO_UCD (unused
                                                                                                       // // Code) //
                                                                                                       // code)
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");

        final Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura,
                this.configuracion.getChunkSize(), 0, 30);

        K2TreeHelper.obtenerPosicionEnSnapshot((K2Tree) estructuraUtil.getSnapshots().get(0), 133,
                ControladorHelper.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados()));

        return "DONE";
    }

    @RequestMapping("/resolver")
    public final String resolverConsultaTimeSlice(@RequestParam(value = "instant") final int instant,
            @RequestParam(value = "idObjeto") final int idObjeto) throws IOException {
        // 2659 1110 11897 22982
        final File ficheroEstructura = new File("src/main/resources/estructuracomprimida");

        final int numLogs = instant % this.configuracion.getDistanciaEntreSnapshots();

        final int numSnapshot = instant / this.configuracion.getDistanciaEntreSnapshots();

        Estructura estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura,
                this.configuracion.getChunkSize(), 0, 0);

        K2TreeHelper.obtenerPosicionEnSnapshot((K2Tree) estructuraUtil.getSnapshots().get(0), 133,
                ControladorHelper.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados()));

        estructuraUtil = CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura,
                this.configuracion.getChunkSize(), numSnapshot, numLogs);

        System.out.println(K2TreeHelper.obtenerPosicionEnSnapshot((K2Tree) estructuraUtil.getSnapshots().get(0),
                idObjeto,
                ControladorHelper.numeroCuadradosSegunLimite(estructuraUtil.getNumeroCuadrados())));

        return "DONE";

    }

    /**
     * Test.
     * 
     * @return the string
     */
    @RequestMapping("/test")
    public final String test() {

        final Test tests = new Test();

        // tests.probarGeneracionK2Tree(this.configuracion);
        tests.probarGeneracionK2TreeyBusqueda(this.configuracion);
        // Test.probarLocalizacionObjetosEnK2Tree();

        return "DONE";
    }

}
