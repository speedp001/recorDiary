package com.gachon.recordiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Select_Diary extends AppCompatActivity {

    // MediaPlayer 객체생성
    MediaPlayer mediaPlayer;
    // 시작버튼
    Button startButton;
    //종료버튼
    Button stopButton;

    public static final int delete_DIARY_CODE = 6;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    String key;
    String curGroup;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.select_diary);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_Diary);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar3);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        TextView tx1 = findViewById(R.id.Read_Title);
        TextView tx2 = findViewById(R.id.Read_date);
        TextView tx3 = findViewById(R.id.Read_Content);

        Intent intent = getIntent();
        String diarytext= intent.getExtras().getString( "diary" );
        tx3.setText( diarytext );
        String titletext= intent.getExtras().getString( "title" );
        tx1.setText( titletext );
        String datetext= intent.getExtras().getString( "date" );
        tx2.setText( datetext );

        String emotion = intent.getExtras().getString("emotion");
        LinearLayout emotionLayer = findViewById(R.id.emotionLayer);
        // 이미지뷰 동적 할당
        ImageView iv = new ImageView(this);
        if(emotion.equals("perfect")) {
            iv.setImageResource(R.drawable.perfect);
            emotionLayer.addView(iv);
        }
        if(emotion.equals("good")) {
            iv.setImageResource(R.drawable.good);
            emotionLayer.addView(iv);
        }
        if(emotion.equals("soso")) {
            iv.setImageResource(R.drawable.soso);
            emotionLayer.addView(iv);
        }
        if(emotion.equals("bad")) {
            iv.setImageResource(R.drawable.bad);
            emotionLayer.addView(iv);
        }

        final ImageView Read_imageview = findViewById(R.id.Read_image);

        String imagepath = intent.getExtras().getString("imagepath");
        curGroup = intent.getExtras().getString("curGroup");
        String writeUID = intent.getExtras().getString("writeUID");
        key = intent.getExtras().getString("key");
        StorageReference storageReference = firebaseStorage.getReference().child(imagepath);

        String curuid = firebaseUser.getUid();


        if(writeUID.equals(curuid)){
            myToolbar.setVisibility(View.VISIBLE);
        }else {
            myToolbar.setVisibility(View.INVISIBLE);
        }

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Glide.with(Select_Diary.this)
                            .load(task.getResult())
                            .into(Read_imageview);
                }else{
                    Toast.makeText(Select_Diary.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 배경음악 부분
        startButton = findViewById(R.id.start);
        stopButton = findViewById(R.id.stop);

        String music = intent.getExtras().getString( "emotion" );

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), music, Toast.LENGTH_SHORT).show();
                if(music.equals("perfect")) {
                    // MediaPlayer 객체 할당
                    mediaPlayer = MediaPlayer.create(Select_Diary.this, R.raw.perfect);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                if(music.equals("good")) {
                    // MediaPlayer 객체 할당
                    mediaPlayer = MediaPlayer.create(Select_Diary.this, R.raw.good);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                if(music.equals("soso")) {
                    // MediaPlayer 객체 할당
                    mediaPlayer = MediaPlayer.create(Select_Diary.this, R.raw.bad);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                if(music.equals("bad")) {
                    // MediaPlayer 객체 할당
                    mediaPlayer = MediaPlayer.create(Select_Diary.this, R.raw.soso);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 정지버튼
                mediaPlayer.stop();
                // 초기화
                mediaPlayer.reset();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                show();
                break;
        }
        return true;
    }

    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("일기를 삭제하시겠습니까?");

        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Select_Diary.this, DB.class);
                        intent.putExtra("CODE", delete_DIARY_CODE);
                        intent.putExtra("curGroup", curGroup);
                        intent.putExtra("key",key);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu3, menu);
        return true;
    }

    // MediaPlayer는 시스템 리소스를 잡아먹는다.
    // MediaPlayer는 필요이상으로 사용하지 않도록 주의해야 한다.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MediaPlayer 해지
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
