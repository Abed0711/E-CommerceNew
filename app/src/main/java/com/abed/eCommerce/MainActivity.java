package com.abed.eCommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abed.eCommerce.Model.User;
import com.abed.eCommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private TextView joinNowTextView;
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "User";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowTextView = findViewById(R.id.gotoReg);

        LoginButton = (Button) findViewById(R.id.login);
        InputPassword = (EditText) findViewById(R.id.inputPasswordLogin);
        InputPhoneNumber = (EditText) findViewById(R.id.UserName);
        Paper.init(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //parentDbName = "User";
                LoginUser();

            }
        });


        joinNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();


        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        } else {

            AllowAccessToAccount(phone, password);

        }


    }

    private void AllowAccessToAccount(final String phone, final String password) {

        Paper.book().write(Prevalent.UserPhoneKey, phone);
        Paper.book().write(Prevalent.UserPasswordKey, password);


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    User usersData = dataSnapshot.child(parentDbName).child(phone).getValue(User.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            if (parentDbName.equals("User")) {
                                Toast.makeText(MainActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        } else {

                            Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}