package com.abed.eCommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.abed.eCommerce.Model.User;
import com.abed.eCommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class ZeroPage extends AppCompatActivity {

    private Button joinNowButton, LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zero_page);

        joinNowButton = (Button) findViewById(R.id.hometoJoin);
        LoginButton = (Button) findViewById(R.id.hometoLogin);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZeroPage.this, MainActivity.class);

                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ZeroPage.this, regestrationActivity.class);
                startActivity(intent);

            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordkey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordkey != "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordkey)) {

                AllowAccess(UserPhoneKey, UserPasswordkey);


            }
        }


    }

    private void AllowAccess(final String phone, final String password) {
        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("User").child(phone).exists()) {
                    User usersData = dataSnapshot.child("User").child(phone).getValue(User.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            Toast.makeText(ZeroPage.this, "Please wait , You are already logged in..", Toast.LENGTH_SHORT);


                            Intent intent = new Intent(ZeroPage.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);

                        } else {


                            Toast.makeText(ZeroPage.this, "Password Incorrect", Toast.LENGTH_SHORT);

                        }
                    }

                } else {
                    Toast.makeText(ZeroPage.this, "Account with this" + phone + "Do not Exits ", Toast.LENGTH_SHORT);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
