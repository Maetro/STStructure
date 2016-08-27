/**
 * SolucionadorRepetidosHelper.java 08-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import org.springframework.core.io.Resource;

import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;



/**
 * The Class SolucionadorRepetidosHelper.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class SolucionadorRepetidosHelper {

    private RandomAccessFile datareader;

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
    public void resolverRepeticiones(final Resource ficheroNormalizado) throws NumberFormatException, IOException {
        String currentLine;

        final File tempFile = new File("src/main/resources/datafileSinRepetidos");

        int lastInstant = 0;

        // if file doesnt exists, then create it
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        this.datareader = new RandomAccessFile(ficheroNormalizado.getFile(), "r");

        while ((currentLine = this.datareader.readLine()) != null) {

            final String[] result = currentLine.trim().split("\\s");
            if (result.length == 4) {
                final int instant = Integer.valueOf(result[0]); // En segundos
                final int id = Integer.valueOf(result[1]);

                final int x = Integer.valueOf(result[2]); // Longitud
                final int y = Integer.valueOf(result[3]); // Latitud
                final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);

                if (instant != lastInstant) {
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
