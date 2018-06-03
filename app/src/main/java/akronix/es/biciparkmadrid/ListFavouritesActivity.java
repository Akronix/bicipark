package akronix.es.biciparkmadrid;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.id;

public class ListFavouritesActivity extends AppCompatActivity implements RenameDialog.RenameDialogCallback{

    public static final int TAKE_PICTURE = 1;
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
        builder.setMessage(R.string.dialog_delete_favourite_message);
        builder.setTitle(R.string.dialog_delete_favourite_title);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;// Log.d(MainActivity.LOG_TAG, "Deleting " + parkingId);
                if (mDBAdapter.deleteByParkingId(parkingId)) {
                    Toast.makeText(getApplicationContext(), R.string.delete_successful, Toast.LENGTH_SHORT).show();
                    cursorAdapter.changeCursor(mDBAdapter.getLocalFavouritedParkingsCursor());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.delete_error, Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.create().show();
    }

    @Override
    public void doRename(long favouriteId, String newName) {
        if (this.mDBAdapter.renameFavourite(favouriteId, newName)) {
            cursorAdapter.changeCursor(mDBAdapter.getLocalFavouritedParkingsCursor());
            Toast.makeText(this, R.string.rename_successful, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.rename_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = Uri.parse(data.toUri(0));
                    ImageView ivCover = (ImageView) findViewById(R.id.ivCover);
                    ivCover.setImageURI(selectedImage);
                }
        }
    }

}
