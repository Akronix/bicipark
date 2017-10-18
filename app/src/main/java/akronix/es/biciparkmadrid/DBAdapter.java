package akronix.es.biciparkmadrid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.util.SortedList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by cice on 18/10/17.
 */

public final class DBAdapter {

    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private Context mContext;

    public DBAdapter (Context context) {
        this.mContext = context;
        mDBHelper = new DBHelper(context);
    }

    public boolean insert (FavouritedParking parking) {
        this.db = mDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(
                DBHelper.FAVOURITES_COLUMN_NAMES.parking_id.toString(),
                parking.getParkingId());
        cv.put(
                DBHelper.FAVOURITES_COLUMN_NAMES.name.toString(),
                parking.getName());
        long ret = db.insertOrThrow(
                DBHelper.FAVOURITES_TABLE_NAME,
                null,
                cv);


        return (ret != -1); // -1 means no row inserted
    }

    public boolean deletebyId (long id) {
        db = mDBHelper.getWritableDatabase();
        String[] args = {String.valueOf(id)};
        int ret = db.delete(DBHelper.FAVOURITES_TABLE_NAME, "_id = ?", args);
        return (ret != 0); // 0 means no row affected
    }

    public boolean deleteByParkingId (long parkingId) {
        db = mDBHelper.getWritableDatabase();
        String[] args = {String.valueOf(parkingId)};
        String whereClause = String.format(" %s =  ?", DBHelper.FAVOURITES_COLUMN_NAMES.parking_id);
        int ret = db.delete(DBHelper.FAVOURITES_TABLE_NAME, whereClause, args);
        return (ret != 0); // 0 means no row affected
    }

    public Cursor getLocalFavouritedParkingsCursor() {
        this.db = mDBHelper.getReadableDatabase();
        Cursor c = db.query(DBHelper.FAVOURITES_TABLE_NAME, null, null, null, null, null, null);
        return c;

    }

    //TOIMPROVE with a specific SELECT _id ORDERBY _id ASC; query
    public Set<Long> getLocalFavouritedParkingsIds() {
        Cursor c = this.getLocalFavouritedParkingsCursor();
        Set parkings = new HashSet(c.getCount());
        while (c.moveToNext()){
            parkings.add(c.getLong(DBHelper.FAVOURITES_COLUMN_NAMES._id.ordinal()));
        }
        return parkings;

    }

    public void finalize() {
        if(null != db)
            db.close();
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
