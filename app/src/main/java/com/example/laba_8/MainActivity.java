package com.example.laba_8;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.laba_8.PageFragment.MY_POSITION_X_KEY;
import static com.example.laba_8.PageFragment.MY_POSITION_Y_KEY;
import static com.example.laba_8.PageFragment.PAGE_FROM;
import static com.example.laba_8.PageFragment.PAGE_TO;
import static com.example.laba_8.PageFragment.X1_KEY;
import static com.example.laba_8.PageFragment.X2_KEY;
import static com.example.laba_8.PageFragment.Y1_KEY;
import static com.example.laba_8.PageFragment.Y2_KEY;

public class MainActivity extends AppCompatActivity implements PageFragment.OnFragmentDataListener {
    private ViewPager pager;

    private Double x1 = 0.0;
    private Double y1 = 0.0;
    private Double x2 = 0.0;
    private Double y2 = 0.0;
    private Double myPositionx;
    private Double myPositiony;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();

        pager = findViewById(R.id.pager);
        pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onFragmentDataListener(Double x, Double y) {
        if (pager.getCurrentItem() == PAGE_FROM) {
            x1 = x;
            y1 = y;
        }
        if (pager.getCurrentItem() == PAGE_TO) {
            x2 = x;
            y2 = y;
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            flag = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        flag = false;
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                flag = true;
                setGpsLocation();
            }
        }
    }

    private void setGpsLocation() {
        try {
            if (flag) {
                Task<Location> locationResult = LocationServices
                        .getFusedLocationProviderClient(this)
                        .getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myPositionx = task.getResult().getLatitude();
                            myPositiony = task.getResult().getLongitude();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: ", e.getMessage());
        }
    }

    public void onShowPathClick(View view) {
        if (x1 != 0.0 && y1 != 0.0 && x2 != 0.0 && y2 != 0.0) {
            Intent intent = new Intent(this, ShowRouteActivity.class);
            intent.putExtra(X1_KEY, x1);
            intent.putExtra(Y1_KEY, y1);
            intent.putExtra(X2_KEY, x2);
            intent.putExtra(Y2_KEY, y2);
            intent.putExtra(MY_POSITION_X_KEY, myPositionx);
            intent.putExtra(MY_POSITION_Y_KEY, myPositiony);
            startActivity(intent);
        }
    }
}
