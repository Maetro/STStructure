/**
 * MatrixOfPositions.java 27-jul-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.matrix;

import es.ramon.casares.proyecto.util.objetos.ObjetoMovil;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

/**
 * The Class MatrixOfPositions.
 */
public class MatrizDePosiciones {

    /** The matriz. */
    private LinkedSparseMatrix matriz;

    private int numCeldas;

    /**
     * Instantiates a new matrix of positions.
     */
    public MatrizDePosiciones(final int limit) {
        this.matriz = new LinkedSparseMatrix(limit, limit);
        this.numCeldas = limit;
    }

    /**
     * Obtiene num celdas.
     *
     * @return num celdas
     */
    public int getNumCeldas() {
        return this.numCeldas;
    }

    /**
     * Establece num celdas.
     *
     * @param numCeldas
     *            nuevo num celdas
     */
    public void setNumCeldas(final int numCeldas) {
        this.numCeldas = numCeldas;
    }

    /**
     * Gets the matriz.
     *
     * @return the matriz
     */
    public LinkedSparseMatrix getMatriz() {
        return this.matriz;
    }

    /**
     * Sets the matriz.
     *
     * @param matriz
     *            the new matriz
     */
    public void setMatriz(final LinkedSparseMatrix matriz) {
        this.matriz = matriz;
    }

    /**
     * Instantiates a new matrix of positions.
     *
     * @param matriz
     *            the matriz
     */
    public MatrizDePosiciones(final LinkedSparseMatrix matriz) {
        super();
        this.matriz = matriz;
    }

    private static int round(final double num, final int multipleOf) {

        return (int) (Math.ceil(num / multipleOf) * multipleOf);

    }

    /**
     * Anadir objeto a posicion.
     *
     * @param idObjeto
     *            the id objeto
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void anadirObjetoAPosicion(final Integer idObjeto, final Integer x, final Integer y) {
        this.matriz.set(y, x, idObjeto.doubleValue());
    }

    /**
     * Anadir objeto a posicion.
     *
     * @param informacionInstanteObjeto
     *            the informacion instante objeto
     */
    public void anadirObjetoAPosicion(final ObjetoMovil informacionInstanteObjeto) {
        this.matriz.set(informacionInstanteObjeto.getPosicionX(),
                informacionInstanteObjeto.getPosicionY(), informacionInstanteObjeto.getObjetoId().doubleValue());

    }
}
