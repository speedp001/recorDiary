package com.gachon.recordiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;


    EditText Login_id;
    EditText Login_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.login);

        //register button Click
        Button regi_btn = findViewById(R.id.register_start_btn);
        regi_btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent reg_intent = new Intent(getApplicationContext(), Register.class);
                startActivity(reg_intent);
            }
        });

        Login_id = findViewById(R.id.id_text);
        Login_pw = findViewById(R.id.password_text);

    }

    public void Login_Btn_Click(View view) {

        firebaseAuth = FirebaseAuth.getInstance();

        String ID = Login_id.getText().toString();
        String PW = Login_pw.getText().toString();

        if(ID.length() == 0){
            Toast.makeText(Login.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(PW.length() < 6) {
            Toast.makeText(Login.this, "비밀번호는 6자리이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(ID, PW).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Make_diary_Activity.class );
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

