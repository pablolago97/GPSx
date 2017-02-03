package com.example.plago_bergonpazos.gpsx;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    public static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private static double x = 42.236323;
    private static double y = -8.712158;
    private SupportMapFragment mFirstMapFragment;
    private Marker marker;
    private GoogleApiClient apiClient;

    // Ejemplo: Crear círculo con radio de 100m
    // y centro (42.236954,  -8.712717)
    LatLng center = new LatLng(42.236954, -8.712717);
    int radius = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obt  ain the SupportMapFragment and get notified when the map is ready to be used.
        mFirstMapFragment = BlankFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, mFirstMapFragment)
                .commit();
        //Registrar escucha onMapReadyCallback
        mFirstMapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Marcadores
        LatLng telepi = new LatLng(x, y);
        marker = mMap.addMarker(new MarkerOptions().position(telepi).title("Telepizza"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(telepi));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        marker.setVisible(false);


        // Controles UI
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));

        // Añadir círculo
        Circle circle = mMap.addCircle(circleOptions);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // ¿Permisos asignados?
            if (permissions.length > 0 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }


    }

    public void calcularDistancia() {
        /*String la= String.valueOf(lat1);
        String lo =String.valueOf(lng1);
        Toast.makeText(this, la+" "+lo, Toast.LENGTH_LONG).show();*/

        double earthRadius = 6372.795477598;

        double dLat = Math.toRadians(x - y);
        double dLng = Math.toRadians(x - y);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(y)) * Math.cos(Math.toRadians(x)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double distMet = dist * 1000;
        String distancia = String.valueOf(distMet);

        Toast.makeText(this, distancia + " metros ", Toast.LENGTH_LONG).show();

        if (distMet <= 20) {
            marker.setVisible(true);
        } else {
            marker.setVisible(false);
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        rest(lastLocation);
        calcularDistancia();

    }

    private void rest(Location loc) {

        if (loc != null) {
            x = loc.getLatitude();
            y = loc.getLongitude();
            //Tosat para saber longitud y latitud de mi posicion
            // Toast.makeText(this, String.valueOf(lat1)+" "+String.valueOf(lng1), Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "Latitud y Longitud desconocidas", Toast.LENGTH_LONG).show();

        }


    }
}

