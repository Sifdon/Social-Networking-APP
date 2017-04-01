package com.firstapp.user.petetion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button buttonRegister;
    private EditText editTextEmail,editTextPassword,editTextname;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseAuth=FirebaseAuth.getInstance();
db= FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);
        buttonRegister=(Button)findViewById(R.id.register_button);
        editTextEmail=(EditText)findViewById(R.id.edit_useemail);
        editTextPassword=(EditText)findViewById(R.id.edit_password);
        editTextname=(EditText)findViewById(R.id.edit_username);
        //textViewSignin=(TextView)findViewById(R.id.textViewSignin);
        //buttonRegister.setOnClickListener(this);
        //textViewSignin.setOnClickListener(this);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkconnection()==0)
                {
                    Toast.makeText(RegisterActivity.this, "No Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                registerUser();
            }
        });
    }
    private void registerUser()
    {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        final String name=editTextname.getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this, "Mail id cant be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this,"Password cant be empty",Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(editTextname.getText().toString()))
        {
            Toast.makeText(RegisterActivity.this,"Name cant be empty",Toast.LENGTH_SHORT).show();
            return;

        }
        if(password.length()<6)
        {
            Toast.makeText(RegisterActivity.this,"Minimum password length is 6 characters",Toast.LENGTH_SHORT).show();
            return;
        }

        //if validations are okk then proceed
        progressDialog.setMessage("Registering User");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user is successfully logged in
                            //we will start the profile activity here
                            ////right now lets display only toast
                            Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            editTextPassword.setText("");
                            editTextEmail.setText("");
                            String s = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference current_data = db.child(s);
                            current_data.child("name").setValue(name);
                            current_data.child("image").setValue("default");
                            progressDialog.dismiss();


                            Intent intent = new Intent(RegisterActivity.this, SetUpActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //finish();
                            //Intent intent = new Intent(R.this, UserProfile.class);
                            //startActivity(intent);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Network Error!! try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();


                        }
                    }
                });

    }
    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(RegisterActivity.this, Main2Activity.class));
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
