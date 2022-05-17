package com.gachon.recordiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    public static final int Register_CODE = 2;

    public static final int Return_OK = 100;
    public static final int Return_fail = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.register);

        final EditText IDText = findViewById(R.id.register_id_text);
        final EditText PWText = findViewById(R.id.register_password_text);
        final EditText PW2Text = findViewById(R.id.register_password2_text);


        Button regi_btn = findViewById(R.id.register_btn);
        regi_btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                //
                String ID = IDText.getText().toString();
                String PW = PWText.getText().toString();
                String PW2 = PW2Text.getText().toString();


                if(ID.length() <= 0){
                    Toast.makeText(Register.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ID.contains("@")==false)
                {

                    Toast.makeText(Register.this, "이메일 형식의 아이디로 입력하세요", Toast.LENGTH_SHORT).show();
                    return;

                }
                if(PW.length() < 6){
                    Toast.makeText(Register.this, "비밀번호는 6자리이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!PW.equals(PW2))
                {
                    Toast.makeText(Register.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent Access_Auth = new Intent(Register.this, DB.class);
                Access_Auth.putExtra("CODE", Register_CODE);
                Access_Auth.putExtra("id",ID);
                Access_Auth.putExtra("pw",PW);
                startActivityForResult(Access_Auth,20);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 20) {
            //OK
            if(resultCode == Return_OK) {
                finish();
            }
            //Fail
            else if(resultCode == Return_fail){
                Toast.makeText(Register.this, "이미 존재하는 이메일 입니다.", Toast.LENGTH_SHORT).show();

            }
        }
    }


}
