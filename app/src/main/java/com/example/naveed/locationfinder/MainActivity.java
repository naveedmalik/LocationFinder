package com.example.naveed.locationfinder;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.*;

public class MainActivity extends Activity implements OnMapReadyCallback
{
    static int count = 0;
    GoogleMap mGoogleMap;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    Polygon shape;
    int markerExist = 1;
    ImageView image;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (googleServicesAvailable())
        {
            setContentView(R.layout.activity_main);
            //v = findViewById(R.id.infoWindow);
            //image = (ImageButton) v.findViewById(R.id.image);
           /* image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getPicture(v);
                }
            });*/
            createMap();
        }


    }

    private void createMap()
    {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable()
    {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
        {
            return true;
        } else if (api.isUserResolvableError(isAvailable))
        {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else
        {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }



    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;

        if (mGoogleMap != null)
        {


            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
            {
                @Override
                public void onMapLongClick(LatLng latLng)
                {

                    if (marker != null)
                    {
                        marker.remove();

                    }

                    MainActivity.this.setMarker("Local", latLng.latitude, latLng.longitude);


                }
            });

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
            {
                @Override
                public void onMarkerDragStart(Marker marker)
                {

                }

                @Override
                public void onMarkerDrag(Marker marker)
                {

                }

                @Override
                public void onMarkerDragEnd(Marker marker)
                {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    LatLng latLng = marker.getPosition();
                    try
                    {
                        List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        Address address = list.get(0);
                        marker.setTitle(address.getLocality());
                        marker.showInfoWindow();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
            {

                @Override
                public View getInfoWindow(Marker marker)
                {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker)
                {
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView lat = (TextView) view.findViewById(R.id.lat);
                    TextView lng = (TextView) view.findViewById(R.id.lng);
                    TextView position = (TextView) view.findViewById(R.id.position);

                    LatLng latLng = marker.getPosition();
                    position.setText("Location:" + marker.getTitle());
                    lat.setText("Latitude:" + latLng.latitude);
                    lng.setText("Longitude:" + latLng.longitude);
                    return view;
                }
            });
        }

        setLocationButton();


        //goToLocation(39.008224, -76.8984527);
    }

    private void setLocationButton()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            return;
        } else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 101:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

                    } else
                    {
                        Toast.makeText(this, "Need Location", Toast.LENGTH_LONG);
                    }

                }
            }
        }
    }

    private void goToLocation(double lat, double lng)
    {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
        mGoogleMap.moveCamera(update);
    }

    Marker marker;

    public void geoLocate(View view) throws IOException
    {
        EditText user_location = (EditText) findViewById(R.id.location);
        String location = user_location.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = geocoder.getFromLocationName(location, 1);
        Address address = list.get(0);
        String locality = address.getLocality();

        double lat = address.getLatitude();
        double lng = address.getLongitude();


        if (marker != null)
        {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat, lng));

        marker = mGoogleMap.addMarker(markerOptions);
        goToLocation(lat, lng);
    }

    private void setMarker(String locality, double lat, double lng)
    {
        if (markers.size() == 5)
        {
            removeEverything();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat, lng));
        markers.add(mGoogleMap.addMarker(markerOptions));

        if (markers.size() == 5)
        {
            drawPolygon();
        }
    }

    private void drawPolygon()
    {
        PolygonOptions option = new PolygonOptions();
        option.fillColor(0x330000FF)
                .strokeWidth(3)
                .strokeColor(Color.BLUE);

        for (int i = 0; i < 5; i++)
        {
            option.add(markers.get(i).getPosition());
        }
        shape = mGoogleMap.addPolygon(option);
        markerExist = 1;
    }

    private void removeEverything()
    {
        for (Marker marker : markers)
        {
            marker.remove();
        }
        markers.clear();
        shape.remove();
        shape = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.none:
            {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            }

            case R.id.normal:
            {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            }

            case R.id.terrain:
            {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            }

            case R.id.satellite:
            {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            }

            case R.id.hybrid:
            {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPicture(View view)
    {
        //ViewGroup viewGroup = (ViewGroup) findViewById(R.id.infoWindow);
        //ImageButton image_btn = (ImageButton) viewGroup.findViewById(R.id.image);
        String option[] = {"Open Camera","Open Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick An Option");
        builder.setItems(option, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case 0:
                    {
                        openCamera();
                        break;
                    }
                    case 1:
                    {
                        selectImageFromGallery();
                        break;
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectImageFromGallery()
    {
        Intent image_selection = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(image_selection,2);
    }
    private void openCamera()
    {
        File path = getFile();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT,path);
        startActivityForResult(camera,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1)
        {
            WindowInfo window = new WindowInfo();
            Toast.makeText(this,"For Camera",Toast.LENGTH_LONG).show();
            String path = "MapImages/map_image"+ count++ +".jpg";
            View v = getLayoutInflater().inflate(R.layout.info_window,null);
            image = (ImageView) v.findViewById(R.id.image);
            //window.image.setImageDrawable(Drawable.createFromPath(path));
            image.setImageDrawable(Drawable.createFromPath(path));
        }else
            if (requestCode == 2)
            {
                View v = getLayoutInflater().inflate(R.layout.info_window,null);
                image = (ImageView) v.findViewById(R.id.image);
                Toast.makeText(this,"For Gallery",Toast.LENGTH_LONG).show();
                //WindowInfo window = new WindowInfo();
                Uri image_uri = data.getData();
                image.setImageURI(image_uri);
            }
    }

    private File getFile()
    {
        File folder = new File("MapImages");
        if(!folder.exists())
        {
            folder.mkdir();
        }
        File image_path = new File(folder, System.currentTimeMillis()+"map_image.jpg");
        return image_path;
    }

    public void callGetPicture(View view)
    {
        WindowInfo window = new WindowInfo();
        window.getPicture(view);
    }
}
