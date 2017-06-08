package com.etspteam.a1_messaging.login_signup;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.etspteam.a1_messaging.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private DatabaseReference root;

    @InjectView(R.id.input_name)
    EditText nameText;
    @InjectView(R.id.input_password)
    EditText passwordText;
    @InjectView(R.id.re_input_password)
    EditText repasswordText;
    @InjectView(R.id.btn_signup)
    Button signupButton;
    @InjectView(R.id.link_login)
    TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkgreen));
        }
        ButterKnife.inject(this);

        TextView title = (TextView) findViewById(R.id.a1_messaging);
        Typeface type = Typeface.createFromAsset(getAssets(),"font/DancingScript-Regular.ttf");
        title.setTypeface(type);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
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

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        if (!isOnline()) {
            Toast.makeText(getBaseContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
            return;
        }
        final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .backgroundColor(getResources().getColor(R.color.darkergreen))
                .content("   Đang tạo tài khoản")
                .canceledOnTouchOutside(false)
                .progress(true, 0).show();
        progressDialog.show();

        final String name = nameText.getText().toString();
        final String password = passwordText.getText().toString();

        root = FirebaseDatabase.getInstance().getReference().child("users");
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(name)) {
                    Toast.makeText(getBaseContext(), "Tên đăng nhập đã được sử dụng", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    onSignupSuccess();
                    DatabaseReference users_root = root.child(name);
                    Map<String, Object> map = new HashMap<>();
                    map.put("user_name", name);
                    map.put("password", password);
                    users_root.updateChildren(map);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        FragmentManager manager = getSupportFragmentManager();
        SuccessfulSignUpFragment dialog = new SuccessfulSignUpFragment();
        dialog.show(manager, "Đăng ký thành công");
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Đăng ký thất bại", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String password = passwordText.getText().toString();
        String repassword = repasswordText.getText().toString();

        boolean temp = true;
        for (char c : name.toCharArray()) {
            if (c < 65 || (c > 90 && c < 97) || c > 122) {
                temp = false;
                break;
            }
        }
        if (name.isEmpty() || !temp) {
            if (name.isEmpty()) nameText.setError("Tên đăng nhập không được bỏ trống");
            else nameText.setError("Tên đăng nhập chỉ gồm các chữ cái");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            passwordText.setError("Mật khẩu dài 4 - 20 kí tự");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (!repassword.equals(password)) {
            repasswordText.setError("Mật khẩu không khớp");
            valid = false;
        } else {
            repasswordText.setError(null);
        }

        return valid;
    }

}
