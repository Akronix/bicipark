package akronix.es.biciparkmadrid;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.id;

public class ListFavouritesActivity extends AppCompatActivity implements RenameDialog.RenameDialogCallback{

    private DBAdapter mDBAdapter;
    private CursorAdapter cursorAdapter;
    @BindView(R.id.lvFavourites) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_favourites);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBAdapter = new DBAdapter(this);
        populateList();
    }

    private void populateList() {
        cursorAdapter = new FavouritesListingAdapter(
                this,
                mDBAdapter.getLocalFavouritedParkingsCursor()
            );

        listView.setAdapter(cursorAdapter);
    }

    public void showContextActionDialog(final FavouritedParking parking) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String [] contextActions = {
                getResources().getString(R.string.rename_favourite),
                getResources().getString(R.string.delete_favourite)
        };
        builder.setTitle(R.string.dialog_select_action)
                .setItems(R.array.favourites_context_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                renameFavourite(parking);
                                break;
                            case 1:
                                deleteFavourite(parking.getParkingId());
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void renameFavourite(final FavouritedParking parking) {
        DialogFragment dialog = RenameDialog.newInstance(parking.getId(), parking.getName());
        dialog.show(getFragmentManager(), "rename_dialog");
    }

    public void deleteFavourite(final long parkingId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Are you sure you want to delete favourite with id: %d?", parkingId));
        builder.setTitle("CONFIRM DELETION");

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Deleting " + parkingId, Toast.LENGTH_SHORT).show();
                mDBAdapter.deleteByParkingId(parkingId);
                cursorAdapter.changeCursor(mDBAdapter.getLocalFavouritedParkingsCursor());
            }
        });

        builder.create().show();
    }

    @Override
    public void doRename(long favouriteId, String newName) {
        if (this.mDBAdapter.renameFavourite(favouriteId, newName)) {
            cursorAdapter.changeCursor(mDBAdapter.getLocalFavouritedParkingsCursor());
            Toast.makeText(this, "Renamed successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ups...An unexpected error occurred when renaming", Toast.LENGTH_SHORT).show();
        }
    }
}
