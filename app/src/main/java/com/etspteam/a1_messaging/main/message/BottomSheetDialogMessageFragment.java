package com.etspteam.a1_messaging.main.message;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.contact.ListMember;

public class BottomSheetDialogMessageFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_dialog_message, null);
        ImageView profile = (ImageView) contentView.findViewById(R.id.selected_profile);
        TextView name = (TextView) contentView.findViewById(R.id.selected_name_person);
        TextView phone = (TextView) contentView.findViewById(R.id.selected_phone_number);
        ListMember.Member member = ListMember.getList().get(getActivity().getIntent().getIntExtra("com.etspteam.selectedIndex", 0));
        profile.setImageDrawable(getResources().getDrawable(member.idImage));
        name.setText(member.name);
        phone.setText("SĐT: " + member.phoneNuber);
        dialog.setContentView(contentView);
    }
}
