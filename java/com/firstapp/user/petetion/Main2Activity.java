package com.firstapp.user.petetion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
            if(checkconnection()==0)
            {
                Toast.makeText(Main2Activity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                return false;
            }
            firebaseAuth= FirebaseAuth.getInstance();

            if(firebaseAuth.getCurrentUser()==null)
            {
                //profile activity here

                finish();
                Intent intent =new Intent(Main2Activity.this,Login.class);
                startActivity(intent);
            }
            else
            startActivity(new Intent(Main2Activity.this, MainActivity.class));
        } else if (id == R.id.nav_gallery) {
            if(checkconnection()==0)
            {
                Toast.makeText(Main2Activity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                return false;
            }
            firebaseAuth= FirebaseAuth.getInstance();

            if(firebaseAuth.getCurrentUser()==null)
            {
                //profile activity here

                finish();
                Intent intent =new Intent(Main2Activity.this,Login.class);
                startActivity(intent);
            }
            else
            startActivity(new Intent(Main2Activity.this,PostActivity.class));
        } else if (id == R.id.nav_slideshow) {
            if(checkconnection()==0)
            {
                Toast.makeText(Main2Activity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                return false;
            }
            firebaseAuth= FirebaseAuth.getInstance();

            if(firebaseAuth.getCurrentUser()==null)
            {
                //profile activity here

                finish();
                Intent intent =new Intent(Main2Activity.this,Login.class);
                startActivity(intent);
            }
            else
            startActivity(new Intent(Main2Activity.this,MYProfile.class));

        } else if (id == R.id.nav_manage) {
            if(checkconnection()==0)
            {
                Toast.makeText(Main2Activity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                return false;
            }
            startActivity(new Intent(Main2Activity.this,Login.class));

        } else if (id == R.id.nav_share) {

startActivity(new Intent(Main2Activity.this,SQLiteDemoActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
