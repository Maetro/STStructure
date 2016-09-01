package es.ramon.casares.proyecto.modelo.objetos;

/*
 * Nos permite ya generar las celdas a partir de los datos normalizados a 7 decimales.
 */
public class Point {

    private int instant;
    private int y;
    private int x;
    private int tamanoCeldas;

    public Point(final int instant, final int y, final int x, final int tamanoCeldas) { // y -> latitud x ->
        // longitud
        this.instant = instant;
        this.y = ((int) Math.floor(y / tamanoCeldas)); // El punto 0 esta reservado
        this.x = ((int) Math.floor(x / tamanoCeldas));
    }

    public int getX() {
        return this.x;
    }

    public void setX(
            final int x) {
        this.x = (int) Math.floor(x / this.tamanoCeldas);
    }

    public int getY() {
        return this.y;
    }

    public void setY(
            final int y) {
        this.y = (int) Math.floor(y / this.tamanoCeldas);
    }

    public int getInstant() {
        return this.instant;
    }

    public void setInstant(
            final int instant) {
        this.instant = instant;
    }
}
