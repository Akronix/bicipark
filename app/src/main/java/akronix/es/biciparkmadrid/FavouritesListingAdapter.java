package akronix.es.biciparkmadrid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
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
    private static String[] from = { "name" };
    private Context mContext;


    public FavouritesListingAdapter(Context context, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, final ViewGroup parent) {
        View view = mInflater.inflate(layout, parent, false);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogFragment dialog = ContextActionDialog.newInstance(
                        23);
                dialog.show(parent.getFragmentManager(), "dialog");
                return true;
            }
        });
        return view;
    }

    public static class ContextActionDialog extends DialogFragment {
        long favouriteId;

        static ContextActionDialog newInstance(int favouriteId) {
            ContextActionDialog dialog = new ContextActionDialog();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("favouriteId", favouriteId);
            dialog.setArguments(args);

            return dialog;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String [] contextActions = {
                    getResources().getString(R.string.rename_favourite),
                    getResources().getString(R.string.delete_favourite)
            };
            builder.setTitle(R.string.dialog_select_action)
                    .setItems(R.array.favourites_context_actions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    getActivity().renameFavourite(favouriteId);
                                    break;
                                case 1:
                                    getActivity().deleteFavourite(favouriteId);
                                    break;
                            }
                        }
                    });
            return builder.create();
        }
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(cursor.getString(cursor.getColumnIndex(from[0])));
    }

}
