package com.example.voationg.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voationg.R;
import com.example.voationg.model.modeldata;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference Like;

    EditText edtPost;
    Button btn_post;
    RecyclerView recyclerView;
    boolean mProcesslike =false;
    private static final String TAG = "MainActivity";

    FirebaseRecyclerOptions<modeldata> option;
    FirebaseRecyclerAdapter<modeldata,MainActivity.Userviewholder> adapter;

    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPost=findViewById(R.id.edt_post);
        btn_post=findViewById(R.id.btn_post);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        Like = FirebaseDatabase.getInstance().getReference().child("Likes");
        recyclerView=findViewById(R.id.recylerview);
        mToolbar=findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Demo_voting");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!TextUtils.isEmpty(edtPost.getText().toString())){

                    DatabaseReference newPost = mDatabase.push();

                    newPost.child("content").setValue(edtPost.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();


        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            sendtTologin();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference querydatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        option= new FirebaseRecyclerOptions.Builder<modeldata>().setQuery(querydatabase,modeldata.class).build();

        adapter = new FirebaseRecyclerAdapter<modeldata, MainActivity.Userviewholder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull final MainActivity.Userviewholder userviewholder, final int i, @NonNull modeldata modeldata) {

                userviewholder.tv_post.setText(modeldata.getContent());
                final String key  = getRef(i).getKey();
                Log.d(TAG, "onBindViewHolder: .....................your post key  "+ key);


                likeicon(key,userviewholder.btn_like);
                Like.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        Log.d(TAG, "onDataChange: ...........numb of children  "+ count);
                        userviewholder.numoflike.setText(String.valueOf(count));

                        notifyItemChanged(i);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

              userviewholder.btn_like.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mProcesslike=true;


                          Like.addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                  if(mProcesslike){

                                      if(dataSnapshot.child(key).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                          Like.child(key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                          mProcesslike=false;

                                      }else {

                                          Like.child(key).child(mAuth.getCurrentUser().getUid()).setValue("Randomevalue");
                                          mProcesslike=false;
                                      }

                                  }else {


                                  }
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError databaseError) {


                              }
                          });


                  }
              });


            }

            @NonNull
            @Override
            public MainActivity.Userviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

               View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.postitem,parent,false);
                return new Userviewholder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    private void likeicon(final String key, final ImageButton like) {

        Like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(key).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){



                    like.setImageResource(R.drawable.ic_baseline_thumb_up_liked);
                }else {

                   like.setImageResource(R.drawable.ic_baseline_thumb_up);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendtTologin() {

        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menue,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logout();
                return true;
            default: return false;
        }
    }

    private void logout() {

        mAuth.getInstance().signOut();

        Toast.makeText(this, "you signed out", Toast.LENGTH_SHORT).show();
        sendtTologin();

    }

    public class Userviewholder extends RecyclerView.ViewHolder {

        TextView tv_post,numoflike;
        ImageButton btn_like;

        public Userviewholder(@NonNull View itemView) {
            super(itemView);

            tv_post=itemView.findViewById(R.id.postview);
            btn_like= itemView.findViewById(R.id.thumsup);
            numoflike= itemView.findViewById(R.id.numoflike);
        }
    }
}