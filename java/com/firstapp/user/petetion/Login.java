package com.firstapp.user.petetion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends Activity implements View.OnClickListener {
TextView signup;
    Button button;
    EditText editTextEmail,editTextPassword;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    int TAG=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth= FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            //profile activity here

            finish();
            Intent intent =new Intent(Login.this,MainActivity.class);
            startActivity(intent);
        }
        progressDialog=new ProgressDialog(this);
        signup=(TextView)findViewById(R.id.textViewSignup);
        button=(Button)findViewById(R.id.buttonRegister);
        editTextEmail=(EditText)findViewById(R.id.editTextMail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
       // mSignInButton=(SignInButton)findViewById(R.id.google_sign_in_button);

        button.setOnClickListener(this);
        signup.setOnClickListener(this);



    }





private void userlogin()
{
    String email=editTextEmail.getText().toString().trim();
    String password=editTextPassword.getText().toString().trim();
    if(TextUtils.isEmpty(email))
    {
        Toast.makeText(Login.this, "Mail id cant be empty", Toast.LENGTH_SHORT).show();
        return;
    }
    if(TextUtils.isEmpty(password))
    {
        Toast.makeText(Login.this,"Password cant be empty",Toast.LENGTH_SHORT).show();
        return;

    }
    //a@gmail.com 123456
    //friday@gmail.com 123456
    //reddy@gmail.com
    //1234@gmail.com abcdefgh

    //if validations are okk then proceed
    progressDialog.setMessage("Signing In");
    progressDialog.setCancelable(false);
    progressDialog.show();

    firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    progressDialog.dismiss();
                    //user profile
                    if (task.isSuccessful()) {
                        finish();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, "Wrong Credentials!!", Toast.LENGTH_SHORT).show();
                        editTextEmail.setText("");
                        editTextPassword.setText("");

                    }
                }
            });
}
    @Override
    public void onClick(View view) {
        if(checkconnection()==0)
        {
            Toast.makeText(Login.this,"No connection please try again",Toast.LENGTH_LONG).show();
            return;
        }
        if(view==button)
        {
userlogin();
        }
        if(view==signup)
        {
            finish();
            startActivity(new Intent(this,RegisterActivity.class));
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(Login.this,Main2Activity.class));
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
