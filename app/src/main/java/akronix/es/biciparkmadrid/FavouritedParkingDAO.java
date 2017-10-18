package akronix.es.biciparkmadrid;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.util.SortedList;

import java.util.List;

/**
 * Created by akronix on 18/10/17.
 */

public class FavouritedParkingDAO {

    private SQLiteDatabase db;

    public boolean insert( FavouritedParking parking) {

    }

    public boolean delete( long parkingId) {

    }

    public SortedList<FavouritedParking> getLocalFavouritedParkings() {
        db.

    }

    public SortedList<Long> getLocalFavouritedParkingsIds() {

    }

     /*
    public boolean insert( List<FavouritedParking> parkings) {
        try
        {
            db.beginTransaction();

            for each record in the list
            {
                do_some_processing();

                if (line represent a valid entry)
                {
                    db.insert(SOME_TABLE, null, SOME_VALUE);
                }

                some_other_processing();
            }

            db.setTransactionSuccessful();
        }
        catch (SQLException e) {}
        finally
        {
            db.endTransaction();
        }
    }
*/

}
