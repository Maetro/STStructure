/**
 * Aplicacion.java 29-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import es.ramon.casares.proyecto.controlador.MainController;

/**
 * The Class Aplicacion.
 */
@SpringBootApplication
public class Aplicacion {
    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    public static void main(final String[] args) {
        logger.info("Arrancando aplicacion");
        final ApplicationContext ctx = SpringApplication.run(Aplicacion.class, args);
    }
}
