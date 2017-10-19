package akronix.es.biciparkmadrid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by akronix on 18/10/17.
 */

final class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BiciparkDB";
    public static final String FAVOURITES_TABLE_NAME = "Favourites";
    private static final int DATABASE_VERSION = 1;

    public enum FAVOURITES_COLUMN_NAMES {
        _id, parking_id, name, img_uri;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public String getDatabaseName() { return DATABASE_NAME; }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String stmt = String.format("CREATE TABLE %s" +
                        "(%s INTEGER PRIMARY KEY ASC," +
                        " %s INTEGER NOT NULL UNIQUE," +
                        " %s TEXT NOT NULL," +
                        " %s TEXT" +
                        ")",
                FAVOURITES_TABLE_NAME,
                FAVOURITES_COLUMN_NAMES._id,
                FAVOURITES_COLUMN_NAMES.parking_id,
                FAVOURITES_COLUMN_NAMES.name,
                FAVOURITES_COLUMN_NAMES.img_uri
                );
        db.execSQL(stmt);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
