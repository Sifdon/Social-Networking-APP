package com.firstapp.user.petetion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MYProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseusers;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    ProgressDialog progressDialog;
    private StorageReference mStorageImage;
    TextView textView;
    String s;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);


        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        mDatabaseusers= FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageImage= FirebaseStorage.getInstance().getReference().child("Profile_images");
        textView=(TextView)findViewById(R.id.textView);

progressDialog.setMessage("Loading your personal information");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String user_id=mAuth.getCurrentUser().getUid();
        //StorageReference filepath=mStorageImage.child(uri.getLastPathSegment());
       /* filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String download_uri=taskSnapshot.getDownloadUrl().toString();

                mDatabaseusers.child(user_id).child("name").setValue(name);
                mDatabaseusers.child(user_id).child("image").setValue(download_uri);
                progressDialog.dismiss();
                startActivity(new Intent(MYProfile.this,MainActivity.class));
            }
        });*/
       // mDatabaseusers.child(user_id).child("image").getKey();

        mDatabaseusers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                s = String.valueOf(dataSnapshot.child(user_id).child("image").getValue());
                String ss = String.valueOf(dataSnapshot.child(user_id).child("name").getValue());
                progressDialog.dismiss();
                ImageView img = (ImageView) findViewById(R.id.upload_image);
                Picasso.with(getApplicationContext()).load(s).into(img);
                textView.setText("Welcome\n" +ss);
                // progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
