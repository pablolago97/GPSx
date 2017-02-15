package com.example.plago_bergonpazos.gpsx;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener {
    public static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private static double x = 42.236323;
    private static double y = -8.712158;
    private SupportMapFragment mFirstMapFragment;
    private Marker marker;
    private GoogleApiClient apiClient;
    private static double MIx, MIy;
    private Circle circle;

    // Ejemplo: Crear círculo con radio de 100m
    // y centro (42.236954,  -8.712717)
    LatLng center = new LatLng(42.236954, -8.712717);
    int radius = 300;


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

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }


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

       //Circulo creado (parametros)
        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));

        // Añadir círculo
        circle = mMap.addCircle(circleOptions);

        //Escucha de 'Clicl'
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);


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
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }


    }





    public void calcularDistancia() {

        double earthRadius = 6372.795477598;

        double dLat = Math.toRadians(MIx - x);
        double dLng = Math.toRadians(MIy - y);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(y)) * Math.cos(Math.toRadians(x)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double distMet = dist * 1000;
        String distancia = String.valueOf((int)distMet);

        Toast.makeText(this, "Estás a "+distancia + " metros ", Toast.LENGTH_LONG).show();

        if (distMet <= 25) {
            marker.setVisible(true);
        } else {
            marker.setVisible(false);
        }

    }




    @Override
    public void onMapClick(LatLng latLng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        Posicion(lastLocation);
        calcularDistancia();

    }





    private void Posicion(Location loc) {

        if (loc != null) {
            MIx = loc.getLatitude();
            MIy = loc.getLongitude();
            //Tosat para saber longitud y latitud de mi posicion
            //Toast.makeText(this, String.valueOf(MIx) + " " + String.valueOf(MIy), Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "Latitud y Longitud desconocidas", Toast.LENGTH_LONG).show();

        }


    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            //Obtener lat y long de mi posicion y se lo pasamos a Posicion.
            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            Posicion(lastLocation);
        }
    }



        @Override
        public void onConnectionSuspended ( int i){

        }

        @Override
        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){

        }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent = new Intent(getBaseContext(), ScannerActivity.class);
        int code = 4545; // Esto puede ser cualquier código.
        startActivityForResult(intent, code);
    }
}




