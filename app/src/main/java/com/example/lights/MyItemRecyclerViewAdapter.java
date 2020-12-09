package com.example.lights;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Group> mValues;

    public MyItemRecyclerViewAdapter(List<Group> items) {
        mValues = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.colorPicker.setDrawingCacheEnabled(true);
        holder.colorPicker.buildDrawingCache(true);

        holder.colorPicker.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    Bitmap bitmap = holder.colorPicker.getDrawingCache();
                    int x = (int)motionEvent.getX();
                    if(x < bitmap.getWidth() && x >= 0) {
                        int pixel = bitmap.getPixel(x,bitmap.getHeight()/2);
                        int tempR = Color.red(pixel);
                        int tempG = Color.green(pixel);
                        int tempB = Color.blue(pixel);
                        if(Color.rgb(tempR,tempG,tempB) != Color.BLACK) {
                            if(Color.rgb(tempR,tempG,tempB) == Color.WHITE){
                                holder.r = 0;
                                holder.g = 0;
                                holder.b = 0;
                                holder.w = 1023;
                                holder.whiteContentBar.setProgress(1023, true);
                            }else {
                                holder.r = tempR*4;//*4 bo sterownik działa od 0-1023 a pixele są opisywane od 0 do 255
                                holder.g = tempG*4;
                                holder.b = tempB*4;
                                holder.w = 0;
                                holder.whiteContentBar.setProgress(holder.w, true);
                            }

                            holder.updateDials();
                            holder.brightnessBar.setThumbTintList(ColorStateList.valueOf(Color.rgb(tempR,tempG,tempB)));
                            holder.brightnessBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(tempR,tempG,tempB)));

                            System.out.println("Group: " + holder.mItem.getName() + " Color: " + holder.r + " " + holder.g + " " + holder.b + " " + holder.w + " " + holder.brightness);

                            String color = holder.r + "_" + holder.g + "_" + holder.b + "_" + holder.w + "_" + holder.brightness;
                            if(!holder.mItem.getLights().isEmpty()) {
                                String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=" + color, "groupId=" + holder.mItem.getId());
                                holder.sendMessage(url);
                            }
                        }
                    }
                }
                return true;
            }
        });

        holder.brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.brightness =(float)((float)i/100.0);

                if(holder.r == 0 && holder.g == 0 && holder.b == 0 && holder.w == 0) {
                    holder.w = 1023;
                    holder.brightnessBar.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
                    holder.brightnessBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
                    holder.whiteContentBar.setProgress(holder.w, true);
                    holder.updateDials();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String color = holder.r + "_" + holder.g + "_" + holder.b + "_" + holder.w + "_" + holder.brightness;
                if(!holder.mItem.getLights().isEmpty()) {
                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=" + color, "groupId=" + holder.mItem.getId());
                    holder.sendMessage(url);
                }
                holder.updateDials();
            }
        });

        holder.whiteContentBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                holder.w = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String color = holder.r + "_" + holder.g + "_" + holder.b + "_" + holder.w + "_" + holder.brightness;
                if(!holder.mItem.getLights().isEmpty()) {
                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=" + color, "groupId=" + holder.mItem.getId());
                    holder.sendMessage(url);
                }
                holder.updateDials();
            }
        });

        holder.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                holder.buttonFade.setTextColor(Color.WHITE);
                holder.buttonBreathe.setTextColor(Color.WHITE);
                if(!holder.mItem.getLights().isEmpty()) {
                    String switchState = "";

                    if(isChecked){
                        switchState = "ON";
                    } else {
                        switchState = "OFF";
                    }

                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=" + switchState, "groupId=" + holder.mItem.getId());
                    holder.sendMessage(url);
                }
            }
        });

        holder.buttonBreathe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.updateDials();
                String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=BREATHE", "groupId=" + holder.mItem.getId());
                holder.sendMessage(url);
                holder.buttonBreathe.setTextColor(Color.GREEN);
            }
        });

        holder.buttonFade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.updateDials();
                String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=FADE", "groupId=" + holder.mItem.getId());
                holder.sendMessage(url);
                holder.buttonFade.setTextColor(Color.GREEN);
            }
        });

        holder.mContentView.setText(mValues.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Group mItem;

        private final ImageView colorPicker;
        private final SeekBar brightnessBar, whiteContentBar;
        private final Switch onOffSwitch;
        private final Button buttonBreathe, buttonFade;
        private int r = 0, g = 0, b = 0, w = 0, whiteContent = 0;
        private float brightness = 1;
        NetworkHandler networkHandler;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            colorPicker = view.findViewById(R.id.colorBar);
            brightnessBar = view.findViewById(R.id.brightnessBar);
            whiteContentBar = view.findViewById(R.id.whiteContentBar);
            onOffSwitch = view.findViewById(R.id.switchOnOff);
            buttonBreathe = view.findViewById(R.id.buttonBreathe);
            buttonFade = view.findViewById(R.id.buttonFade);
            networkHandler = new NetworkHandler();

        }

        private void sendMessage(String url){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("That didn't work!");
                    Log.e("Volly Error", error.toString());
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    }
                }
            });
            RequestQueue queue = Volley.newRequestQueue(mView.getContext());
            queue.add(stringRequest);
        }

        private void updateDials(){
            onOffSwitch.setChecked(true);
            buttonFade.setTextColor(Color.WHITE);
            buttonBreathe.setTextColor(Color.WHITE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }





}