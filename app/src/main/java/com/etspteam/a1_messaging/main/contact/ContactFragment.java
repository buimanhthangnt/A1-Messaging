package com.etspteam.a1_messaging.main.contact;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.database.DataApplicationHelper;
import com.etspteam.a1_messaging.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    public static int[] listFavorite = new int[48];
    private RecyclerView favoriteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        ((MainActivity) getActivity()).setToolbarAndStatusBarColor(R.color.gmail, R.color.gmail_dark);
        SQLiteDatabase database = new DataApplicationHelper(getActivity()).getWritableDatabase();
        Cursor cursor = database.query("favorite", null, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            int count = 0;
            while (!cursor.isAfterLast()) {
                listFavorite[count] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("isFavorite")));
                count++;
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_member);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MemberAdapter(getContext(), MainActivity.getListWithoutUser()));

        favoriteList = (RecyclerView) v.findViewById(R.id.favorite_list);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        favoriteList.setLayoutManager(layoutManager2);
        updateListFavorite();
        return v;
    }

    public void updateListFavorite() {
        List<ListMember.Member> list_favorite = new ArrayList<>();
        for (int i = 0; i < listFavorite.length; i++) {
            if (listFavorite[i] == 1) list_favorite.add(ListMember.getList().get(i));
        }
        favoriteList.setAdapter(new FavoriteFriendAdapter(getContext(), list_favorite));
    }
}
