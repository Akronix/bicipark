package akronix.es.biciparkmadrid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    public void callForHelp(View view) {
//        Intent callIntent = new Intent(Intent.ACTION_DIAL);
//        callIntent.setData(Uri.parse("tel:" + phone));
//        try {
//            startActivity(callIntent);
//        } catch (Exception e) {
//            Log.e(MainActivity.LOG_TAG, "Failed to invoke call", e);
//        }
//    }

}
