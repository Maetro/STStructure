/**
 * PosicionInstanteObjeto.java 04-jul-2016
 */
package es.ramon.casares.proyecto.modelo.matrix;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Class PosicionInstanteObjeto.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class PosicionInstanteObjeto extends PosicionInstante {

    /** The id objeto. */
    protected Integer idObjeto;

    /**
     * Instancia un nuevo posicion instante objeto.
     * 
     * @param x
     *            x
     * @param y
     *            y
     * @param instante
     *            instante
     * @param idObjeto
     *            id objeto
     */
    public PosicionInstanteObjeto(final Integer x, final Integer y, final Integer instante, final Integer idObjeto) {
        super(x, y, instante);
        this.idObjeto = idObjeto;
    }

    /**
     * Obtiene id objeto.
     * 
     * @return id objeto
     */
    public Integer getIdObjeto() {
        return this.idObjeto;
    }

    /**
     * Establece id objeto.
     * 
     * @param idObjeto
     *            nuevo id objeto
     */
    public void setIdObjeto(final Integer idObjeto) {
        this.idObjeto = idObjeto;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PosicionInstanteObjeto)) {
            return false;
        }
        final PosicionInstanteObjeto castOther = (PosicionInstanteObjeto) other;
        return new EqualsBuilder().append(this.x, castOther.x).append(this.y, castOther.y)
                .append(this.instante, castOther.instante).append(this.idObjeto, castOther.idObjeto).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.x).append(this.y).append(this.instante).append(this.idObjeto)
                .toHashCode();
    }

}
