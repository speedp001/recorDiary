package com.gachon.recordiary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class Make_diary_Activity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;

    public static final int User_Info_DB = 4;
    public static final int get_Grouplist = 5;

    private RecyclerView listview;
    private MyAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    Button button;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_make_diary_ );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Get Nickname
        if (TextUtils.isEmpty( firebaseUser.getDisplayName() )) {

            final EditText InputName = new EditText( Make_diary_Activity.this );
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            AlertDialog.Builder getNameDialog = new AlertDialog.Builder( context, R.style.MyAlertDialogStyle );
            getNameDialog.setTitle( "닉네임입력" ).setMessage( "일기장에서 사용할 닉네임을 입력해주세요! (한글 2~8자)" );
            InputName.setLayoutParams( params );
            InputName.setSingleLine( true );
            getNameDialog.setView( InputName );
            getNameDialog.setCancelable( false );


            InputName.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (InputName.getText().toString().length() >= 2 && InputName.getText().toString().length() <= 8) {
                        button.setEnabled(true);
                        InputName.setTextColor( Color.GRAY);
                    } else {
                        InputName.setTextColor(Color.RED );
                        button.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }

            } );


            getNameDialog.setPositiveButton( "확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final String name = InputName.getText().toString();
                    if (TextUtils.isEmpty( name )) {
                        Toast.makeText( Make_diary_Activity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT ).show();
                    } else {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName( name ).build();
                        firebaseUser.updateProfile( profileUpdates ).addOnCompleteListener( new OnCompleteListener< Void >() {
                            @Override
                            public void onComplete(@NonNull Task< Void > task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent( Make_diary_Activity.this, DB.class );
                                    intent.putExtra( "CODE", User_Info_DB );
                                    intent.putExtra( "ID", firebaseUser.getEmail() );
                                    intent.putExtra( "Name", name );
                                    intent.putExtra( "UID", UID );
                                    startActivity( intent );

                                    Toast.makeText( Make_diary_Activity.this, "닉네임 등록 완료!", Toast.LENGTH_SHORT ).show();
                                }
                            }
                        } );
                        dialog.dismiss();
                    }

                }
            } );

            final AlertDialog alertDialog = getNameDialog.create();
            alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {

                @Override

                public void onShow(DialogInterface dialog) {

                    button = alertDialog.getButton( AlertDialog.BUTTON_POSITIVE );

                    if (button != null) {
                        button.setEnabled( false );
                    }
                }
            } );
            alertDialog.show();
        }

        UID = firebaseUser.getUid();
        setContentView( R.layout.activity_make_diary_ );

        getGrouplist( UID );

        ImageButton diary_add_btn = findViewById( R.id.make_new_diary_btn );
        diary_add_btn.setOnClickListener( new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent( Make_diary_Activity.this, make_new_diary.class );
                intent.putExtra( "UID", UID );

                startActivity( intent );
            }
        } );

        Toolbar myToolbar = findViewById( R.id.my_toolbar );
        setSupportActionBar( myToolbar );
        mDrawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        getSupportActionBar().setDisplayShowTitleEnabled( false );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.menu);
        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.bringToFront();
        View headerView = navigationView.inflateHeaderView( R.layout.userinfo_header );
        TextView UserInfo_Name = headerView.findViewById( R.id.User_Name );
        TextView UserInfo_Email = headerView.findViewById( R.id.User_mail );
        TextView UserInfo_UID = headerView.findViewById( R.id.User_UID );

        UserInfo_Name.setText( firebaseUser.getDisplayName() );
        UserInfo_Email.setText( firebaseUser.getEmail() );
        UserInfo_UID.setText( firebaseUser.getUid() );

        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked( true );
                mDrawerLayout.closeDrawers();
                int id = menuItem.getItemId();


                if (id == R.id.action_settings) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
                    ClipData clip = ClipData.newPlainText( "UID", firebaseUser.getUid() );
                    clipboard.setPrimaryClip( clip );
                    Toast.makeText( context, " 클립보드에 코드가 복사되었습니다.", Toast.LENGTH_SHORT ).show();

                } else if (id == R.id.action_settings2) {
                    firebaseAuth.signOut();
                    Toast.makeText( context, " 로그아웃 되었습니다.", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( Make_diary_Activity.this, Login.class );
                    startActivity( intent );
                    finish();

                } else if (id == R.id.action_settings3) {
                    Toast.makeText( context, "Create by 지우 현민", Toast.LENGTH_SHORT ).show();

                }

                return true;
            }


        } );


        swipeRefreshLayout=findViewById( R.id.swipe2 );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getGrouplist(UID);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    public void getGrouplist(String UID) {
        Intent get_Group = new Intent( Make_diary_Activity.this, DB.class );
        get_Group.putExtra( "CODE", get_Grouplist );
        get_Group.putExtra( "UID", UID );
        startActivityForResult( get_Group, 10 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            if (resultCode == 11) {
                if (data.getExtras() != null) {
                    ArrayList< String > grouplist = data.getStringArrayListExtra( "grouplist" );
                    ArrayList < String > groupkey = data.getStringArrayListExtra( "groupkey" );
                    ArrayList < String > parsegroup = parsegrouplist( grouplist );
                    printscreen(parsegroup, groupkey);
                }

            }
        }
    }

    private ArrayList < String > parsegrouplist(ArrayList < String > grouplist) {
        ArrayList < String > result = new ArrayList <>();
        int i;
        String tmp;
        for (i = 0; i < grouplist.size(); i++) {
            tmp = grouplist.get( i );
            result.add( tmp.substring( tmp.lastIndexOf( "_" ) + 1 ) );
        }

        return result;
    }

    private void printscreen(ArrayList < String > grouplist, final ArrayList<String> groupkey) {

        listview = findViewById( R.id.make_diary_listView );
        MyListDecoration decoration = new MyListDecoration();
        LinearLayoutManager layoutManager = new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false );
        listview.setLayoutManager( layoutManager );
        adapter = new MyAdapter( this, grouplist);

        listview.setAdapter( adapter );
        listview.addItemDecoration( decoration );


        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                TextView c = (TextView) v.findViewById(R.id.Diary_Title);
                String groupname = c.getText().toString();
                String key = groupkey.get(position);
                Intent intent = new Intent( Make_diary_Activity.this, Main_Screen.class );
                intent.putExtra("cur_User",firebaseUser.getDisplayName());
                intent.putExtra("cur_Group", groupname);
                intent.putExtra("cur_UID",UID);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        }) ;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                mDrawerLayout.openDrawer( GravityCompat.START );
                return true;
            }
        }
        return super.onOptionsItemSelected( item );

    }

    private long time = 0;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText( getApplicationContext(), "뒤로 버튼을 한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT ).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            firebaseAuth.signOut();
            finish();
        }
    }

    static class MyAdapter extends RecyclerView.Adapter < MyAdapter.ViewHolder > {

        private ArrayList < String > itemList;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(View v, int position) ;
        }

        private OnItemClickListener mListener = null ;


        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mListener = listener ;
        }
        public MyAdapter(Context context, ArrayList < String > itemList) {
            this.context = context;
            this.itemList = itemList;

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( context ).inflate( R.layout.make_diary_item, parent, false );
            return new ViewHolder( view );

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String item = itemList.get( position );
            holder.textview.setText( item );

        }

        @Override
        public int getItemCount() {
            if (itemList == null) {
                return 0;
            }

            return itemList.size();

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textview;

            public ViewHolder(View itemView) {
                super( itemView );
                textview = itemView.findViewById( R.id.Diary_Title );
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition() ;
                        if (pos != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onItemClick(v, pos) ;
                            }
                        }


                    }
                });

            }


        }
    }

    class MyListDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getChildAdapterPosition( view ) != parent.getAdapter().getItemCount() - 1) {
            }
        }

    }
}
