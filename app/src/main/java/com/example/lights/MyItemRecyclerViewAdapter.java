package com.example.lights;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lights.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private ImageView colorPicker;
    private SeekBar brightnessBar, whiteContentBar;
    private int r, g, b, w, whiteContent;
    private float brightness = 1;

    private final List<DummyItem> mValues;

    public MyItemRecyclerViewAdapter(List<DummyItem> items) {
        mValues = items;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        colorPicker = view.findViewById(R.id.colorBar);
        brightnessBar = view.findViewById(R.id.brightnessBar);
        whiteContentBar = view.findViewById(R.id.whiteContentBar);

        colorPicker.setDrawingCacheEnabled(true);
        colorPicker.buildDrawingCache(true);

        colorPicker.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Bitmap bitmap = colorPicker.getDrawingCache();
                    int x = (int)motionEvent.getX();
                    if(x < bitmap.getWidth() && x >= 0) {
                        int pixel = bitmap.getPixel(x,bitmap.getHeight()/2);
                        int tempR = Color.red(pixel);
                        int tempG = Color.green(pixel);
                        int tempB = Color.blue(pixel);
                        if(Color.rgb(tempR,tempG,tempB) != Color.BLACK) {
                            if(Color.rgb(tempR,tempG,tempB) == Color.WHITE){
                                r = 0;
                                g = 0;
                                b = 0;
                                w = 1023;
                                whiteContent = 1023;
                                whiteContentBar.setProgress(1023, true);
                            }else {
                                r = tempR*4;//*4 bo sterownik działa od 0-1023 a pixele są opisywane od 0 do 255
                                g = tempG*4;
                                b = tempB*4;
                                w = 0;
                                whiteContent = 0;
                                whiteContentBar.setProgress(0, true);
                            }
                            brightnessBar.setThumbTintList(ColorStateList.valueOf(Color.rgb(tempR,tempG,tempB)));
                            brightnessBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(tempR,tempG,tempB)));
                            System.out.println("Color: " + r + " " + g + " " + b + " " + w + " " + brightness);
                        }
                    }
                }
                return true;
            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightness =(float)((float)i/100.0);
                System.out.println(brightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        whiteContentBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                whiteContent = i;
                System.out.println(whiteContent);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
       // holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}