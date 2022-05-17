package com.gachon.recordiary;

import static android.content.RestrictionsManager.RESULT_ERROR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Write_Diary extends AppCompatActivity {

    private Uri filePath;
    private ImageView imageView;
    public static final int REQUESTCODE = 0;


    String curUser;
    String curGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_diary);
        final EditText Titletext = findViewById(R.id.Diary_title);
        final EditText Diarytext = findViewById(R.id.Diary_input);


        Intent get_intent = getIntent();
        curUser = get_intent.getExtras().getString("curUser");
        curGroup = get_intent.getExtras().getString("curGroup");
        imageView = findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(Write_Diary.this);



            }
        });


        final Intent intent = new Intent(Write_Diary.this, DB.class);

        findViewById(R.id.Diary_upload).setOnClickListener(new View.OnClickListener() {

            // Do processing Title or Diary nullException
            @Override
            public void onClick(View v) {
                String Title = Titletext.getText().toString();
                String Diary = Diarytext.getText().toString();

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd");
                String str_date = sdf.format(date);

                intent.putExtra("CODE", REQUESTCODE);
                intent.putExtra("keyGroup", curGroup);
                intent.putExtra("keyUser", curUser);
                intent.putExtra("keyDate",str_date);
                intent.putExtra("keyTitle",Title);
                intent.putExtra("keyDiary",Diary);
                intent.putExtra("keyPath", String.valueOf(filePath));

                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            filePath = data.getData();
            Uri destinetion = Uri.fromFile( new File(getCacheDir(),"cropped"));
            Crop.of( filePath,destinetion ).asSquare().start(this);
            imageView.setImageURI( Crop.getOutput(data));
        }
        else if (requestCode==Crop.REQUEST_CROP)
        {
            handle_crop(resultCode,data);
        }



    }

    private void handle_crop(int Code, Intent result) {
        if(Code == RESULT_OK){
            imageView.setImageURI( Crop.getOutput(result));


        }
        else if (Code == RESULT_ERROR)
        {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();

        }}

}
