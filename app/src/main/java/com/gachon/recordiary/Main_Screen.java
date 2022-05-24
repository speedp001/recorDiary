package com.gachon.recordiary;

import static com.gachon.recordiary.DB.Write_Schedule_CODE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

public class Main_Screen extends AppCompatActivity {

    public static final int Main_Screen_CODE = 1;
    public static final int delete_GROUP_CODE = 7;
    ArrayList<datalist> data_list;
    ArrayList<String> key;
    RecyclerAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String cur_User;
    String cur_UID;
    String groupkey;
    public String cur_Group;
    Toolbar myToolbar;
    RecyclerView recyclerView;
    ImageView emptyView;
    ImageView notemptyView;
    int[] image = { R.drawable.phrase2,R.drawable.phrase3, R.drawable.phrase4, R.drawable.phrase5,R.drawable.phrase6, R.drawable.phrase7, R.drawable.phrase8, R.drawable.phrase9,R.drawable.phrase10, R.drawable.phrase11, R.drawable.phrase12, R.drawable.phrase13,R.drawable.phrase14, R.drawable.phrase15, R.drawable.phrase16, R.drawable.phrase17,R.drawable.phrase18};

    CalendarView calendarView;
    Button button;
    TextView textView;
    EditText editText;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://recordiary-bb8f1-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        Intent get_Intent = getIntent();
        cur_User = get_Intent.getExtras().getString("cur_User");
        cur_Group = get_Intent.getExtras().getString("cur_Group");
        cur_UID = get_Intent.getExtras().getString("cur_UID");
        groupkey = get_Intent.getExtras().getString("key");

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(cur_Group);
        data_list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        emptyView=findViewById( R.id.empty_text );
        notemptyView=findViewById( R.id.not_empty_image );
        get_DB();
        Log.d("key", groupkey);

        calendarView=findViewById(R.id.calendarView);
        button=findViewById(R.id.save_button);
        textView=findViewById(R.id.ScheduleTextView);
        editText=findViewById(R.id.editTextTextPersonName);
        final String[] date = new String[1];

        //show schedule
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date[0] = String.format("%d%d%d",year,month+1,dayOfMonth);
                String schedule_path = "Schedule/" + cur_User + "/" + date[0] + "/";

                databaseReference = firebaseDatabase.getReference(schedule_path);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String schedule = dataSnapshot.getValue(String.class);
                        if (schedule != null)
                        {
                            textView.setText(String.format("%d/%d/%d\n",year,month+1, dayOfMonth) + schedule);
                        }
                        else
                        {
                            textView.setText(String.format("%d/%d/%d\n일정없음",year,month+1, dayOfMonth));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        textView.setText(String.format("%d/%d/%d",year,month+1, dayOfMonth));
                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String edit_schedule = String.valueOf(editText.getText());
                Intent intent = new Intent(Main_Screen.this, DB.class);
                intent.putExtra("CODE", Write_Schedule_CODE);
                intent.putExtra("keyUserID", cur_User);
                intent.putExtra("keySchedule", edit_schedule);
                intent.putExtra("keyDate", date[0]);
                startActivity(intent);
            }
        });

        //Write Diary
        ImageButton addbtn = findViewById(R.id.Diary_add);
        addbtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(Main_Screen.this, Write_Diary.class );
                intent.putExtra("curUser", cur_User);
                intent.putExtra("curGroup", cur_Group);
                startActivity(intent);

            }
        });
        swipeRefreshLayout=findViewById( R.id.swipe );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                get_DB();

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_diary:
                show();
                break;
        }
        return true;
    }

    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("일기장을 탈퇴하시겠습니까?");
        builder.setMessage("일기장을 더 이상 열어볼 수 없습니다.");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Main_Screen.this, DB.class);
                        intent.putExtra("CODE", delete_GROUP_CODE);
                        intent.putExtra("curUID", cur_UID);
                        intent.putExtra("key", groupkey);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"탈퇴되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오",Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu2, menu);
        return true;
    }


    public void printscreen(){
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout( true );//최근 글 부터 보이게 역순 출력
        layoutManager.setStackFromEnd( true );//최근 글 부터 보이게 역순 출력
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(data_list,this, cur_Group, key);
        recyclerView.setAdapter(recyclerAdapter);


        if (recyclerAdapter.getItemCount() == 0) {

            emptyView.setVisibility(View.VISIBLE);
            notemptyView.setVisibility( View.GONE );
        }
        else {
            Random random = new Random(  );
            int num = random.nextInt(image.length);
            notemptyView.setImageResource( image[num] );
            emptyView.setVisibility(View.GONE);
            notemptyView.setVisibility( View.VISIBLE );
        }
        recyclerAdapter.notifyDataSetChanged();
    }


    public void get_DB(){
        Intent Access_DB = new Intent(Main_Screen.this, DB.class);
        Access_DB.putExtra("CODE",Main_Screen_CODE);
        Access_DB.putExtra("curGroup", cur_Group);
        startActivityForResult(Access_DB,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            if(resultCode == 11) {
                if (data.getExtras() != null) {
                    data_list = data.getExtras().getParcelableArrayList("data");
                    key = data.getStringArrayListExtra( "key" );
                    //Log.d("test", key.get(0));
                }
                printscreen();
            }
        }
    }



}
class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
    private ArrayList<datalist> list_data;
    private Context context;
    String curGroup;
    ArrayList<String> key_list;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();



    public RecyclerAdapter(ArrayList <datalist> list_data, Context context, String curGroup, ArrayList<String> key_list) {
        this.list_data = list_data;
        this.context = context;
        this.curGroup = curGroup;
        this.key_list = key_list;

    }




    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view = LayoutInflater.from( viewGroup.getContext())
                .inflate(R.layout.listitem, viewGroup ,false);
        ItemViewHolder holder = new ItemViewHolder( view );
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position){

        holder.datetext.setText(list_data.get(position).getDatetext());
        holder.titletext.setText(list_data.get(position).getTitletext());
        holder.diarytext = list_data.get(position).getDiarytext();
        holder.imagepath = list_data.get(position).getImagepath();
        holder.writeUser.setText(list_data.get(position).getWriteUser());
        holder.writeUID = list_data.get(position).getWriteUID();
        holder.key = key_list.get(position);


        StorageReference storageReference = firebaseStorage.getReference().child(holder.imagepath);

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Glide.with(context)
                            .load(task.getResult())
                            .into(holder.imageview);
                }else{
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount()
    {
        if (list_data == null) {
            return 0;
        }

        return list_data.size();

    }

    void additem(datalist datalist)
    {
        list_data.add(datalist);
    }

    void remove(int position){
        list_data.remove(position);
    }


    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView datetext;
        TextView titletext;
        String diarytext;
        TextView writeUser;
        String writeUID;
        String imagepath;
        ImageView imageview;
        String key;

        public ItemViewHolder(@NonNull View itemView){

            super(itemView);
            this.datetext=itemView.findViewById(R.id.date_content);
            this.titletext=itemView.findViewById(R.id.item_titletext);
            this.imageview=itemView.findViewById(R.id.item_imageView);
            this.writeUser=itemView.findViewById(R.id.id_content);

            //Select Diary
            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context context = view.getContext();
                    Intent intent = new Intent(context, Select_Diary.class);
                    String date = datetext.getText().toString();
                    String title = titletext.getText().toString();
                    String diary = diarytext;

                    String imagepath = curGroup + "/" + date + "/" + title + ".png";

                    intent.putExtra("date", date );
                    intent.putExtra("title", title);
                    intent.putExtra( "diary", diary);
                    intent.putExtra("imagepath", imagepath);
                    intent.putExtra("curGroup",curGroup);
                    intent.putExtra("writeUID",writeUID);
                    intent.putExtra("key",key);

                    ((Activity) context).startActivity(intent);

                }
            } );

        }
    }


}




