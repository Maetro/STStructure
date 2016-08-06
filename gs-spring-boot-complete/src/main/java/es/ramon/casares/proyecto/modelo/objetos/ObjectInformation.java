package es.ramon.casares.proyecto.modelo.objetos;

/*
 * Acumula información sobre un objeto particular para ayudarnos a descartar datos erroneos.
 */

public class ObjectInformation {

    private double lastSpeed;
    private double lastAngle;

    public ObjectInformation(final double lastSpeed, final double lastAngle) {

        this.lastSpeed = lastSpeed;
        this.lastAngle = lastAngle;
    }

    public double getLastSpeed() {
        return this.lastSpeed;
    }

    public void setLastSpeed(
            final double lastSpeed) {
        this.lastSpeed = lastSpeed;
    }

    public double getLastAngle() {
        return this.lastAngle;
    }

    public void setLastAngle(
            final double lastAngle) {
        this.lastAngle = lastAngle;
    }

}