package com.example.lights;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.zip.Inflater;

public class MyDeviceRecyclerViewAdapter extends RecyclerView.Adapter<MyDeviceRecyclerViewAdapter.ViewHolder> {


    private final List<Light> mValues;
    private final List<Group> mGroups;
    public int user_id;


    Context ctx;



    public MyDeviceRecyclerViewAdapter(List<Light> items, List<Group> groupp, Context context) {
        mValues = items;
        mGroups = groupp;
        this.ctx = context;
        user_id = mGroups.get(0).getUserId();



    }




    @NotNull
    @Override
    public MyDeviceRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new MyDeviceRecyclerViewAdapter.ViewHolder(view);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final MyDeviceRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);



        holder.colorPicker.setDrawingCacheEnabled(true);
        holder.colorPicker.buildDrawingCache(true);

        holder.colorPicker.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    long timeTouched = SystemClock.elapsedRealtime(); //Pobranie czasu motionEventu
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

                            if((timeTouched - holder.timeOfLastColorSandedSuccessfully) > holder.timeIntervalToSendMessage) { //Wysyłanie żądań na serwer co conajmniej 250ms (zmienna stała w holderze)

                                System.out.println("Group: " + holder.mItem.getName() + " Color: " + holder.r + " " + holder.g + " " + holder.b + " " + holder.w + " " + holder.brightness);

                                String color = holder.r + "_" + holder.g + "_" + holder.b + "_" + holder.w + "_" + holder.brightness;
                                if (!holder.mItem.getSerial().isEmpty()) {
                                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfoOne", "message=" + color, "serial=" + holder.mItem.getSerial());
                                    holder.sendMessage(url);
                                }
                                holder.timeOfLastColorSandedSuccessfully = timeTouched; //Zapisanie do zmiennej holdera kiedy ostatnio wysłano kolor na serwer
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
                if(!holder.mItem.getSerial().isEmpty()) {
                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfoOne", "message=" + color, "serial=" + holder.mItem.getSerial());
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
                if(!holder.mItem.getSerial().isEmpty()) {
                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfoOne", "message=" + color, "serial=" + holder.mItem.getSerial());
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
                if(!holder.mItem.getSerial().isEmpty()) {
                    String switchState = "";

                    if(isChecked){
                        switchState = "ON";
                    } else {
                        switchState = "OFF";
                    }

                    String url = holder.networkHandler.makeUrl("/mqtt/sendInfoOne", "message=" + switchState, "serial=" + holder.mItem.getSerial());
                    holder.sendMessage(url);
                }
            }
        });

        holder.buttonBreathe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.updateDials();
                String url = holder.networkHandler.makeUrl("/mqtt/sendInfo", "message=BREATHE", "serial=" + holder.mItem.getSerial());
                holder.sendMessage(url);
                holder.buttonBreathe.setTextColor(Color.GREEN);
            }
        });

        holder.buttonFade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.updateDials();
                String url = holder.networkHandler.makeUrl("/mqtt/sendInfoOne", "message=FADE", "groupId=" + holder.mItem.getSerial());
                holder.sendMessage(url);
                holder.buttonFade.setTextColor(Color.GREEN);
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_layout);


                Button buttonDeleteDialog = (Button) dialog.findViewById(R.id.buttonDeleteAlert);
                Button buttonBackDialog = (Button) dialog.findViewById(R.id.buttonBackAlert);

                buttonDeleteDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Usuwanie");
                        String url = holder.networkHandler.makeUrl("/lights/removeLight", "serial=" + holder.mItem.getSerial(), "user_id=" + user_id);
                        holder.sendDeleteMessage(url);
                        Intent intent = new Intent(ctx, ModelPanel.class);
                        intent.putExtra("idUser", Integer.toString(user_id));
                        ctx.startActivity(intent);
                    }
                });
                buttonBackDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Anulowanie");
                        dialog.cancel();
                    }
                });

                dialog.show();
             //   System.out.println("Button delete");
/*
                AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                        .setTitle("Usuwanie")
                        .setView(View.inflate(ctx, R.layout.dialog_layout, null))
                        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("Usuwanie");
                                String url = holder.networkHandler.makeUrl("/lights/removeLight", "serial" + holder.mItem.getSerial(), "user_id="  );
                                holder.sendMessage(url);
                            }
                        })
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("Anulowanie");
                                dialog.cancel();
                            }
                        }).create();

                alertDialog.show();
*/
              //  AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.layout.dialog_layout);


              //  builder.setTitle("usuwanko");
              //  builder.setCancelable(true);

               // builder.setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
              //      @Override
               //     public void onClick(DialogInterface dialog, int which) {
               //         String url = holder.networkHandler.makeUrl("/lights/removeLight", "serial" + holder.mItem.getSerial(), "user_id="  );
               //         holder.sendMessage(url);
                //    }
              //  });

              //  builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
              //      @Override
              //      public void onClick(DialogInterface dialog, int which) {
             //           dialog.cancel();
            //        }
            //    });

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
        public Light mItem;
        public int mGroupp;

       // public int user_id;


        private final ImageView colorPicker;
        private final SeekBar brightnessBar, whiteContentBar;
        private final Switch onOffSwitch;
        private final Button buttonBreathe, buttonFade;
        private final Button buttonDelete;
        private long timeOfLastColorSandedSuccessfully = 0;
        private final long timeIntervalToSendMessage = 250;
        private int r = 0, g = 0, b = 0, w = 0;
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
            buttonDelete = view.findViewById(R.id.buttonDelete);



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

        private void sendDeleteMessage(String url){
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
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

        private void updateDials() {
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
