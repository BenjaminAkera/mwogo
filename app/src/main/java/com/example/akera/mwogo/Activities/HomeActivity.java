package com.example.akera.mwogo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.akera.mwogo.R;
import com.example.akera.mwogo.UtilityClasses.ProjectConstants;
import com.example.akera.mwogo.UtilityClasses.pojoUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser currentUser;

    Button logOutButton,editProfileBtn;
    TextView nameTv,emailTv,genderTv,dobTv,contactTv;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createProgressDialog();
        showProgressDialog("Setting up things for you");
        initFirebase();
        checkLoginStatus();
        initLayoutElements();
        getCurrentUserData();
        setListeners();

    }

    private void getCurrentUserData() {
        databaseReference.child(currentUser.getUid()).child(ProjectConstants.USER_PROFILE_NODE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                setDataToUI(dataSnapshot.getValue(pojoUser.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
//                Toast.makeText(HomeActivity.this, "Unable to fetch Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToUI(pojoUser value) {
        if(value!=null){
            nameTv.setText("Name : "+value.getName());
            emailTv.setText("E-Mail : "+value.getEmail());
            contactTv.setText("Contact : "+value.getContact());
            dobTv.setText("Date of Birth : "+value.getDob());
            genderTv.setText("Gender : "+value.getGender());
        }
    }

    private void setListeners() {
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,UpdateProfileActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();
            }
        });
    }

    private void initLayoutElements() {
        logOutButton = findViewById(R.id.btn_log_out);
        editProfileBtn = findViewById(R.id.btn_edit_profile);
        nameTv = findViewById(R.id.tv_name);
        emailTv = findViewById(R.id.tv_email);
        contactTv = findViewById(R.id.tv_contact);
        dobTv = findViewById(R.id.tv_dob);
        genderTv = findViewById(R.id.tv_gender);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(ProjectConstants.USER_NODE);
    }

    private void checkLoginStatus() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser==null){
                    Intent i = new Intent(HomeActivity.this,LoginActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    finish();
                }
            }
        };
    }

    private void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void showProgressDialog(String msg){
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.dismiss();
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        super.onBackPressed();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
