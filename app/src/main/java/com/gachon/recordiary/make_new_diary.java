package com.gachon.recordiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class make_new_diary extends AppCompatActivity {

    public static final int Group_Create_CODE = 3;
    int count = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_make_new_diary );
        final EditText Diary_name_text = findViewById(R.id.Diary_name);
        final EditText Friendtext1 = findViewById(R.id.Diary_friend1);
        final EditText Friendtext2 = findViewById(R.id.Diary_friend2);
        final EditText Friendtext3 = findViewById(R.id.Diary_friend3);
        final EditText Friendtext4 = findViewById(R.id.Diary_friend4);
        final ArrayList<String> UID_List = new ArrayList<>();
        Intent get_intent = getIntent();
        final String UID = get_intent.getExtras().getString("UID");
        UID_List.add(UID);



        findViewById(R.id.Create_diary ).setOnClickListener( new View.OnClickListener() {

            // Do processing Title or Diary nullException
            @Override
            public void onClick(View v) {
                if(Diary_name_text.getText().toString().isEmpty()){
                    Toast.makeText( make_new_diary.this, "일기장 제목을 입력하세요.", Toast.LENGTH_SHORT ).show();
                }
                else if(Diary_name_text.getText().toString().contains( "_" )){
                    Toast.makeText( make_new_diary.this, "올바르지 않은 형식입니다.", Toast.LENGTH_SHORT ).show();
                }

                else{
                    String Diary_Title = UID + "_" + Diary_name_text.getText().toString();

                    String[] Friend_UID = {
                            Friendtext1.getText().toString(),
                            Friendtext2.getText().toString(),
                            Friendtext3.getText().toString(),
                            Friendtext4.getText().toString()};

                    for(int i =0; i < count; i++){
                        UID_List.add(Friend_UID[i]);
                    }

                    //그룹 이름 검사 //그룹 이름 제한 //그룹 이름에 언더바 _ 들어가지 않도록!
                    Intent send_intent = new Intent(make_new_diary.this, DB.class );
                    send_intent.putExtra("CODE", Group_Create_CODE);
                    send_intent.putExtra("Diary_Title", Diary_Title);
                    send_intent.putStringArrayListExtra("UID", UID_List);

                    startActivity(send_intent);
                    finish();
                }
            }
        });
        findViewById(R.id.Add_friend ).setOnClickListener( new View.OnClickListener() {

            // Do processing Title or Diary nullException
            @Override
            public void onClick(View v) {
                if (count ==1){
                    Friendtext2.setVisibility(View.VISIBLE );
                    count++;
                }
                else if (count ==2){
                    Friendtext3.setVisibility(View.VISIBLE );
                    count++;
                }
                else if (count ==3){
                    Friendtext4.setVisibility(View.VISIBLE );
                    count++;
                }
                else {
                    Toast.makeText( make_new_diary.this,  " 더 이상 사람을 추가할 수 없습니다.", Toast.LENGTH_SHORT ).show();

                }
            }
        });
        findViewById(R.id.delete_friend).setOnClickListener( new View.OnClickListener() {

            // Do processing Title or Diary nullException
            @Override
            public void onClick(View v) {
                if (count ==4){
                    Friendtext4.setVisibility(View.INVISIBLE );
                    count--;
                }
                else if (count ==3){
                    Friendtext3.setVisibility(View.INVISIBLE );
                    count--;
                }
                else if (count ==2){
                    Friendtext2.setVisibility(View.INVISIBLE );
                    count--;
                }
                else {
                    Toast.makeText( make_new_diary.this,  "더 이상 삭제할 수 없습니다.", Toast.LENGTH_SHORT ).show();

                }
            }
        });

    }
}
