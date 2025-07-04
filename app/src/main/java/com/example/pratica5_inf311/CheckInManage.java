package com.example.pratica5_inf311;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CheckInManage extends AppCompatActivity {
    private LinearLayout layoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_manage);

        setTitle("GestaoCheckIn");

        layoutContent = findViewById(R.id.layoutContent);

        fillList();
    }

    private void fillList() {
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        Cursor cursor = db.rawQuery("SELECT Local FROM Checkin");

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);

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

            ImageButton bt = new ImageButton(this);
            bt.setImageResource(android.R.drawable.ic_delete);
            bt.setBackgroundColor(0x00000000);
            bt.setColorFilter(getResources().getColor(android.R.color.holo_red_light));
            bt.setTag(name);
            bt.setOnClickListener(this::confirmDeletion);
            bt.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            row.addView(tv);
            row.addView(bt);

            layoutContent.addView(row);
        }

        cursor.close();
    }

    public void confirmDeletion(View view) {
        String localName = (String) view.getTag();

        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setMessage("Tem certeza que deseja excluir " + localName + "?")
                .setPositiveButton("SIM", (dialog, which) -> {
                    BancoDadosSingleton db = BancoDadosSingleton.getInstance();
                    db.deletar("Checkin", "Local = '" + localName + "'");
                    Toast.makeText(this, "Check-in excluído!", Toast.LENGTH_SHORT).show();

                    Intent it = new Intent(this, CheckInManage.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                    finish();
                })
                .setNegativeButton("NÃO", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkin_manage, menu);
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
