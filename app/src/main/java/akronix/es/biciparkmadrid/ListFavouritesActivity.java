package akronix.es.biciparkmadrid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
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

public class ListFavouritesActivity extends AppCompatActivity {

    private DBAdapter mDBAdapter;
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
        CursorAdapter cursorAdapter = new FavouritesListingAdapter(
                this,
                mDBAdapter.getLocalFavouritedParkingsCursor()
            );

        listView.setAdapter(cursorAdapter);
    }

    public void showContextActionDialog(long id) {
        final long favouriteId = id;

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
                                renameFavourite(favouriteId);
                                break;
                            case 1:
                                deleteFavourite(favouriteId);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void renameFavourite(long favouriteId) {
        Toast.makeText(this, "Renaming " + favouriteId, Toast.LENGTH_SHORT).show();
    }

    public void deleteFavourite(long favouriteId) {
        Toast.makeText(this, "Deleting " + favouriteId, Toast.LENGTH_SHORT).show();
    }
}
