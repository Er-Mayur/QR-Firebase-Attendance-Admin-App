package com.ermayurmahajan.mcoeadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText edtForgetPasswordEmail;
    private Button btnSendLink;
    private String textForgetPasswordEmail;
    private Boolean isDialogBoxIsDisplay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_activity);
        getSupportActionBar().hide();

        edtForgetPasswordEmail = findViewById(R.id.edt_email);
        btnSendLink = findViewById(R.id.btn_send_link);

        btnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textForgetPasswordEmail = edtForgetPasswordEmail.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(textForgetPasswordEmail).matches()) {
                    edtForgetPasswordEmail.setError("Enter Valid Email");
                    edtForgetPasswordEmail.requestFocus();
                }else {
                    forgetPasswordSendLink();
                }
            }
        });
    }

    private void forgetPasswordSendLink() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(textForgetPasswordEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendVerificationEmailDialogBox();
                }else {
                    edtForgetPasswordEmail.setError("User does not exist");
                    edtForgetPasswordEmail.requestFocus();
                }
            }
        });
    }
    private void sendVerificationEmailDialogBox(){
        Dialog dialog = new Dialog(ForgetPasswordActivity.this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.setCancelable(false);
        Button btnDialogContinue = dialog.findViewById(R.id.btn_dialog_continue);
        TextView txtDialogBoxTitle = dialog.findViewById(R.id.txt_dialog_box_title);
        TextView txtDialogBoxContext = dialog.findViewById(R.id.txt_dialog_box_context);

        txtDialogBoxTitle.setText("Reset Password");
        txtDialogBoxContext.setText("Reset Password link is send. Please check your email.");
        //set Size
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        btnDialogContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                isDialogBoxIsDisplay = true;
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (isDialogBoxIsDisplay){
            Toast.makeText(this, "Now login with new password", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}