package com.homanhuang.tomtomtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomtom.online.sdk.map.BaseMarkerBalloonLayout;
import com.tomtom.online.sdk.map.Marker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Homan on 2/26/2018.
 */

public class HomanMarkerLayoutBalloon extends BaseMarkerBalloonLayout {

    private Bitmap balloonImage;
    private String tag;

    public HomanMarkerLayoutBalloon(int i, Bitmap balloonImage) {
        super(i);
        this.balloonImage = balloonImage;
    }

    @Override
    public void onBindView(View view, Marker marker) {
        ImageView imageView = (ImageView) view.findViewById(R.id.balloonImageView);
        TextView textView = (TextView) view.findViewById(R.id.balloonTextView);

        imageView.setImageBitmap(balloonImage);

        tag = marker.getTag().toString();
        textView.setText(tag);
    }

    public void setText(String tag) {
        this.tag = tag;
    }

    @NonNull
    @Override
    public Point getBalloonOffset(Marker marker) {
        //-this.a.getWidth() / 2, -this.a.getHeight()):new Point(this.b(var1), this.a(var1)
        int width = 150;
        int height = 50;
        int mWidth = marker.getIconWidth();
        int mHeight = marker.getIconHeight();
        return new Point(-width-(mWidth/2), -mHeight*2-height/2);
    }

}
