package es.ramon.casares.proyecto.modelo.log;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MovimientoComprimido {

    private List<Integer> movimiento;

    public List<Integer> getMovimiento() {
        return this.movimiento;
    }

    public void setMovimiento(final List<Integer> movimiento) {
        this.movimiento = movimiento;
    }

    public MovimientoComprimido(final List<Integer> movimiento) {
        super();
        this.movimiento = movimiento;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MovimientoComprimido)) {
            return false;
        }
        final MovimientoComprimido castOther = (MovimientoComprimido) other;
        return new EqualsBuilder().append(this.movimiento, castOther.movimiento).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.movimiento).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("x", this.movimiento).toString();
    }
}
