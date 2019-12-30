package com.dualbrotech.tourmate.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dualbrotech.tourmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView underlineText;
    private EditText etEmail, etPassword;
    private Button btnSignIn;

    private Context context;

    FirebaseAuth mAuth;
    FirebaseUser user;

    SharedPreferences preferences;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        editor = preferences.edit();

        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        if (user != null){
//            Intent intent = new Intent(context,EventsActivity.class);
//            startActivity(intent);
//        }

        boolean isSignedIn = preferences.getBoolean("signedIn",false);
        if (isSignedIn){
            Intent intent = new Intent(context,EventsActivity.class);
            startActivity(intent);
        }

        underlineText = findViewById(R.id.tv_signUp);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_signIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.isEmpty() && !email.contains("@")){
                    etEmail.setError("Enter valid email");
                }else if (password.isEmpty()){
                    etPassword.setError("Enter Password");
                }else {
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){
                                        editor.clear();
                                        editor.putBoolean("signedIn",true);
                                        editor.commit();
                                        Toast.makeText(LoginActivity.this,"Loged in.",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this,EventsActivity.class);
                                        startActivity(intent);

                                    }else {
                                        Toast.makeText(LoginActivity.this,"Failed to log in.",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }


            }
        });

        underlineText.setPaintFlags(underlineText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void signUp(View view) {
        Intent signUp = new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(signUp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
