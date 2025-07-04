package com.example.pratica5_inf311;

import static java.lang.Double.parseDouble;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
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
            String latStr = latText.getText().toString().replace("Latitude: ", "").trim();
            String longStr = longText.getText().toString().replace("Longitude: ", "").trim();

            if (!latStr.isEmpty() && !longStr.isEmpty()) {
                Intent it = new Intent(this, MapCheckIn.class);
                it.putExtra("latitude", parseDouble(latStr));
                it.putExtra("longitude", parseDouble(longStr));
                startActivity(it);
            } else {
                Toast.makeText(this, "Localização ainda não obtida!", Toast.LENGTH_SHORT).show();
            }

            return true;
        } else if (id == R.id.menu_manage) {
            Intent it = new Intent(this, CheckInManage.class);
            startActivity(it);
            return true;
        } else if (id == R.id.menu_report) {
            Toast.makeText(this, "Lugares mais visitados", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UnsafeIntentLaunch")
    public void checkIn(View view){
        String localName = autoCompleteLocations.getText().toString().trim();
        String categorie = (String) spinnerCategories.getSelectedItem();
        String lati = latText.getText().toString().replace("Latitude: ", "").trim();
        String longi = longText.getText().toString().replace("Longitude: ", "").trim();

        if (localName.isEmpty() || categorie == null || categorie.isEmpty() || lati.isEmpty() || longi.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos e aguarde a obtenção da localização!", Toast.LENGTH_LONG).show();
            return;
        }

        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        Cursor cursor = db.rawQuery("SELECT qtdVisitas FROM Checkin WHERE Local = '" + localName + "'");

        if (cursor.moveToFirst()) {
            int visits = cursor.getInt(0) + 1;
            ContentValues values = new ContentValues();
            values.put("qtdVisitas", visits);

            db.atualizar("Checkin", values, "Local = '" + localName + "'");
            Toast.makeText(this, "Check-in atualizado!", Toast.LENGTH_SHORT).show();
        } else {
            Cursor catCursor = db.rawQuery("SELECT idCategoria FROM Categoria WHERE nome = '" + categorie + "'");

            if (catCursor.moveToFirst()) {
                int categorieId = catCursor.getInt(0);

                ContentValues values = new ContentValues();
                values.put("Local", localName);
                values.put("qtdVisitas", 1);
                values.put("cat", categorieId);
                values.put("latitude", lati);
                values.put("longitude", longi);

                db.inserir("Checkin", values);
                Toast.makeText(this, "Novo check-in adicionado!", Toast.LENGTH_SHORT).show();
            }
            catCursor.close();
        }

        cursor.close();

        Intent it = new Intent(this, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        finish();
    }
}