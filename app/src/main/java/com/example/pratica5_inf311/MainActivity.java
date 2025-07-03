package com.example.pratica5_inf311;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteLocations;
    private Spinner spinnerCategories;
    private TextView latText, longText;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteLocations = findViewById(R.id.localNames);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        latText = findViewById(R.id.latText);
        longText = findViewById(R.id.longText);

        getVisitedLocations();
        getCategories();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        } else {
            updateLatLong();
        }
    }

    private void getVisitedLocations() {
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        Cursor cursor = db.rawQuery("SELECT DISTINCT Local FROM Checkin");
        ArrayList<String> locations = new ArrayList<>();

        while (cursor.moveToNext()) {
            locations.add(cursor.getString(0));
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, locations);
        autoCompleteLocations.setAdapter(adapter);
    }

    private void getCategories() {
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        Cursor cursor = db.rawQuery("SELECT nome FROM Categoria ORDER BY idCategoria ASC");
        ArrayList<String> categories = new ArrayList<>();

        while (cursor.moveToNext()) {
            categories.add(cursor.getString(0));
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        spinnerCategories.setAdapter(adapter);
    }

    private void updateLatLong() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationListener listener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                latText.setText("Latitude: " + latitude);
                longText.setText("Longitude: " + longitude);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateLatLong();
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_map) {
            Toast.makeText(this, "Mapa de Check-in", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_manage) {
            Toast.makeText(this, "Gestão de Check-in", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_report) {
            Toast.makeText(this, "Lugares mais visitados", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}