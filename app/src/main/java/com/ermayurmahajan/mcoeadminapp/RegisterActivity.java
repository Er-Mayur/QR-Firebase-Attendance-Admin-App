package com.ermayurmahajan.mcoeadminapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TeacherReadWriteDetails writeDetails;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference teachersRef;
    private ImageView imgRegisterShowHidePassword;
    private  static final String TAG = "RegisterActivity";
    private EditText edtTeacherFullName, edtDOB, edtTeacherMobileNumber, edtEmail,edtPassword, edtConfirmPassword;
    private DatePickerDialog picker;

    private LottieAnimationView loading;
    RadioGroup radBtnGender;
    RadioButton radBtnGenderSelected;
    String textGenderSelected ,textTeacherFullName, textDOB, textTeacherMobileNumber = null, textEmail, textPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        getSupportActionBar().hide();

        Toast.makeText(this, "Register Now", Toast.LENGTH_SHORT).show();

        //Edit View ID
        edtTeacherFullName = findViewById(R.id.edt_teacher_full_name);
        edtDOB = findViewById(R.id.edt_DOB);
        edtTeacherMobileNumber = findViewById(R.id.edt_teacher_mobile_number);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        imgRegisterShowHidePassword = findViewById(R.id.img_register_show_hide_password);

        loading = findViewById(R.id.loading);

        String mobileRegex = "[6-9][0-9]{9}"; //1st No. can be {6, 7, 8, 9} and rest 9 nos. can be any no.
        Pattern mobilePattern = Pattern.compile(mobileRegex);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        teachersRef = database.getReference("Teachers");

        //Radio Button Id
        radBtnGender = findViewById(R.id.rad_btn_gender);

        radBtnGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                ((RadioButton) findViewById(R.id.rad_btn_male)).setError(null);
                ((RadioButton) findViewById(R.id.rad_btn_female)).setError(null);
            }
        });

        //Set Image of Hidden Password
        imgRegisterShowHidePassword.setImageResource(R.drawable.outline_visibility_off_24);
        imgRegisterShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Icon
                    imgRegisterShowHidePassword.setImageResource(R.drawable.outline_visibility_off_24);
                } else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgRegisterShowHidePassword.setImageResource(R.drawable.outline_visibility_24);
                }
            }
        });

        //Setting Up DatePicker on edtDOB
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDOB.setError(null);
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        edtDOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radBtnGender.getCheckedRadioButtonId();
                radBtnGenderSelected = findViewById(selectedGenderId);

                //Obtain the student entered data
                textTeacherFullName = edtTeacherFullName.getText().toString().toUpperCase();
                textDOB = edtDOB.getText().toString();
                textTeacherMobileNumber = edtTeacherMobileNumber.getText().toString();
                textEmail = edtEmail.getText().toString();
                textPassword = edtPassword.getText().toString();
                String textConfirmPassword = edtConfirmPassword.getText().toString();

                Matcher studentMobileMatcher;
                studentMobileMatcher = mobilePattern.matcher(textTeacherMobileNumber);
                //Checking information is Not empty
                if (TextUtils.isEmpty(textTeacherFullName)) {
                    edtTeacherFullName.setError("Full Name is required");
                    edtTeacherFullName.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    edtDOB.setError("Enter DOB");
                    edtDOB.requestFocus();

                } else if (radBtnGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    ((RadioButton) findViewById(R.id.rad_btn_male)).setError(" ");
                    ((RadioButton) findViewById(R.id.rad_btn_female)).setError(" ");
                    radBtnGender.requestFocus();
                }else if (TextUtils.isEmpty(textTeacherMobileNumber)) {
                    edtTeacherMobileNumber.setError("Student Mobile Number is required");
                    edtTeacherMobileNumber.requestFocus();
                } else if (!studentMobileMatcher.find() || textTeacherMobileNumber.length() != 10) {
                    edtTeacherMobileNumber.setError("Mobile No. is Invalid");
                    edtTeacherMobileNumber.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    edtEmail.setError("Enter Valid Email");
                    edtEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    edtPassword.setError("Password is required");
                    edtPassword.requestFocus();
                } else if (textPassword.length() < 6) {
                    edtPassword.setError("Password is too weak at least 6 digit");
                    edtPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    edtConfirmPassword.setError("Password Confirmation is required");
                    edtConfirmPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    edtConfirmPassword.setError("Password doses not match");
                    edtConfirmPassword.requestFocus();
                    //Clear the confirm password
                } else {
                    textGenderSelected = radBtnGenderSelected.getText().toString();
                    loading.setVisibility(View.VISIBLE);
                    registerNewTeacher(textEmail, textPassword);
                }
            }
        });

    }

    private void registerNewTeacher(String textEmail, String textPassword){
        //Creating User Profile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Enter User Data into the Firebase Realtime Database
                    writeDetails = new TeacherReadWriteDetails(textTeacherFullName, textDOB, textGenderSelected,  textTeacherMobileNumber, textEmail);
                    firebaseUser = auth.getCurrentUser();
                    userRef = teachersRef.child("AIML");

                    userRef.child(firebaseUser.getUid()).setValue(writeDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                //Open user profile after successful registration
                                Intent intent = new Intent(RegisterActivity.this, AdminMainActivity.class);

                                //To Prevent user from returning back to register Activity on pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // to close Register Activity
                            } else {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Registration Failed. Please try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    }catch (FirebaseAuthWeakPasswordException e){
                        edtPassword.setError("Your password is too weak. Kindly use a mix of alphabet, number & spacial characters");
                        edtPassword.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        edtEmail.setError("Your email is invalid or already in use. Kindly re-enter");
                        edtEmail.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        edtEmail.setError("User is already register with this email. Use another email");
                        edtEmail.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }finally {
                        loading.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}