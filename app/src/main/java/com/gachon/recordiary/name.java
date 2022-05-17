package com.gachon.recordiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class name extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_name );

        EditText nametext = findViewById(R.id.name_text);
        final String name = nametext.getText().toString();

        Button b = (Button) findViewById( R.id.name_btn );
        b.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(name.this, DB.class);
                intent.putExtra("name", name );
                finish();
            }
        } );
    }
}
