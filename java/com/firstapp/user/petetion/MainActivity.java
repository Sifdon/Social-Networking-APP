package com.firstapp.user.petetion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
private RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    //private RecyclerView bloglist;
    DatabaseReference firebaseDatabase;
    DatabaseReference mdatabaselike;
    boolean mProcessLike=false;
    String url_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        mAuthlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent intent=new Intent(MainActivity.this,Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
       firebaseDatabase=FirebaseDatabase.getInstance().getReference().child("Blog");
        mdatabaselike=FirebaseDatabase.getInstance().getReference().child("Likes");

        mdatabaselike.keepSynced(true);
        firebaseDatabase.keepSynced(true);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        recyclerView=(RecyclerView)findViewById(R.id.blog_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onStart()
    {
        mAuth.addAuthStateListener(mAuthlistener);
        progressDialog.setMessage("Loading...\nPlease wait...");
        progressDialog.show();
       progressDialog.setCancelable(false);
        super.onStart();

        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                firebaseDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {


                final String key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeButton(key);
                //viewHolder.load_image(key);
                //viewHolder.setLike_count(key);

                viewHolder.btnimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
mProcessLike=true;


mdatabaselike.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(mProcessLike) {
            if (dataSnapshot.child(key).hasChild(mAuth.getCurrentUser().getUid())) {
                mdatabaselike.child(key).child(mAuth.getCurrentUser().getUid()).removeValue();

mProcessLike=false;
            } else {
                mdatabaselike.child(key).child(mAuth.getCurrentUser().getUid()).setValue("rand");
                mProcessLike=false;
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});

                    }
                });
                progressDialog.dismiss();
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder
    {
        ImageButton btnimg;
View mview;
        TextView like_count;
        DatabaseReference databaseReference,dataurl;
        FirebaseAuth mAuth;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
            btnimg=(ImageButton)mview.findViewById(R.id.btn_like);
            like_count=(TextView)mview.findViewById(R.id.like_count);

            databaseReference=FirebaseDatabase.getInstance().getReference();
            dataurl=FirebaseDatabase.getInstance().getReference().child("Users");

            String url_key=dataurl.getKey();
            mAuth=FirebaseAuth.getInstance();
        }


        public void setLikeButton(final String post_key)
        {
databaseReference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.child("Likes").child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
            btnimg.setImageResource(R.drawable.like);


            String s=dataSnapshot.child("Likes").child(post_key).toString();
            Matcher m = Pattern.compile("rand").matcher(s);
            int matches = 0;
            while(m.find())
                matches++;

            String f= String.valueOf(((int) matches));
            like_count.setText(f);

        }
        else
        {
            String s=dataSnapshot.child("Likes").child(post_key).toString();


            btnimg.setImageResource(R.drawable.dislike);
           // String s=dataSnapshot.child("Users").child("image").toString();
            Matcher m = Pattern.compile("rand").matcher(s);
            int matches = 0;
            while(m.find())
                matches++;

            String f= String.valueOf(((int) matches));
            like_count.setText(f);

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
        }
        public void setTitle(String title)
        {
            //separating title and topic names by inserting '$' between them
            String s1="",s2="";
            int i=0;
            for( i=0;i<title.length();i++)
            {
                if(title.charAt(i)=='$')
                {
                    i++;
                    break;
                }
                char c=title.charAt(i);
                s1=s1+c;

            }
            for(;i<title.length();i++)
            {
                char c=title.charAt(i);
                s2=s2+c;

            }
            TextView t1=(TextView)mview.findViewById(R.id.post_title);
            t1.setText(s2);
            TextView t2=(TextView)mview.findViewById(R.id.post_topic);
            t2.setText(s1);
        }
        public void setDescription(String description)
        {
            TextView t2=(TextView)mview.findViewById(R.id.post_text);
            t2.setText(description);
        }
        public void setUsername(String username)
        {
            TextView t3=(TextView)mview.findViewById(R.id.post_author);
            t3.setText("by "+username);
        }

        public  void setImage(Context ctx,String image)
        {
            ImageView img=(ImageView)mview.findViewById(R.id.upload_image);
            Picasso.with(ctx).load(image).into(img);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_log_out)
        {
            //startActivity(new Intent(MainActivity.this,PostActivity.class));
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(MainActivity.this,Main2Activity.class));
    }
}
