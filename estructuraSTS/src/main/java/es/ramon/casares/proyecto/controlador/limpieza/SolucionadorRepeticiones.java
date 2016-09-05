/**
 * SolucionadorRepetidosHelper.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.controlador.limpieza;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ramon.casares.proyecto.util.objetos.ObjetoMovil;

/**
 * The Class SolucionadorRepetidosHelper.
 *
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class SolucionadorRepeticiones {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(SolucionadorRepeticiones.class);

    /** The datareader. */
    private RandomAccessFile datareader;

    /** The mapa ids. */
    private final HashMap<Integer, ObjetoMovil> mapaIds = new HashMap<Integer, ObjetoMovil>();

    /**
     * Resolver repeticiones.
     *
     * @param ficheroNormalizado
     *            fichero normalizado
     * @throws NumberFormatException
     *             de number format exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void resolverRepeticiones(final File ficheroNormalizado) throws NumberFormatException, IOException {
        String currentLine;

        final File tempFile = new File("src/main/resources/ficheroSinRepetidos");

        int lastInstant = 0;

        // if file doesnt exists, then create it
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        this.datareader = new RandomAccessFile(ficheroNormalizado, "r");

        while ((currentLine = this.datareader.readLine()) != null) {

            final String[] result = currentLine.trim().split("\\s");
            if (result.length == 4) {
                final int instant = Integer.valueOf(result[0]); // En segundos

                final int id = Integer.valueOf(result[1]);
                final int x = Integer.valueOf(result[2]); // Longitud
                final int y = Integer.valueOf(result[3]); // Latitud

                final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);
                if (instant != lastInstant) {
                    if ((instant % 1000) == 0) {
                        logger.info("Instante: " + instant);
                    }
                    for (final ObjetoMovil objeto : this.mapaIds.values()) {
                        writer.write(objeto.getInstante() + " " + objeto.getObjetoId() + " " + objeto.getPosicionX() +
                                " " + objeto.getPosicionY() + "\n");
                    }
                    lastInstant = instant;
                    this.mapaIds.clear();
                    writer.flush();

                }
                this.mapaIds.put(id, nuevaPos);
            }
        }
        writer.flush();
        writer.close();
    }

}
