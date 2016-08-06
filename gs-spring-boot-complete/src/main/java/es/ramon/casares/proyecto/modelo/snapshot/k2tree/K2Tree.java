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

    /** The ids objetos. */
    private List<Short> idsObjetos;

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
        this.idsObjetos = k2Tree.getIdsObjetos();

    }

    /**
     * Instantiates a new k2 tree.
     * 
     * @param t
     *            the t
     * @param l
     *            the l
     * @param idsObjetos
     *            ids objetos
     */
    public K2Tree(final List<Byte> t, final List<Byte> l, final List<Short> idsObjetos) {
        super();
        this.t = t;
        this.l = l;
        this.idsObjetos = idsObjetos;
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
     * Obtiene ids objetos.
     * 
     * @return ids objetos
     */
    public List<Short> getIdsObjetos() {
        return this.idsObjetos;
    }

    /**
     * Establece ids objetos.
     * 
     * @param idsObjetos
     *            nuevo ids objetos
     */
    public void setIdsObjetos(final List<Short> idsObjetos) {
        this.idsObjetos = idsObjetos;
    }

}
