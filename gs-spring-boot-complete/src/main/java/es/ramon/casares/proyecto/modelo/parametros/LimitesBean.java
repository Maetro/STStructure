/**
 * LimitesBean.java 27-ago-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.modelo.parametros;

/**
 * The Class LimitesBean.
 */
public class LimitesBean {

    /** The limite. */
    private int limiteMovimiento;

    /** The id objeto. */
    private int numeroObjetos;

    public LimitesBean() {
    }

    /**
     * Instantiates a new limites bean.
     *
     * @param limite
     *            the limite
     * @param idObjeto
     *            the id objeto
     */
    public LimitesBean(final int limite, final int idObjeto) {
        super();
        this.limiteMovimiento = limite;
        this.numeroObjetos = idObjeto;
    }

    /**
     * Gets the limite.
     *
     * @return the limite
     */
    public int getLimiteMovimiento() {
        return this.limiteMovimiento;
    }

    /**
     * Sets the limite.
     *
     * @param limite
     *            the new limite
     */
    public void setLimiteMovimiento(final int limite) {
        this.limiteMovimiento = limite;
    }

    /**
     * Gets the id objeto.
     *
     * @return the id objeto
     */
    public int getNumeroObjetos() {
        return this.numeroObjetos;
    }

    /**
     * Sets the id objeto.
     *
     * @param idObjeto
     *            the new id objeto
     */
    public void setNumeroObjetos(final int idObjeto) {
        this.numeroObjetos = idObjeto;
    }

}
