package com.example.pratica5_inf311;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CheckInReport extends AppCompatActivity {
    private LinearLayout layoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_report);

        setTitle("Relatorio");

        layoutContent = findViewById(R.id.layoutContent);

        fillList();
    }

    private void fillList() {
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        Cursor cursor = db.rawQuery("SELECT Local, qtdVisitas FROM Checkin ORDER BY qtdVisitas DESC");

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int visits = cursor.getInt(1);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 8, 0, 8);

            // Nome do local
            TextView tv = new TextView(this);
            tv.setText(name);
            tv.setTextSize(18);
            tv.setTextColor(getResources().getColor(android.R.color.white));
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tv.setPadding(0, 8, 0, 8);

            TextView tvVisits = new TextView(this);
            tvVisits.setText(String.valueOf(visits));
            tvVisits.setTextSize(18);
            tvVisits.setTextColor(getResources().getColor(android.R.color.white));
            tvVisits.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvVisits.setPadding(16, 8, 0, 8);

            row.addView(tv);
            row.addView(tvVisits);

            layoutContent.addView(row);
        }

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkin_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_back) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

