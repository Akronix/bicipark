package akronix.es.biciparkmadrid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {

    /*** Main members ***/
    private GoogleMap mMap;
    private KmlLayer mKmlLayer;

    /*** View members ***/
    MenuItem favActionButton;


    /*** Location members: ***/
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // A default location (Puerta del Sol, Madrid, Spain) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(40.416932, -3.703317);
    private static final int DEFAULT_ZOOM = 17;
    private boolean mRequestingLocationUpdates = false;

    /*** Location update members ***/
    private static final int UPDATE_INTERVAL = 10 * 1000;
    private static final int FASTEST_UPDATE_INTERVAL = 2 * 1000;
    /* Metadata about updates we want to receive */
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;


    /*** Other members ***/
    public static final String LOG_TAG = "BICIPARK";
    private Set<Long> mFavourites;
    private long mSelectedParkingId;
    private DBAdapter mDBAdapter;
    private Uri mSelectedParkingImgUri = null;

    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Verify play services is active and up to date
        int resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                Log.i(LOG_TAG, "Google Play Services is ready to go!");
                break;
            default:
                showPlayServicesError(resultCode);
                return;
        }

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        initLocationRequest();
        initLocationCallback();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_directMe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mDBAdapter = new DBAdapter(this);
        initFavouritesCollection();

    }

    private void initFavouritesCollection() {
        mDBAdapter.registerObserver(new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                mFavourites = mDBAdapter.getLocalFavouritedParkingsIds();
                for (long id : mFavourites)
                    Log.d(LOG_TAG, id + ", ");
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        mFavourites = mDBAdapter.getLocalFavouritedParkingsIds();
        for (long id : mFavourites)
            Log.d(LOG_TAG, id + ", ");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Disable updates when we are not in the foreground
        if (mRequestingLocationUpdates)
            stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBAdapter.finalize();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void initLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastKnownLocation = location;
                }
            }

            ;
        };
    }


    /*
     * When Play Services is missing or at the wrong version, the client
     * library will assist with a dialog to help the user update.
     */
    private void showPlayServicesError(int errorCode) {
        GoogleApiAvailability.getInstance()
                .showErrorDialogFragment(this, errorCode, 10,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
    }


    /*** Location methods ***/

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
                updateLocationUI();
                getDeviceLocation();
            }
        }
    }


    private void updateLocationUI() {
        if (mMap == null) {
            Log.d(LOG_TAG, "GMap is null :O");
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                Log.i(LOG_TAG, "Enabling location button");
                mMap.setMyLocationEnabled(true);
            } else {
                Log.i(LOG_TAG, "Disabling location button");
                mMap.setMyLocationEnabled(false);
            }
        } catch (SecurityException e)  {
            Log.e(LOG_TAG, String.format("Exception: %s", e.getMessage()));
        }
    }


    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            centerCameraOnLocation(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()));
                        } else {
                            Log.d(LOG_TAG, "Current location is null. Using defaults.");
                            //Log.e(LOG_TAG, String.format("Exception: %s", task.getException()));
                            centerCameraOnLocation(mDefaultLocation);
                        }
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, R.string.message_default_position, Toast.LENGTH_LONG).show();
                centerCameraOnLocation(mDefaultLocation);
            }
        } catch(SecurityException e)  {
            Log.e(LOG_TAG, String.format("Exception: %s", e.getMessage()));
        }
    }


    private void centerCameraOnLocation(LatLng location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set padding to map controls like my-location button or map toolbar
        int topPaddingInDp = 60;
        /* dp => px; */
        int topPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topPaddingInDp, displayMetrics);
        mMap.setPadding(0, topPadding, 0, 0);

        // Enable get directions and show in gMaps toolbar (when a marker is pressed) */
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Prompt user for permission.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();
        mRequestingLocationUpdates = true;
        startLocationUpdates();

        // Add parking points to map
        try {
            mKmlLayer = new KmlLayer(mMap, R.raw.upstream, getApplicationContext());
            mKmlLayer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Set a listener for geometry clicked events.
        mKmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                mSelectedParkingId = Long.parseLong(feature.getProperty("name"));
                mSelectedParkingImgUri = parseImgUri(feature.getProperty("description"));
                showFavButton(mSelectedParkingId);
                Log.i("KmlClick", "Feature clicked: " + feature.getProperty("name"));
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mSelectedParkingId = 0;
                hideFavButton();
            }
        });

    }

    private Uri parseImgUri(String description) {
        Log.d(LOG_TAG, description);
        //Pattern p = Pattern.compile("src=(.*)");
        Pattern p = Pattern.compile("\\<div");
        //Pattern p = Pattern.compile("/<img[^>]+src=\"([^\"\\s]+)\"/>/g");
        Matcher m = p.matcher(description);
        boolean b = m.matches();
        if (b) Log.d(LOG_TAG, "img found");
        else Log.d(LOG_TAG, "img not found");
        return null;
    }

    private void hideFavButton() {
        favActionButton.setVisible(false);
    }


    private void showFavButton(long parkingId) {
        if (mFavourites.contains(parkingId))
            favActionButton.setIcon(android.R.drawable.btn_star_big_on);
        else {
            favActionButton.setIcon(android.R.drawable.btn_star_big_off);
        }
        favActionButton.setVisible(true);
    }


    protected void initLocationRequest() {
        mLocationRequest = LocationRequest.create()
                //Set the required accuracy level
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //Set the desired (inexact) frequency of location updates
                .setInterval(UPDATE_INTERVAL)
                //Throttle the max rate of update requests
                .setFastestInterval(FASTEST_UPDATE_INTERVAL);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        favActionButton = menu.findItem(R.id.action_fav);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fav) {
            toggleFav(mSelectedParkingId);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFav(long mSelectedParkingId) {
        if (mFavourites.contains(mSelectedParkingId)) {
            if (mDBAdapter.deleteByParkingId(mSelectedParkingId)) {
                mFavourites.remove(mSelectedParkingId);
                Toast.makeText(this, R.string.action_fav_unmarked_message, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, String.format("Unmarked parking %d as favourite", mSelectedParkingId));
                favActionButton.setIcon(android.R.drawable.btn_star_big_off);
            } else {
                Log.e(LOG_TAG, "Trying to delete parking " + mSelectedParkingId + " from db, but it failed.");
            }

        } else {
            FavouritedParking parking = new FavouritedParking(
                    mSelectedParkingId,
                    String.valueOf(mSelectedParkingId));
            if (mSelectedParkingImgUri != null) parking.setImgUri(mSelectedParkingImgUri);
            if (mDBAdapter.insert(parking)) {
                mFavourites.add(mSelectedParkingId);
                Toast.makeText(this, R.string.action_fav_marked_message, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, String.format("Marked parking %d as favourite", mSelectedParkingId));
                favActionButton.setIcon(android.R.drawable.btn_star_big_on);
            } else {
                Log.e(LOG_TAG, "Trying to delete parking " + mSelectedParkingId + " from db, but it failed.");
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            // Already here, do nothing
        } else if (id == R.id.nav_favourites) {
            Intent intent = new Intent(this, ListFavouritesActivity.class);
            startActivity(intent);
            /*
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Here settings show be displayed", Toast.LENGTH_SHORT).show();
            */
        } else if (id == R.id.nav_share) {
            if (mLastKnownLocation != null) {
                Log.i(LOG_TAG, "Location " + mLastKnownLocation.toString());


                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.intent_share_location_subject));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, String.format("%s: %s,%s",
                        getResources().getString(R.string.intent_share_location_message),
                        mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.intent_share_chooser)));

                /*
                Intent locationIntent = new Intent(Intent.ACTION_VIEW);
                locationIntent.setData(Uri.parse(String.format("geo: %s,%s",
                        mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude())));
                startActivity(locationIntent);
                */

            } else {
                Toast.makeText(this, R.string.unavaible_location_message, Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
