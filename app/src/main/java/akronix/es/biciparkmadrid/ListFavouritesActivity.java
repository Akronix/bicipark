package akronix.es.biciparkmadrid;

import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        mDBAdapter = new DBAdapter(this);
        populateList();
    }

    private void populateList() {
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                mDBAdapter.getLocalFavouritedParkingsCursor(),
                new String[]{ "name" },
                new int[] {android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );

        listView.setAdapter(cursorAdapter);

    }
}
