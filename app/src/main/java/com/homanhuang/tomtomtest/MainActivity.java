package com.homanhuang.tomtomtest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.MapConstants;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.TomtomMapCallback;
import com.tomtom.online.sdk.map.TomtomMapCallback.OnMarkerClickListener;
import com.tomtom.online.sdk.map.model.MapTilesType;
import com.tomtom.online.sdk.map.model.MapTrafficType;

import static com.tomtom.online.sdk.map.MapConstants.DEFAULT_ZOOM_LEVEL;

public class MainActivity extends AppCompatActivity {

    /* Log tag and shortcut */
    final static String TAG = "MYLOG TOMTOM";
    public static void ltag(String message) {
        Log.i(TAG, message);
    }
    /* Toast shortcut */
    public static void msg(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void hideKeyboard(Context context, EditText mEditText) {
    	InputMethodManager keyboard = (InputMethodManager)getSystemService(context.INPUT_METHOD_SERVICE);
    	// hide keyboard after input
    	keyboard.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private MapFragment mapFragment;
    private TomtomMap tomtomMap;
    Thread markerThread;

    //Map balloon
    SimpleMarkerBalloon balloon;

    private OnMapReadyCallback onMapReadyCallback =
        new OnMapReadyCallback() {
            @Override
            public void onMapReady(TomtomMap map) {
            //Map is ready here
            tomtomMap = map;
            tomtomMap.setMyLocationEnabled(true);
            tomtomMap.getUiSettings().getCurrentLocationView().show();

            ltag("Map is ready");

            //Map events
            tomtomMap.addOnMapClickListener(onMapClickListener);
            tomtomMap.addOnMapLongClickListener(onMapLongClickListener);
            tomtomMap.addOnMapViewPortChangedListener(onMapViewPortChangedListener);

            //marker
            OnMarkerClickListener onMarkerClickListener =
                new OnMarkerClickListener() {
                    @Override
                    public void onMarkerClick(Marker marker) {
                        ltag("Marker Clicked at ID: "+marker.getId()+", tag: "+marker.getTag().toString());

                        mMarker = marker;
                        openMarkerOption = false;
                        showMarkerOptionButton();
                        markerThread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2*60*1000);
                                } catch (InterruptedException e) {
                                    ltag(e.toString());
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideMarkerButtons(Choice.ALL);
                                    }
                                });
                            }
                        };
                        markerThread.start(); //start the thread
                    }
                };
            tomtomMap.addOnMarkerClickListener(onMarkerClickListener);
            }
        };

    static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 201; // any code you want.
    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                ltag("Permission is granted");
            } else {
                ltag("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    TextView titleTextView;

    //Map Tiles Buttons & layout
    View mapTilesInclude;
    LinearLayout vectorRasterLinearLayout;

    //Map Traffic Buttons & layout
    View mapTrafficeInclude;
    LinearLayout trafficLinearLayout;

    //Map center buttons & layout
    View centerInclude;
    ConstraintLayout centerConstraintLayout;
    FrameLayout laFrame;
    FrameLayout dcFrame;
    FrameLayout nyFrame;
    FloatingActionButton laFButton;
    FloatingActionButton dcFButton;
    FloatingActionButton nyFButton;
    LatLng DC;
    LatLng LA;
    LatLng NY;
    Button compassButton;
    Button myLocationButton;

    //Map perspective
    View perspectiveInclude;
    LinearLayout mapPerpsLinearLayout;

    //Map marker
    Marker mMarker;
    View markerInclude;
    ConstraintLayout markerConstraintLayout;
    LinearLayout tagLinearLayout;
    FrameLayout markerFrame;
    FrameLayout tagFrame;
    FrameLayout deleteFrame;
    Button clearButton;
    EditText tagEditText;

    public void vectorView(View v) {
        //setTiles
        if (tomtomMap != null) {
            tomtomMap.getUiSettings().setMapTilesType(MapTilesType.VECTOR);
            titleTextView.setText("Vecter Tiles");
        }
    }

    public void rasterView(View v) {
        //setTiles
        if (tomtomMap != null) {
            tomtomMap.getUiSettings().setMapTilesType(MapTilesType.RASTER);
            titleTextView.setText("Raster Tiles");
        }
    }

    public void showIncident(View v) {
        if (tomtomMap != null) {
            tomtomMap.getUiSettings().setMapTrafficType(MapTrafficType.TRAFFIC_INCIDENTS);
            titleTextView.setText("Show Traffic Incidents");
        }
    }

    public void showFlow(View v) {
        if (tomtomMap != null) {
            tomtomMap.getUiSettings().setMapTrafficType(MapTrafficType.TRAFFIC_FLOW);
            titleTextView.setText("Show Traffic Flow");
        }
    }

    public void hideTraffic(View v) {
        if (tomtomMap != null) {
            tomtomMap.getUiSettings().setMapTrafficType(MapTrafficType.TRAFFIC_NONE);
            titleTextView.setText("Hide Traffic");
        }
    }

    public void showCenter(View v) {
        /* show buttons */
        laFrame.setVisibility(View.VISIBLE);
        dcFrame.setVisibility(View.VISIBLE);
        nyFrame.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                /* hide buttons */
                        laFrame.setVisibility(View.INVISIBLE);
                        dcFrame.setVisibility(View.INVISIBLE);
                        nyFrame.setVisibility(View.INVISIBLE);
                    }
                });
            }
        };
        thread.start(); //start the thread
    }

    public void showLA(View v) {
        tomtomMap.centerOn(
                LA.getLatitude(),
                LA.getLongitude(),
                DEFAULT_ZOOM_LEVEL,
                MapConstants.ORIENTATION_NORTH);
        titleTextView.setText("Los Angeles");
    }

    public void showDC(View v) {
        tomtomMap.centerOn(
                DC.getLatitude(),
                DC.getLongitude(),
                DEFAULT_ZOOM_LEVEL,
                MapConstants.ORIENTATION_NORTH);
        titleTextView.setText("Washington DC");
    }

    public void showNY(View v) {
        tomtomMap.centerOn(
                NY.getLatitude(),
                NY.getLongitude(),
                DEFAULT_ZOOM_LEVEL,
                MapConstants.ORIENTATION_NORTH);
        titleTextView.setText("New York");
    }

    boolean compassSwitch = false; //default
    public void showCompass(View v) {
        ltag("Compass button is clicked.");
        if (compassSwitch) {
            tomtomMap.getUiSettings().getCompassView().hide();
            compassButton.setTextColor(Color.WHITE);
            compassSwitch = false;
        } else {
            tomtomMap.getUiSettings().getCompassView().show();
            compassButton.setTextColor(Color.RED);
            compassSwitch = true;
        }
    }

    boolean myLocationSwitch = true; //default
    public void showMyLocation(View v) {
        ltag("My Location button is clicked.");
        tomtomMap.setMyLocationEnabled(true);

        if (myLocationSwitch) {
            tomtomMap.getUiSettings().getCurrentLocationView().hide();
            myLocationButton.setTextColor(Color.WHITE);
            myLocationSwitch = false;
        } else {
            tomtomMap.getUiSettings().getCurrentLocationView().show();
            myLocationButton.setTextColor(Color.RED);
            myLocationSwitch = true;
        }
    }

    //map event: click
    TomtomMapCallback.OnMapClickListener onMapClickListener =
            new TomtomMapCallback.OnMapClickListener() {
                @Override
                public void onMapClick(double latitude, double longitude) {

                }
            };

    //map event: long click
    TomtomMapCallback.OnMapLongClickListener onMapLongClickListener =
            new TomtomMapCallback.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    addMarker(latLng, "");
                }
            };

    public void addMarker(LatLng position, String tag) {
        MarkerBuilder markerBuilder;
        if (tag == "") {
            markerBuilder = new MarkerBuilder(position)
                    .tag(tag)
                    .markerBalloon(new SimpleMarkerBalloon(position.toSimplerString()));
        } else {
            balloon = new SimpleMarkerBalloon(tag);
            markerBuilder = new MarkerBuilder(position)
                    .tag(tag)
                    .markerBalloon(balloon);
            balloon.setText(tag);
        }
        mMarker = tomtomMap.addMarker(markerBuilder);
    }

    //map event: panning
    TomtomMapCallback.MapViewPortChanged onMapViewPortChangedListener =
            new TomtomMapCallback.MapViewPortChanged() {
                @Override
                public void onMapViewPortChanged(float focalLatitude,
                                                 float focalLongitude,
                                                 int zoomLevel,
                                                 float perspectiveRatio,
                                                 float yawDegrees) {

                    hideMarkerButtons(Choice.ALL);
                }
            };

    public void view2D(View v) {
        tomtomMap.set2DMode();
    }

    public void view3D(View v) {
        tomtomMap.set3DMode();
    }

    //Marker section
    boolean openMarkerOption = false;
    private void showMarkerOptionButton() {
        markerInclude.setVisibility(View.VISIBLE);
        markerFrame.setVisibility(View.VISIBLE);
        if (openMarkerOption) {
            ltag("Show Marker Option.");
            tagFrame.setVisibility(View.VISIBLE);
            deleteFrame.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
            openMarkerOption = false;
        } else {
            ltag("Show Marker Option.");
            tagFrame.setVisibility(View.INVISIBLE);
            deleteFrame.setVisibility(View.INVISIBLE);
            clearButton.setVisibility(View.INVISIBLE);
            openMarkerOption = true;
        }
    }

    public void showOptions(View v) {
        ltag("show options button clicked.");
        showMarkerOptionButton();
    }

    public void modifyTag(View v) {
        hideMarkerButtons(Choice.ALL);
        //show Eidttext box
        tagLinearLayout.setVisibility(View.VISIBLE);
        tagEditText.setText(mMarker.getTag().toString());
    }

    public void removeMarker(View v) {
        ltag("Remove marker id: "+mMarker.getId());
        hideMarkerButtons(Choice.ALL);
        //remove the marker
        tomtomMap.removeMarker(mMarker);
    }

    public void clearAllMarkers(View v) {
        hideMarkerButtons(Choice.ALL);
        //remove all markers
        tomtomMap.removeMarkers();
    }

    enum Choice {ALL, THREE}
    private void hideMarkerButtons(Choice choice) {
        //hide everything
        if (choice == Choice.ALL) {
            markerFrame.setVisibility(View.INVISIBLE);
            //remove timer
            if (markerThread != null) markerThread.interrupt();
        }
        tagFrame.setVisibility(View.INVISIBLE);
        deleteFrame.setVisibility(View.INVISIBLE);
        clearButton.setVisibility(View.INVISIBLE);
    }

    public void updateTag(View v) {
        String tag = tagEditText.getText().toString();

        ltag("Update Tag: "+tag);

        //the Tomtom Marker doesn't have setTag(), setId()
        //So I have to delete one and create a new one
        LatLng x = mMarker.getPosition();

        tomtomMap.removeMarker(mMarker);
        addMarker(x, tag);

        ltag("New marker's tag: "+mMarker.getTag().toString());

        //Clear Text box
        tagEditText.setText("");
        //hide Text box
        tagLinearLayout.setVisibility(View.INVISIBLE);
        //hide keyboard
        hideKeyboard(getApplicationContext(), tagEditText);
    }
//

    /*
        OnCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check for permission
        requestPermissions();

        //map variable
        mapFragment = (MapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getAsyncMap(onMapReadyCallback);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        compassButton = (Button) findViewById(R.id.compassButton);

        // Map Tiles
        mapTilesInclude = findViewById(R.id.mapTilesInclude);
        vectorRasterLinearLayout = (LinearLayout) mapTilesInclude.findViewById(R.id.vectorRasterLinearLayout);

        // Map Traffic
        mapTrafficeInclude = findViewById(R.id.trafficInclude);
        trafficLinearLayout = (LinearLayout) mapTrafficeInclude.findViewById(R.id.trafficLinearLayout);

        //map center, my location and compass
        centerInclude = findViewById(R.id.centerInclude);
        centerConstraintLayout = (ConstraintLayout) centerInclude.findViewById(R.id.centerConstraintLayout);
        laFrame = (FrameLayout) centerInclude.findViewById(R.id.deleteFrame);
        dcFrame = (FrameLayout) centerInclude.findViewById(R.id.dcFrame);
        nyFrame = (FrameLayout) centerInclude.findViewById(R.id.tagFrame);
        laFButton = (FloatingActionButton) centerInclude.findViewById(R.id.laFButton);
        dcFButton = (FloatingActionButton) centerInclude.findViewById(R.id.dcFButton);
        nyFButton = (FloatingActionButton) centerInclude.findViewById(R.id.nyFButton);
        DC = new LatLng(38.889955, -77.009180);
        LA = new LatLng(34.054297, -118.242880);
        NY = new LatLng(40.758860, -73.985431);
        compassButton = (Button) centerInclude.findViewById(R.id.compassButton);
        myLocationButton = (Button) centerInclude.findViewById(R.id.myLocationButton);

        //Map perspective
        perspectiveInclude = findViewById(R.id.perspectiveInclude);
        mapPerpsLinearLayout = (LinearLayout) perspectiveInclude.findViewById(R.id.mapPerpsLinearLayout);

        //Map marker
        markerInclude = findViewById(R.id.markerInclude);
        markerConstraintLayout = (ConstraintLayout) markerInclude.findViewById(R.id.markerConstraintLayout);
        tagLinearLayout = (LinearLayout) markerInclude.findViewById(R.id.tagLinearLayout);
        markerFrame = (FrameLayout) markerInclude.findViewById(R.id.markerFrame);
        tagFrame = (FrameLayout) markerInclude.findViewById(R.id.tagFrame);
        deleteFrame = (FrameLayout) markerInclude.findViewById(R.id.deleteFrame);
        clearButton = (Button) markerInclude.findViewById(R.id.clearButton);
        tagEditText = (EditText) markerInclude.findViewById(R.id.tagEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //link xml to menu
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //set all include layouts invisible.
        mapTilesInclude.setVisibility(View.INVISIBLE);
        mapTrafficeInclude.setVisibility(View.INVISIBLE);
        centerInclude.setVisibility(View.INVISIBLE);
        perspectiveInclude.setVisibility(View.INVISIBLE);
        markerInclude.setVisibility(View.INVISIBLE);

        //check item
        switch (item.getItemId() ) {
            case R.id.mapTitles:
                mapTilesInclude.setVisibility(View.VISIBLE);
                return true;

            case R.id.aboutTest:
                titleTextView.setText("Test Tomtom SDK\nBy\nHoman Huang");
                return true;

            case R.id.mapTraffic:
                mapTrafficeInclude.setVisibility(View.VISIBLE);
                return true;

            case R.id.mapCenter:
                centerInclude.setVisibility(View.VISIBLE);
                return true;

            case R.id.mapPersective:
                perspectiveInclude.setVisibility(View.VISIBLE);
                return true;

            default:
                return false;

        }
    }
}
