/**
 * K2Tree.java 04-jul-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.snapshot.k2tree;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class K2Tree.
 */
public class K2Tree implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1879911087111637327L;

    /** The t. */
    private List<Byte> t;

    /** The l. */
    private List<Byte> l;

    /** The permutacion. */
    private Permutation permutacion;

    public K2Tree() {
    }

    /**
     * Instantiates a new k2 tree.
     * 
     * @param k2Tree
     *            the k2 tree
     */
    public K2Tree(final K2Tree k2Tree) {
        this.t = k2Tree.getT();
        this.l = k2Tree.getL();
        this.permutacion = k2Tree.getPermutacion();

    }

    /**
     * Instantiates a new k2 tree.
     * 
     * @param t
     *            the t
     * @param l
     *            the l
     * @param permutacion
     *            the permutacion
     */
    public K2Tree(final List<Byte> t, final List<Byte> l, final Permutation permutacion) {
        super();
        this.t = t;
        this.l = l;
        this.permutacion = permutacion;
    }

    /**
     * Gets the t.
     * 
     * @return the t
     */
    public List<Byte> getT() {
        return this.t;
    }

    /**
     * Sets the t.
     * 
     * @param t
     *            the new t
     */
    public void setT(final List<Byte> t) {
        this.t = t;
    }

    /**
     * Gets the l.
     * 
     * @return the l
     */
    public List<Byte> getL() {
        return this.l;
    }

    /**
     * Sets the l.
     * 
     * @param tBits
     *            the new l
     */
    public void setL(final List<Byte> tBits) {
        this.l = tBits;
    }

    /**
     * Gets the permutacion.
     * 
     * @return the permutacion
     */
    public Permutation getPermutacion() {
        return this.permutacion;
    }

    /**
     * Sets the permutacion.
     * 
     * @param permutacion
     *            the new permutacion
     */
    public void setPermutacion(final Permutation permutacion) {
        this.permutacion = permutacion;
    }

}
