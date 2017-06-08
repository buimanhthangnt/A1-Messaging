package com.etspteam.a1_messaging.login_signup;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.database.DataApplicationHelper;
import com.etspteam.a1_messaging.main.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_username)
    EditText nameText;
    @InjectView(R.id.input_password)
    EditText passwordText;
    @InjectView(R.id.btn_login)
    Button loginButton;
    @InjectView(R.id.link_signup)
    TextView signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.facebook));
        }
        ButterKnife.inject(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        TextView title = (TextView) findViewById(R.id.a1_messaging);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/DancingScript-Regular.ttf");
        title.setTypeface(type);

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String name = nameText.getText().toString();
        final String password = passwordText.getText().toString();

        if (!isOnline()) {
            Toast.makeText(getBaseContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }

        final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .backgroundColor(getResources().getColor(R.color.darkerFacebook))
                .content("   Đang kiểm tra")
                .canceledOnTouchOutside(false)
                .progress(true, 0).show();
        progressDialog.show();

        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("users");
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(name)) {
                    HashMap info = (HashMap) dataSnapshot.child(name).getValue();
                    String passwordCheck = (String) info.get("password");
                    String user_name_check = (String) info.get("user_name");
                    if (user_name_check.equals(name) && passwordCheck.equals(password)) {
                        SQLiteDatabase mDatabase = new DataApplicationHelper(getBaseContext()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("key", "user_name");
                        values.put("value", name);
                        mDatabase.update("settings", values, "key" + " = ?", new String[]{"user_name"});
                        progressDialog.dismiss();
                        onLoginSuccess();
                    } else {
                        Toast.makeText(getBaseContext(), "Mật khẩu không chính xác", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } else {
                    Toast.makeText(getBaseContext(), "Tài khoản không tồn tại", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Đăng nhập thành công", Toast.LENGTH_LONG).show();
        setResult(1);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Đăng nhập thất bại", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String password = passwordText.getText().toString();
        boolean temp = true;
        for (char c : name.toCharArray()) {
            if (c < 65 || (c > 90 && c < 97) || c > 122) {
                temp = false;
                break;
            }
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10 || !Patterns.PHONE.matcher(password).matches()) {
            passwordText.setError("Mật khẩu dài 4 - 20 kí tự");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (name.isEmpty() || !temp) {
            if (name.isEmpty()) nameText.setError("Tên đăng nhập không được bỏ trống");
            else nameText.setError("Tên đăng nhập chỉ gồm các chữ cái");
            valid = false;
        } else {
            nameText.setError(null);
        }

        return valid;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
