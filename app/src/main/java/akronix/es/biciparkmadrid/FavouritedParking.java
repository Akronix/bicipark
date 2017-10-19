package akronix.es.biciparkmadrid;

import android.net.Uri;

/**
 * Created by akronix on 18/10/17.
 */

public class FavouritedParking {

    public long getId() {
        return id;
    }

    private long id;
    private long parkingId;
    private String name;
    private Uri imgUri;

    public long getParkingId() {
        return parkingId;
    }

    public String getName() {
        return name;
    }

    public FavouritedParking(long parkingId, String name) {
        this.parkingId = parkingId;
        this.name = name;
    }
    public FavouritedParking(long id, long parkingId, String name) {
        this(parkingId, name);
        this.id = id;
    }

    @Override
    public String toString() {
        return "FavouritedParking{" +
                "parkingId=" + parkingId +
                ", name='" + name + '\'' +
                '}';
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }
}
