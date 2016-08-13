package es.ramon.casares.proyecto.modelo.objetos;

public class ObjetoMovil {

    private int id;
    private int instant;
    private int y;
    private int x;
    private boolean enMovimiento = false;

    public ObjetoMovil() {

    }

    public ObjetoMovil(final int id, final int instant, final int x, final int y) {
        this.id = id;
        this.instant = instant;
        this.y = y;
        this.x = x;
    }

    public int getId() {
        return this.id;
    }

    public void setId(
            final int id) {
        this.id = id;
    }

    public int getX() {
        return this.x;
    }

    public void setX(
            final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(
            final int y) {
        this.y = y;
    }

    public int getInstant() {
        return this.instant;
    }

    public void setInstant(
            final int instant) {
        this.instant = instant;
    }

    public boolean isEnMovimiento() {
        return this.enMovimiento;
    }

    public void setEnMovimiento(final boolean enMovimiento) {
        this.enMovimiento = enMovimiento;
    }
}
