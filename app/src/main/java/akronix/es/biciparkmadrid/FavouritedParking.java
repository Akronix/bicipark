package akronix.es.biciparkmadrid;

/**
 * Created by akronix on 18/10/17.
 */

public class FavouritedParking {

    private long parkingId;
    private String name;

    public FavouritedParking(int parkingId, String name) {
        this.parkingId = parkingId;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FavouritedParking{" +
                "parkingId=" + parkingId +
                ", name='" + name + '\'' +
                '}';
    }

}
