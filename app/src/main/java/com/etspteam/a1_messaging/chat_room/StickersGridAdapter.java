package com.etspteam.a1_messaging.chat_room;

import java.io.InputStream;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.etspteam.a1_messaging.R;

public class StickersGridAdapter extends BaseAdapter {

    private ArrayList<String> paths;
    private Activity mContext;
    private KeyClickListener mListener;

    StickersGridAdapter(Activity context, ArrayList<String> paths, KeyClickListener listener) {
        this.mContext = context;
        this.paths = paths;
        this.mListener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.sticker_item, null);
        }
        final String path = paths.get(position);
        ImageView image = (ImageView) v.findViewById(R.id.item);
        image.setImageBitmap(getImage(path));
        if (path.charAt(8) == '3') {
            image.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen.emotion_height));
            image.setMinimumWidth((int) mContext.getResources().getDimension(R.dimen.emotion_width));
        } else {
            image.setMinimumHeight((int) mContext.getResources().getDimension(R.dimen.sticker_height));
            image.setMinimumWidth((int) mContext.getResources().getDimension(R.dimen.sticker_width));
        }
        image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.keyClickedIndex(path);
            }
        });
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatActivity) mContext).sendMessage(path, "sticker");
            }
        });
        return v;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public String getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Bitmap getImage(String path) {
        AssetManager mngr = mContext.getAssets();
        InputStream in = null;
        try {
            in = mngr.open(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(in, null, null);
    }

    public interface KeyClickListener {
        void keyClickedIndex(String index);
    }
}
