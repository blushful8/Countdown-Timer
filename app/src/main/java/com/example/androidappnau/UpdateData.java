package com.example.androidappnau;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateData extends AppCompatActivity {


    EditText name;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        name = findViewById(R.id.name);
        update = findViewById(R.id.btn_update);


        //GET DATA
        name.setText(getIntent().getExtras().getString("name"));
        final String id = getIntent().getExtras().getString("id");

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_txt = name.getText().toString().trim();
                if (name_txt.equals("")) {
                    Toast.makeText(UpdateData.this, "All Field is required ....", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseClass.getDatabase(getApplicationContext()).getDao().updateData(name_txt, Integer.parseInt(id));
                    finish();

                }


            }
        });
    }
}