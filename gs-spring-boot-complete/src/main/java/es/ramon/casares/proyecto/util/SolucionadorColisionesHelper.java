/**
 * SolucionadorColisionesHelperAlt.java 09-ago-2016
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
import java.util.Random;

import org.springframework.core.io.Resource;

import es.ramon.casares.proyecto.modelo.objetos.ObjetoMovil;
import es.ramon.casares.proyecto.modelo.objetos.PosicionKey;

/**
 * @author Ramon Casares
 * 
 *         Clase que resuelve las colisiones entre dos objetos que se encuentran en el mismo punto en el mismo instante
 *         Cuando el algoritmo detecta una colisión evalúa cual es el siguiente de los dos objetos que se moverá. De esa
 *         forma aparta el otro objeto desde el instante anterior a la colisión hasta que vuelve a la posición en el
 *         instante posterior.
 */
public class SolucionadorColisionesHelper {

    // Las posiciones ocupadas en el momento actual del fichero
    private final HashMap<PosicionKey, ObjetoMovil> posicionIds = new HashMap<PosicionKey, ObjetoMovil>();

    private RandomAccessFile datareader; // es

    public int numOfCollisions = 0; // concatenacion

    private final HashMap<Integer, ObjetoMovil> mapaIds = new HashMap<Integer, ObjetoMovil>();

    public int resolverColisiones(final Resource ficheroNormalizado) throws NumberFormatException, IOException,
            ImpossibleToSolveColisionException {
        String currentLine;
        int numOfCollisions = 0;
        System.out.println("Rellenando HashMap");
        final File tempFile = new File("src/main/resources/datafileSinColisiones");

        // if file doesnt exists, then create it
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }

        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        this.datareader = new RandomAccessFile(ficheroNormalizado.getFile(), "r");

        while ((currentLine = this.datareader.readLine()) != null) {
            final String[] result = currentLine.trim().split("\\s");
            final int instant = Integer.valueOf(result[0]); // En segundos
            final int id = Integer.valueOf(result[1]);
            final int x = Integer.valueOf(result[2]); // Longitud
            final int y = Integer.valueOf(result[3]); // Latitud

            PosicionKey claveNum = new PosicionKey(x, y);
            final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);

            if (this.mapaIds.containsKey(id)) {
                // Si esta en el mapa hay que cambiar
                // la posicion anotada
                final ObjetoMovil viejaPos = this.mapaIds.get(id);
                final PosicionKey viejaClaveNum = new PosicionKey(viejaPos.getPosicionX(), viejaPos.getPosicionY());
                if (this.posicionIds.get(viejaClaveNum).getObjetoId() == id) {
                    this.posicionIds.remove(viejaClaveNum);
                }
                if (!this.posicionIds.containsKey(claveNum)) { // no hay colision
                    anotarPosicionNoOcupada(id, claveNum, nuevaPos, writer);
                } else { // Hay colision
                    final ObjetoMovil nuevaPosicion = encontrarPosicionLibre(id, instant,
                            claveNum.getX(), claveNum.getY());
                    numOfCollisions++;
                    claveNum = new PosicionKey(nuevaPosicion.getPosicionX(), nuevaPosicion.getPosicionY());
                    anotarPosicionNoOcupada(id, claveNum, nuevaPosicion, writer);
                }
            } else {
                // Es la primera vez que se anota este objeto
                // Miramos si su posicion esta ocupada
                if (!this.posicionIds.containsKey(claveNum)) {
                    // Posicion libre
                    anotarPosicionNoOcupada(id, claveNum, nuevaPos, writer);
                } else {
                    // Posicion ocupada
                    final ObjetoMovil nuevaPosicion = encontrarPosicionLibre(id, instant,
                            claveNum.getX(), claveNum.getY());
                    claveNum = new PosicionKey(nuevaPosicion.getPosicionX(), nuevaPosicion.getPosicionY());

                    anotarPosicionNoOcupada(id, claveNum, nuevaPosicion, writer);
                    numOfCollisions++;
                }
            }

        }

        return numOfCollisions;
    }

    public int detectarColisiones(final Resource ficheroNormalizado) throws NumberFormatException, IOException,
            ImpossibleToSolveColisionException {
        String currentLine;
        int numOfCollisions = 0;

        this.datareader = new RandomAccessFile(ficheroNormalizado.getFile(), "r");

        while ((currentLine = this.datareader.readLine()) != null) {
            final String[] result = currentLine.trim().split("\\s");
            final int instant = Integer.valueOf(result[0]); // En segundos
            final int id = Integer.valueOf(result[1]);
            final int x = Integer.valueOf(result[2]); // Longitud
            final int y = Integer.valueOf(result[3]); // Latitud

            PosicionKey claveNum = new PosicionKey(x, y);
            final ObjetoMovil nuevaPos = new ObjetoMovil(id, instant, x, y);

            if (this.mapaIds.containsKey(id)) {
                // Si esta en el mapa hay que cambiar
                // la posicion anotada
                final ObjetoMovil viejaPos = this.mapaIds.get(id);
                final PosicionKey viejaClaveNum = new PosicionKey(viejaPos.getPosicionX(), viejaPos.getPosicionY());
                if (this.posicionIds.get(viejaClaveNum).getObjetoId() == id) {
                    this.posicionIds.remove(viejaClaveNum);
                }
                if (!this.posicionIds.containsKey(claveNum)) { // no hay colision
                    anotarPosicionNoOcupada(id, claveNum, nuevaPos, null);
                } else { // Hay colision
                    final ObjetoMovil nuevaPosicion = encontrarPosicionLibre(id, instant,
                            claveNum.getX(), claveNum.getY());
                    numOfCollisions++;
                    claveNum = new PosicionKey(nuevaPosicion.getPosicionX(), nuevaPosicion.getPosicionY());
                    anotarPosicionNoOcupada(id, claveNum, nuevaPosicion, null);
                }
            } else {
                // Es la primera vez que se anota este objeto
                // Miramos si su posicion esta ocupada
                if (!this.posicionIds.containsKey(claveNum)) {
                    // Posicion libre
                    anotarPosicionNoOcupada(id, claveNum, nuevaPos, null);
                } else {
                    // Posicion ocupada
                    final ObjetoMovil nuevaPosicion = encontrarPosicionLibre(id, instant,
                            claveNum.getX(), claveNum.getY());
                    claveNum = new PosicionKey(nuevaPosicion.getPosicionX(), nuevaPosicion.getPosicionY());

                    anotarPosicionNoOcupada(id, claveNum, nuevaPosicion, null);
                    numOfCollisions++;
                }
            }

        }

        return numOfCollisions;
    }

    /**
     * Anotar posicion no ocupada.
     * 
     * @param id
     *            id
     * @param claveNum
     *            clave num
     * @param nuevaPos
     *            nueva pos
     * @param writer
     * @throws IOException
     */
    private void anotarPosicionNoOcupada(final int id, final PosicionKey claveNum, final ObjetoMovil nuevaPos,
            final BufferedWriter writer) throws IOException {
        this.posicionIds.put(claveNum, nuevaPos);
        this.mapaIds.put(id, nuevaPos);
        if (writer != null) {
            writer.write(nuevaPos.getInstante() + " " + id + " " + nuevaPos.getPosicionX() + " " + nuevaPos.getPosicionY() + "\n");
        }

    }

    public static int randInt(
            final int min, final int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        final Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        final int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    /*
     * Devuleve la posicion libre adyacente al punto
     */
    private ObjetoMovil encontrarPosicionLibre(final int idObjeto, final int instante,
            int x, int y) throws ImpossibleToSolveColisionException {
        final int origx = x;
        final int origy = y;

        int cont = 0;

        PosicionKey key = new PosicionKey(x, y);
        // Que no sea la posicion anterior, y la celda este vacia.
        while (this.posicionIds.containsKey(key)) {

            switch (cont) {
            case 0:
                y = origy - 0;
                x = origx - 1;
                break;
            case 1:
                y = origy + 0;
                x = origx + 1;
                break;
            case 2:
                y = origy - 1;
                x = origx + 0;
                break;
            case 3:
                y = origy + 1;
                x = origx - 0;
                break;
            case 4:
                y = origy - 1;
                x = origx - 1;
                break;
            case 5:
                y = origy + 1;
                x = origx + 1;
                break;
            case 6:
                y = origy - 1;
                x = origx + 1;
                break;
            case 7:
                y = origy + 1;
                x = origx - 1;
                break;
            case 8:
                System.out.println("NO HAY ESPACIO LIBRE ADYACENTE");
                throw new ImpossibleToSolveColisionException("NO HAY ESPACIO LIBRE ADYACENTE");
            }
            key = new PosicionKey(x, y);
            cont++;

        }
        return new ObjetoMovil(idObjeto, instante, x, y);
    }

    public class ImpossibleToSolveColisionException extends Exception {
        public ImpossibleToSolveColisionException() {
            super();
        }

        public ImpossibleToSolveColisionException(final String message) {
            super(message);
        }

        public ImpossibleToSolveColisionException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public ImpossibleToSolveColisionException(final Throwable cause) {
            super(cause);
        }
    }

}
