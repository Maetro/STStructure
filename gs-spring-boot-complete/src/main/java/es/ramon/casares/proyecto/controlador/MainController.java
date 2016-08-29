/**
 * MainController.java 26-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.controlador;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.ramon.casares.proyecto.encoder.SCDenseCoder;
import es.ramon.casares.proyecto.modelo.parametros.ComprimirEstructuraParametersBean;
import es.ramon.casares.proyecto.modelo.parametros.LimitesBean;
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

    /** The resource loader. */
    @Autowired
    private ResourceLoader resourceLoader;

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

        final Resource ficheroDataSet = this.resourceLoader.getResource("classpath:datafileSinColisiones");

        this.logger.info("Analizando límites");
        final LimitesBean limites = ControladorHelper.analizadorDeLimites(ficheroDataSet.getFile());

        this.logger.info("Creando Modelo estructura");
        final CreadorEstructura creador = new CreadorEstructura(limiteSuperior, limites.getLimiteMovimiento(),
                limites.getNumeroObjetos());

        final Resource ficheroFrecuencias = this.resourceLoader.getResource("classpath:frecuencias");

        creador.inicializar(ficheroFrecuencias, this.configuracion);
        final List<Integer> punteros = creador.crearEstructura(ficheroDataSet, this.configuracion);

        final Resource ficherofrecuencias = this.resourceLoader.getResource("classpath:frecuencias");

        final Resource ficheroCuerpo = this.resourceLoader.getResource("classpath:EstructuraTemporal");

        final ComprimirEstructuraParametersBean parametros = new ComprimirEstructuraParametersBean();

        crearParametrosParaComprimir(limites, parametros);

        this.logger.info("Escribiendo estructura comprimida en fichero");

        CompresorEstructuraHelper.comprimirEstructura(ficherofrecuencias, ficheroCuerpo, punteros,
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
        final int numeroEspiral = ControladorHelper.unidimensionar(limites.getLimiteMovimiento(),
                -limites.getLimiteMovimiento());

        parametros.setSeparacionSnapshots(this.configuracion.getDistanciaEntreSnapshots());
        parametros.setNumeroObjetos(limites.getNumeroObjetos());
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
                                                                                                       // code)
            IOException, NumberFormatException, ImpossibleToSolveColisionException {

        final Resource ficheroEstructura = this.resourceLoader.getResource("classpath:estructuraComprimida");

        CompresorEstructuraHelper.descomprimirEstructura(ficheroEstructura.getFile(),
                this.configuracion.getChunkSize());

        return "DONE";
    }

    /**
     * Test.
     *
     * @return the string
     */
    @RequestMapping("/test")
    public final String test() {

        final SCDenseCoder encoder = new SCDenseCoder(this.configuracion.getS(), this.configuracion.getC());

        for (int i = 0; i < CICLOS_TEST; i++) {
            final List<Integer> list = encoder.encode(i);
            Collections.reverse(list);
            System.out.println("(" + i + ")" + list + " - " + encoder.decode(list));

            if (i != encoder.decode(list)) {
                System.out.println("FALLO");
            }
        }

        return "DONE";
    }
}
