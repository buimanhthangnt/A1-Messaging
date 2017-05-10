package com.etspteam.a1_messaging.chat_room;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.etspteam.a1_messaging.chat_room.StickersGridAdapter.KeyClickListener;
import com.etspteam.a1_messaging.R;

class StickersPagerAdapter extends PagerAdapter {
    private Activity mActivity;
    private KeyClickListener mListener;

    StickersPagerAdapter(Activity activity, StickersGridAdapter.KeyClickListener listener) {
        this.mActivity = activity;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        int numberOfStickers[] = {20, 32, 24, 19, 31, 19};
        String temp = "stickers" + Integer.toString(position + 1) + "/";
        View layout = mActivity.getLayoutInflater().inflate(R.layout.sticker_grid, null);
        ArrayList<String> emoticonsInAPage = new ArrayList<>();
        for (int i = 0; i < numberOfStickers[position]; i++) {
            emoticonsInAPage.add(temp + "a" + (i + 1) + ".png");
        }
        GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
        StickersGridAdapter adapter = new StickersGridAdapter(mActivity, emoticonsInAPage, mListener);
        grid.setAdapter(adapter);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
