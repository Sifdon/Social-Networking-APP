package com.firstapp.user.petetion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
private ImageButton imageButton;
    Button submit;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    EditText e1,e2;
    Uri uri=null;
    public static int GALLERY_INTENT=1;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentuser;
    ProgressDialog progressDialog;
    private DatabaseReference mdatabaseUsers;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    String topic="";
    int f=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth=FirebaseAuth.getInstance();
        mAuthlistener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent intent=new Intent(PostActivity.this,Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        //mAuth=FirebaseAuth.getInstance();
        mCurrentuser=mAuth.getCurrentUser();
        progressDialog=new ProgressDialog(this);
storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Blog");
        mdatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentuser.getUid());

        imageButton=(ImageButton)findViewById(R.id.insert_your_image);
        submit=(Button)findViewById(R.id.button_submit);
        e1=(EditText)findViewById(R.id.edit_title);
        e2=(EditText)findViewById(R.id.edit_description);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FanMenuButtons sub = (FanMenuButtons) findViewById(R.id.myFABSubmenu);
        if (sub != null) {
            sub.setOnFanButtonClickListener(new FanMenuButtons.OnFanClickListener() {
                @Override
                public void onFanButtonClicked(int index) {

                    //sub.setButtonSelected(index);
                    select_topic(index);
                    sub.toggleShow();

                }
            });

            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sub.toggleShow();
                    }
                });
            }
        }
        //load image
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_INTENT);
            }
        });


        //submit data
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(checkconnection()==0)
{
    Toast.makeText(PostActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
    return;
}
                startposting();
            }
        });

    }

    public void startposting()
    {
        final String title_value=topic+"$"+e1.getText().toString().trim();
        final String title_description=e2.getText().toString().trim();

      //  progressDialog.setCancelable(false);
        if(!TextUtils.isEmpty(title_value)&&!TextUtils.isEmpty(title_description)&&uri!=null&&f==1)
        {
            progressDialog.setMessage("Posting data..\nPlease wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
StorageReference filepath=storageReference.child("Blog_Images").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    final Uri downloadurl=taskSnapshot.getDownloadUrl();
                    final DatabaseReference newpost=databaseReference.push();



                    mdatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newpost.child("title").setValue(title_value);
                            newpost.child("description").setValue(title_description);
                            newpost.child("image").setValue(downloadurl.toString());
                            newpost.child("uid").setValue(mCurrentuser.getUid());
                            newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if(task.isSuccessful())
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this,"Success",Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(PostActivity.this,MainActivity.class));

                }
            });

        }
        else if(TextUtils.isEmpty(title_value))
            Toast.makeText(PostActivity.this,"Title is empty",Toast.LENGTH_SHORT).show();
            else if(TextUtils.isEmpty(title_description))
            Toast.makeText(PostActivity.this,"Description is empty",Toast.LENGTH_SHORT).show();
            else if(f==0)
            Toast.makeText(PostActivity.this,"Select topic",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT&&resultCode==RESULT_OK)
        {
             uri=data.getData();
            imageButton.setImageURI(uri);
        }
    }
    @Override
    public void onBackPressed()
    {
        //go back
        finish();
        startActivity(new Intent(PostActivity.this, Main2Activity.class));
    }
    public void select_topic(int index)
    {
        if(index==6)
        {
            topic="Computer Science";
        }
        if(index==5)
        {
            topic="Online Shopping";
        }
        if(index==4)
        {
            topic="Engineering";
        }
        if(index==3)
        {
            topic="Social Networking";
        }
        if(index==2)
        {
            topic="Politics";
        }
        if(index==1)
        {
            topic="Food and Drinks";
        }
        if(index==0)
        {
            topic="Others";
        }
        Toast.makeText(PostActivity.this,"You topic is "+topic,Toast.LENGTH_SHORT).show();
        f=1;

    }
    public int checkconnection()
    {
        ConnectivityManager cn=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        if(nf != null && nf.isConnected()==true )
        {
            //Toast.makeText(this, "Network Available", Toast.LENGTH_LONG).show();
            return 1;
            //tvstatus.setText("Network Available");
        }
        else
        {
            // Toast.makeText(this, "Network Not Available", Toast.LENGTH_LONG).show();
            return 0;
            //tvstatus.setText("Network Not Available");
        }
    }
}
