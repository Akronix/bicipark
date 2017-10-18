package akronix.es.biciparkmadrid;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

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
}
