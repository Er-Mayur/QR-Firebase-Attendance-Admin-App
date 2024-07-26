package com.ermayurmahajan.mcoeadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminMainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_activity);

        getSupportActionBar().setTitle("Attendance");
        authProfile = FirebaseAuth.getInstance();
        //bottom navigation bar
        bottomNavView = findViewById(R.id.bottomNavView);

        // Restoring selected item from saved state
        if (savedInstanceState != null) {
            int selectedItemId = savedInstanceState.getInt("selectedItemId");
            bottomNavView.setSelectedItemId(selectedItemId);
        }
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemID = item.getItemId();
                if (itemID == R.id.menu_attendance) {
                    startFragment(new AttendanceFragment(),"Attendance");
                } else if (itemID == R.id.menu_class_info) {
                    startFragment(new ClassInfoFragment(),"Class Info");
                }
                return true;
            }

        });
       bottomNavView.setSelectedItemId(R.id.menu_attendance);
    }

    public void startFragment(Fragment fragment, String title){
        getSupportActionBar().setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItemId", bottomNavView.getSelectedItemId());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.change_password_menu:
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                authProfile.signOut();
                Toast.makeText(AdminMainActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(AdminMainActivity.this, MainActivity.class);
                intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent5);
                finish(); // to close UserProfile Activity Activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}