package com.etspteam.a1_messaging.main.contact;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.database.DataApplicationHelper;

public class BottomSheetDialogContactFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_dialog_contact, null);
        ImageView profile = (ImageView) contentView.findViewById(R.id.contact_selected_profile);
        TextView name = (TextView) contentView.findViewById(R.id.contact_selected_name_person);
        TextView phone = (TextView) contentView.findViewById(R.id.contact_selected_phone_number);
        TextView markCloseFriend = (TextView) contentView.findViewById(R.id.mark_as_close_friend);
        final int index = getActivity().getIntent().getIntExtra("com.etspteam.contactSelectedIndex", 0);
        if (ContactFragment.listFavorite[index] == 1) {
            markCloseFriend.setText("Bỏ đánh dấu là bạn thân");
        }
        ListMember.Member member = ListMember.getList().get(index);
        profile.setImageDrawable(getResources().getDrawable(member.idImage));
        name.setText(member.name);
        phone.setText("SĐT: " + member.phoneNuber);
        LinearLayout option1 = (LinearLayout) contentView.findViewById(R.id.contact_option1);
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout option2 = (LinearLayout) contentView.findViewById(R.id.contact_option2);
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = new DataApplicationHelper(getContext()).getWritableDatabase();
                ContentValues values = new ContentValues();
                String i = Integer.toString(index);
                values.put("indexMember", i);
                if (ContactFragment.listFavorite[index] == 0) {
                    values.put("isFavorite", "1");
                    ContactFragment.listFavorite[index] = 1;
                } else {
                    values.put("isFavorite", "0");
                    ContactFragment.listFavorite[index] = 0;
                }
                database.update("favorite", values, "indexMember" + " = ?", new String[]{i});
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ContactFragment fragment = (ContactFragment) fm.findFragmentById(R.id.container);
                fragment.updateListFavorite();
                BottomSheetDialogContactFragment.this.dismiss();
            }
        });
        dialog.setContentView(contentView);
    }
}
