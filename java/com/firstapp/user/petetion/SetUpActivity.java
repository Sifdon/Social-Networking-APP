package com.firstapp.user.petetion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetUpActivity extends AppCompatActivity {
ImageButton imageButton;
    EditText e1;
    Button button_submit;
    public static int GALLERY_INTENT=1;
    Uri uri=null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseusers;
    ProgressDialog progressDialog;
    private StorageReference mStorageImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);


        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mDatabaseusers= FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageImage= FirebaseStorage.getInstance().getReference().child("Profile_images");


        imageButton=(ImageButton)findViewById(R.id.profile_pic);
        e1=(EditText)findViewById(R.id.user_display_name);
        button_submit=(Button)findViewById(R.id.button_profile_submit);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_INTENT);
            }
        });



        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupAccount();
            }
        });
    }

    private void setupAccount() {
        final String name=e1.getText().toString().trim();

        final String user_id=mAuth.getCurrentUser().getUid();
        if(TextUtils.isEmpty(name)||uri==null)

        {
            Toast.makeText(SetUpActivity.this,"Please fill the details!",Toast.LENGTH_LONG).show();
            return;
        }
            if(!TextUtils.isEmpty(name)&&uri!=null)
        {
            progressDialog.setMessage("Uploading your profile....");
            progressDialog.setCancelable(false);
            progressDialog.show();
StorageReference filepath=mStorageImage.child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
String download_uri=taskSnapshot.getDownloadUrl().toString();

                    mDatabaseusers.child(user_id).child("name").setValue(name);
                    mDatabaseusers.child(user_id).child("image").setValue(download_uri);
                    progressDialog.dismiss();
                    startActivity(new Intent(SetUpActivity.this,MainActivity.class));
                }
            });
        }
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
        startActivity(new Intent(SetUpActivity.this, Main2Activity.class));
    }
}
