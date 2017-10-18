package akronix.es.biciparkmadrid;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by akronix on 18/10/17.
 */

public final class FavouritesListingAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private static int layout = R.layout.favourite_record;
    private static String[] from = {
            DBHelper.FAVOURITES_COLUMN_NAMES.parking_id.toString(),
            DBHelper.FAVOURITES_COLUMN_NAMES.name.toString(),
            DBHelper.FAVOURITES_COLUMN_NAMES._id.toString(),
    };
    private Context mContext;
    private ListFavouritesActivity mActivity;


    public FavouritesListingAdapter(ListFavouritesActivity activity, Cursor c) {
        super(activity, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.mActivity = activity;
        this.mContext = activity;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, final ViewGroup parent) {
        View view = mInflater.inflate(layout, parent, false);
        return view;
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        String name = cursor.getString(cursor.getColumnIndex(from[1]));
        final long parkingId = cursor.getLong(cursor.getColumnIndex(from[0]));
        final long favouritedId = cursor.getLong(cursor.getColumnIndex(from[2]));

        tvName.setText(name);
        final FavouritedParking parking = new FavouritedParking(favouritedId, parkingId, name);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mActivity.showContextActionDialog(parking);
                return true;
            }
        });
    }

}
