/**
 * ObjetoMovil.java 05-sep-2016
 *
 * Copyright 2016 RAMON CASARES.
 * @author Ramon.Casares.Porto@gmail.com
 */
package es.ramon.casares.proyecto.util.objetos;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class InformacionInstanteObjeto.
 */
public class ObjetoMovil extends Posicion implements Serializable {

    private static final long serialVersionUID = 7776211319563188088L;

    /** The objeto id. */
    private Integer objetoId;

    /** The instante. */
    private Integer instante;

    /**
     * Instantiates a new informacion instante objeto.
     */
    public ObjetoMovil(final Integer posicionX, final Integer posicionY) {
        super(posicionX, posicionY);
    }

    public ObjetoMovil(final Integer objetoId, final Integer instante, final Integer posicionX,
            final Integer posicionY) {
        super(posicionX, posicionY);
        this.objetoId = objetoId;
        this.instante = instante;
    }

    /**
     * Gets the objeto id.
     *
     * @return the objeto id
     */
    public Integer getObjetoId() {
        return this.objetoId;
    }

    /**
     * Sets the objeto id.
     *
     * @param objetoId
     *            the new objeto id
     */
    public void setObjetoId(final Integer objetoId) {
        this.objetoId = objetoId;
    }

    /**
     * Gets the instante.
     *
     * @return the instante
     */
    public Integer getInstante() {
        return this.instante;
    }

    /**
     * Sets the instante.
     *
     * @param instante
     *            the new instante
     */
    public void setInstante(final Integer instante) {
        this.instante = instante;
    }

}
