package es.ramon.casares.proyecto.util;

public class ControladorHelper {

    // calculate haversine distance for linear distance
    /**
     * Haversine_km.
     * 
     * @param lat1
     *            lat1
     * @param long1
     *            long1
     * @param lat2
     *            lat2
     * @param long2
     *            long2
     * @return the double
     */
    public static double haversine_km(final double lat1, final double long1, final double lat2, final double long2)
    {
        final double d2r = Math.PI / 180.0;
        final double dlong = (long2 - long1) * d2r;
        final double dlat = (lat2 - lat1) * d2r;
        final double a = Math.pow(Math.sin(dlat / 2.0), 2)
                + (Math.cos(lat1 * d2r) * Math.cos(lat2 * d2r) * Math.pow(Math.sin(dlong / 2.0), 2));
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final double d = 6367 * c;

        return d;
    }

}
