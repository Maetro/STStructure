/**
 * Movimiento.java 11-ago-2016
 *
 * Copyright 2016 INDITEX.
 * Departamento de Sistemas
 */
package es.ramon.casares.proyecto.modelo.log;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Class Movimiento.
 * 
 * @author <a href="ramon-jose.casares@external.connectis-gs.es">Ramon Casares</a>
 */
public class Movimiento {

    private Integer x;
    private Integer y;

    public Integer getX() {
        return this.x;
    }

    public void setX(final Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return this.y;
    }

    public void setY(final Integer y) {
        this.y = y;
    }

    public Movimiento(final Integer x, final Integer y) {
        super();
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Movimiento)) {
            return false;
        }
        final Movimiento castOther = (Movimiento) other;
        return new EqualsBuilder().append(this.x, castOther.x).append(this.y, castOther.y).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.x).append(this.y).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("x", this.x).append("y", this.y).toString();
    }

}
