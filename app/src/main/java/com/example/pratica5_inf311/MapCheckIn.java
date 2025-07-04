package com.example.pratica5_inf311;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.Menu;
import android.view.MenuItem;

public class MapCheckIn extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private double userLat, userLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_map);

        userLat = getIntent().getDoubleExtra("latitude", 0.0);
        userLong = getIntent().getDoubleExtra("longitude", 0.0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng userLocation = new LatLng(userLat, userLong);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f));

        setMarkers();
    }

    private void setMarkers() {
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        Cursor cursor = db.rawQuery("SELECT Checkin.Local, Categoria.nome, qtdVisitas, latitude, longitude " +
                "FROM Checkin INNER JOIN Categoria ON Checkin.cat = Categoria.idCategoria");

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String categorie = cursor.getString(1);
            int visits = cursor.getInt(2);
            double lat = Double.parseDouble(cursor.getString(3));
            double lng = Double.parseDouble(cursor.getString(4));

            LatLng pos = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(name)
                    .snippet("Categoria: " + categorie + " Visitas: " + visits));
        }

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkin_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_back) {
            finish();
            return true;
        } else if (id == R.id.menu_manage) {
            //startActivity(new Intent(this, GestaoCheckinActivity.class));
            return true;
        } else if (id == R.id.menu_report) {
            //startActivity(new Intent(this, RelatorioLocaisActivity.class));
            return true;
        } else if (id == R.id.menu_normal_map) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if (id == R.id.menu_hybrid_map) {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}

