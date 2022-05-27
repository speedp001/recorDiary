package com.gachon.recordiary;

import static android.content.RestrictionsManager.RESULT_ERROR;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Write_Diary extends AppCompatActivity {

    Intent intentRecord;
    SpeechRecognizer mRecognizer;
    TextView recordTextView;
    ImageButton recordBtn;
    EditText Diarytext;

    final int PERMISSION = 1;
    boolean recording = false;  //녹음중인지 여부

    private Uri filePath;
    private ImageView imageView;
    public static final int REQUESTCODE = 0;

    String curUser;
    String curGroup;
    String emotion; // 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_diary);

        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        final EditText Titletext = findViewById(R.id.Diary_title);
        Diarytext = (EditText)findViewById(R.id.Diary_input);
        final Button UploadBtn = findViewById(R.id.Diary_upload);
        recordBtn = (ImageButton) findViewById(R.id.recordBtn);

        // 감정 체크박스 시작 부분
        CheckBox checkBox_P = findViewById(R.id.checkBox_P);
        CheckBox checkBox_G = findViewById(R.id.checkBox_G);
        CheckBox checkBox_S = findViewById(R.id.checkBox_S);
        CheckBox checkBox_B = findViewById(R.id.checkBox_B);

        CheckPermission();

        intentRecord=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecord.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intentRecord.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        recordBtn.setOnClickListener(v ->{
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intentRecord);
        });

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


        final Intent intent = new Intent(Write_Diary.this, DB.class); // sub(DB)로 넘기기

        UploadBtn.setOnClickListener(new View.OnClickListener() {

            // Do processing Title or Diary nullException
            @Override
            public void onClick(View view) {
                String Title = Titletext.getText().toString();
                String Diary = Diarytext.getText().toString();

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str_date = sdf.format(date);

                if (Title.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Diary.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "일기 내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkBox_P.isChecked()) {
                    // perfect db에 전송
                    emotion = "perfect";
                    Toast.makeText(getApplicationContext(), "perfect", Toast.LENGTH_SHORT).show();
                    intent.putExtra("CODE", REQUESTCODE);
                    intent.putExtra("keyGroup", curGroup);
                    intent.putExtra("keyUser", curUser);
                    intent.putExtra("keyDate", str_date);
                    intent.putExtra("keyTitle", Title);
                    intent.putExtra("keyDiary", Diary);
                    intent.putExtra("keyPath", String.valueOf(filePath));
                    intent.putExtra("emotion", emotion); //추가


                    startActivity(intent);
                    finish();
                }
                if(checkBox_G.isChecked()) {
                    // good db에 전송
                    emotion = "good";
                    intent.putExtra("CODE", REQUESTCODE);
                    intent.putExtra("keyGroup", curGroup);
                    intent.putExtra("keyUser", curUser);
                    intent.putExtra("keyDate", str_date);
                    intent.putExtra("keyTitle", Title);
                    intent.putExtra("keyDiary", Diary);
                    intent.putExtra("keyPath", String.valueOf(filePath));
                    intent.putExtra("emotion", emotion); //추가


                    startActivity(intent);
                    finish();
                }
                if(checkBox_S.isChecked()) {
                    // soso db에 전송
                    emotion = "soso";
                    intent.putExtra("CODE", REQUESTCODE);
                    intent.putExtra("keyGroup", curGroup);
                    intent.putExtra("keyUser", curUser);
                    intent.putExtra("keyDate", str_date);
                    intent.putExtra("keyTitle", Title);
                    intent.putExtra("keyDiary", Diary);
                    intent.putExtra("keyPath", String.valueOf(filePath));
                    intent.putExtra("emotion", emotion); //추가


                    startActivity(intent);
                    finish();
                }
                if(checkBox_B.isChecked()) {
                    // bad db에 전송
                    emotion = "bad";
                    intent.putExtra("CODE", REQUESTCODE);
                    intent.putExtra("keyGroup", curGroup);
                    intent.putExtra("keyUser", curUser);
                    intent.putExtra("keyDate", str_date);
                    intent.putExtra("keyTitle", Title);
                    intent.putExtra("keyDiary", Diary);
                    intent.putExtra("keyPath", String.valueOf(filePath));
                    intent.putExtra("emotion", emotion); //추가


                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "감정을 선택해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
                finish();

                switch (view.getId()) {
                    //녹음 버튼
                    case R.id.recordBtn:
                        if (!recording) {   //녹음 시작
                            StartRecord();
                            Toast.makeText(getApplicationContext(), "지금부터 음성으로 기록합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {  //이미 녹음 중이면 녹음 중지
                            StopRecord();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

     RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //stopListening을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    if (recording)
                        StartRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String originText = Diarytext.getText().toString();  //기존 text

            // 이후 for문으로 textView에 setText로 음성인식된 결과를 수정해줍니다.
            // 인식 결과
            String newText="";
            for(int i = 0; i < matches.size() ; i++){
                newText += matches.get(i);
            }
            Diarytext.setText(originText + newText + " ");   //기존의 text에 인식 결과를 이어붙임
            mRecognizer.startListening(intentRecord);    //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    //녹음 시작
    void StartRecord() {
        recording = true;
        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.stop_record);
        recordTextView.setText("음성 녹음 중지");
        mRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intentRecord);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;
        //마이크 이미지와 텍스트 변경
        recordBtn.setImageResource(R.drawable.start_record);
        recordTextView.setText("음성 녹음 시작");
        mRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();
    }

    // 퍼미션 체크
    void CheckPermission() {
        //안드로이드 버전이 6.0 이상
        if ( Build.VERSION.SDK_INT >= 23 ){
            //인터넷이나 녹음 권한이 없으면 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO},PERMISSION);
            }
        }
    }


    @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
                filePath = data.getData();
                Uri destinetion = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(filePath, destinetion).asSquare().start(this);
                imageView.setImageURI(Crop.getOutput(data));
            } else if (requestCode == Crop.REQUEST_CROP) {
                handle_crop(resultCode, data);
            }


        }

        private void handle_crop ( int Code, Intent result){
            if (Code == RESULT_OK) {
                imageView.setImageURI(Crop.getOutput(result));

            } else if (Code == RESULT_ERROR) {
                Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

}

