package com.example.akera.mwogo.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akera.mwogo.R;
import com.example.akera.mwogo.UtilityClasses.ProjectConstants;
import com.example.akera.mwogo.UtilityClasses.pojoUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateProfileActivity extends AppCompatActivity {

    private TextView welcomeTv;
    private Button updateProfileBtn;
    private EditText nameEt,emailEt,dobEt,contactEt;
    private RadioGroup genderRadioGrp;
    private RadioButton genderRadioBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private FirebaseUser currentUser;
    private pojoUser userData;

    private ProgressDialog progressDialog;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initFirebase();
        checkLoginStatus();
        initLayoutElements();
        getCurrentUserData();
        addDatePicker();
        createProgressDialog();
        setListeners();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(ProjectConstants.USER_NODE);
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

    private void getCurrentUserData() {
        if(currentUser!=null){
            userData = new pojoUser();
            userData.setUsername(currentUser.getDisplayName());
            userData.setEmail(currentUser.getEmail());
            setDataTOEditText();
        }
    }

    private void setDataTOEditText() {
        if(userData.getUsername()!=null){
            welcomeTv.setText("Welcome " + userData.getUsername());
            nameEt.setText(userData.getUsername());
            nameEt.setEnabled(false);
        }
        if(userData.getEmail()!=null){
            emailEt.setText(userData.getEmail());
            emailEt.setEnabled(false);
        }
    }

    private void checkLoginStatus() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser==null){
                    Intent i = new Intent(UpdateProfileActivity.this,LoginActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                    finish();
                }
            }
        };
    }

    private void initLayoutElements() {
        welcomeTv = findViewById(R.id.tv_welcome);
        updateProfileBtn = findViewById(R.id.btn_update_profile);
        nameEt = findViewById(R.id.et_name);
        emailEt = findViewById(R.id.et_email);
        dobEt = findViewById(R.id.et_dob);
        contactEt = findViewById(R.id.et_contact);
        genderRadioGrp = findViewById(R.id.radio_grp_gender);
    }

    private void addDatePicker() {
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dobEt.setText(sdf.format(myCalendar.getTime()));
    }

    private void setListeners() {
        //set datepicker to dob edittext
        dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeToDB();
            }
        });
    }

    private void storeToDB() {
        userData = readUserInputData();
        if(userData!=null){
            showProgressDialog("Updating Profile");
            databaseReference.child(currentUser.getUid()).child(ProjectConstants.USER_PROFILE_NODE).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressDialog();
                    if(task.isSuccessful()){
                        databaseReference.child(currentUser.getUid()).child(ProjectConstants.USER_PROFILE_IS_SET_NODE).setValue("1");
                        updateUI();
                    }
                    else {
                        databaseReference.child(currentUser.getUid()).child(ProjectConstants.USER_PROFILE_IS_SET_NODE).setValue("0");
                        Toast.makeText(UpdateProfileActivity.this, "Unable to Update Profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private pojoUser readUserInputData() {

        String name = nameEt.getText().toString();
        String email = emailEt.getText().toString();
        String contact = contactEt.getText().toString();
        String dob = dobEt.getText().toString();

        int i = genderRadioGrp.getCheckedRadioButtonId();
        genderRadioBtn = findViewById(i);

        String gender = genderRadioBtn.getText().toString();

        pojoUser user = null;

        if(isInputValid(name,email,contact,dob,gender)){
            user = new pojoUser();
            user.setName(name);
            user.setEmail(email);
            user.setDob(dob);
            user.setContact(contact);
            user.setGender(gender);
        }
        return user;
    }

    private boolean isInputValid(String name, String email, String contact, String dob, String gender) {

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Name is Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "E-Mail is Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(contact)){
            Toast.makeText(this, "Contact is Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(dob)){
            Toast.makeText(this, "Date of Birth is Required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Gender is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!TextUtils.isDigitsOnly(contact) || contact.length()!=10){
            Toast.makeText(this, "Contact is inValid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "E-Mail is inValid", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void updateUI(){
        Intent i = new Intent(UpdateProfileActivity.this,HomeActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}
