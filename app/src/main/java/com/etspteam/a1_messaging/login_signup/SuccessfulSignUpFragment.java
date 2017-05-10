package com.etspteam.a1_messaging.login_signup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.etspteam.a1_messaging.R;

public class SuccessfulSignUpFragment extends DialogFragment {


    public SuccessfulSignUpFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle("Đã đăng ký thành công. Đăng nhập để tiếp tục sử dụng.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        getActivity().finish();
                    }
                }).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(R.color.darkergreen);
        return dialog;
    }
}
