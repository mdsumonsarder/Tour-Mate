package com.dualbrotech.tourmate.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dualbrotech.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity {
    private Button btnRegister;
    private EditText etGetEmail, etGetPassword, etGetName;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btn_register);
        etGetEmail = findViewById(R.id.et_getEmail);
        etGetName = findViewById(R.id.et_name);
        etGetPassword = findViewById(R.id.et_getPassword);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etGetEmail.getText().toString();
                String password = etGetPassword.getText().toString();
                final String name = etGetName.getText().toString();
                if (email.isEmpty() || !email.contains("@")){
                    etGetEmail.setError("Enter valid email");
                }else if (password.isEmpty() || password.length()<6) {
                    etGetPassword.setError("Min length 6");
                }else {
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name).build();
                                        user.updateProfile(profileUpdates);

                                        Toast.makeText(SignupActivity.this,"User Added",Toast.LENGTH_LONG).show();
                                        etGetEmail.setText("");
                                        etGetName.setText("");
                                        etGetPassword.setText("");

                                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("user",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putBoolean("signedIn",false);
                                        editor.commit();

                                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(SignupActivity.this,"Failed to add user.",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }

            }
        });


    }

    public boolean passwordHasNumber(String password){
        boolean flag = false;
        //password;
        for (int i=0; i<password.length(); i++){
            char ch = password.charAt(i);
            if (Character.isDigit(ch)){
                flag = true;
            }
        }
        return flag;
    }

}
