package com.gachon.recordiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class DB extends AppCompatActivity {

    public static final int Write_Diary_CODE = 0;
    public static final int Main_Screen_CODE = 1;
    public static final int Register_CODE = 2;
    public static final int Group_Create_CODE = 3;
    public static final int User_Info_DB = 4;
    public static final int get_Grouplist = 5;
    public static final int delete_DIARY_CODE = 6;
    public static final int delete_GROUP_CODE = 7;
    public static final int Write_Schedule_CODE = 8;

    public static final int Return_OK = 100;
    public static final int Return_fail = 200;
    //------------- FireBase Setting----------------
    //DB
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://recordiary-bb8f1-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference databaseReference;

    // Storage
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;

    ArrayList<datalist> data_list;
    ArrayList<String> group_list;
    ArrayList<String> UID_list;
    ArrayList<String> diary_key;
    ArrayList<String> group_key;

    private Uri filePath;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String cur_groupname;

        firebaseUser = firebaseAuth.getCurrentUser();

        data_list = new ArrayList<>();
        group_list = new ArrayList<>();
        UID_list = new ArrayList<>();
        diary_key = new ArrayList<>();
        group_key = new ArrayList<>();

        final Intent get_intent = getIntent();
        int CODE = get_intent.getExtras().getInt("CODE");
        String DBPath;

        switch (CODE) {
            //DB upload
            case Write_Diary_CODE:

                cur_groupname = get_intent.getExtras().getString("keyGroup");
                String writeUser = get_intent.getExtras().getString("keyUser");
                String Date = get_intent.getExtras().getString("keyDate");
                String Title = get_intent.getExtras().getString("keyTitle");
                String Diary = get_intent.getExtras().getString("keyDiary");
                String strUri = get_intent.getExtras().getString("keyPath");
                String emotion = get_intent.getExtras().getString("emotion");
                String Imagepath = cur_groupname + "/" + Date + "/" + Title + ".png";
                filePath = Uri.parse(strUri);

                datalist tmp_datalist = new datalist(writeUser, Date, Title, Diary, Imagepath, firebaseUser.getUid(), emotion);

                DBPath = "Group/" + cur_groupname + "/";
                databaseReference = firebaseDatabase.getReference(DBPath);
                databaseReference.push().setValue(tmp_datalist);
                uploadFile(cur_groupname, Date, Title);
                finish();
                break;

            case Write_Schedule_CODE:
                String UserID = get_intent.getExtras().getString("keyUserID");
                String schedule_text = get_intent.getExtras().getString("keySchedule");
                Date = get_intent.getExtras().getString("keyDate");
                DBPath = "Schedule/" + UserID + "/" + Date;

                databaseReference = firebaseDatabase.getReference(DBPath);
                databaseReference.setValue(schedule_text);
                finish();
                break;

            //DB download
            case Main_Screen_CODE:

                cur_groupname = get_intent.getExtras().getString("curGroup");
                DBPath = "Group/" + cur_groupname + "/";

                databaseReference = firebaseDatabase.getReference(DBPath);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        data_list.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            datalist datalist = snapshot.getValue(datalist.class);
                            String key = snapshot.getKey();

                            data_list.add(datalist);
                            diary_key.add(key);
                        }

                        Intent send_intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("data", data_list);
                        send_intent.putExtras(bundle);
                        send_intent.putStringArrayListExtra("key", diary_key);
                        setResult(11, send_intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            //Register
            case Register_CODE:
                String Regist_ID = get_intent.getExtras().getString("id");
                String Regist_PW = get_intent.getExtras().getString("pw");


                firebaseAuth.createUserWithEmailAndPassword(Regist_ID, Regist_PW)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Intent send_intent = new Intent();
                                if(task.isSuccessful()){
                                    Toast.makeText(DB.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                                    setResult(Return_OK, send_intent);
                                    finish();
                                }else{
                                    Toast.makeText(DB.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                    setResult(Return_fail, send_intent);
                                    finish();
                                }
                            }
                        });
                break;

            case Group_Create_CODE:

                String Diary_Title = get_intent.getExtras().getString("Diary_Title");
                UID_list = get_intent.getStringArrayListExtra("UID");

                for(int i = 0; i < UID_list.size(); i++) {

                    String UID = UID_list.get(i);

                    databaseReference = firebaseDatabase.getReference();
                    databaseReference = databaseReference.child("/User/" + UID);

                    databaseReference.child("Group").push().setValue(Diary_Title);

                }
                finish();


            case User_Info_DB:
                String ID = get_intent.getExtras().getString("ID");
                String Name = get_intent.getExtras().getString("Name");
                String cur_UID = get_intent.getExtras().getString("UID");

                databaseReference = firebaseDatabase.getReference();
                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Object> UserValues = null;
                User user = new User(ID, Name, cur_UID);
                UserValues = user.toMap();

                childUpdates.put("/User/" + cur_UID, UserValues);
                databaseReference.updateChildren(childUpdates);

                finish();

            case get_Grouplist:

                String for_grouplist_UID = get_intent.getExtras().getString("UID");
                DBPath = "User/" + for_grouplist_UID + "/Group/";

                databaseReference = firebaseDatabase.getReference(DBPath);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        group_list.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String group = snapshot.getValue(String.class);
                            String key = snapshot.getKey();

                            group_list.add(group);
                            group_key.add(key);
                        }

                        Intent grouplist_intent = new Intent();
                        grouplist_intent.putStringArrayListExtra("grouplist", group_list);
                        grouplist_intent.putStringArrayListExtra("groupkey", group_key);
                        setResult(11, grouplist_intent);
                        finish();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            case delete_DIARY_CODE:

                String curGroup = get_intent.getExtras().getString("curGroup");
                String key = get_intent.getExtras().getString("key");

                DBPath = "Group/" + curGroup + "/" + key + "/";

                databaseReference = firebaseDatabase.getReference(DBPath);
                databaseReference.removeValue();
                finish();
                break;

            case delete_GROUP_CODE:

                String curUID = get_intent.getExtras().getString("curUID");
                String groupkey = get_intent.getExtras().getString("key");

                DBPath = "User/" + curUID + "/Group/" + groupkey + "/";

                databaseReference = firebaseDatabase.getReference(DBPath);
                databaseReference.removeValue();
                finish();
                break;

        }

    }
    private void uploadFile(String cur_groupname, String Date, String Title) {

        String savePath = cur_groupname + "/" + Date + "/";

        if (filePath != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            String filename = Title + ".png";

            StorageReference storageReference = firebaseStorage.getReference().child(savePath + filename);


            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(progressDialog != null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
