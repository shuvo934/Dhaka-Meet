package ttit.com.shuvo.dhakameet.homepage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ttit.com.shuvo.dhakameet.R;
import ttit.com.shuvo.dhakameet.qrscanner.QRCodeResult;
import ttit.com.shuvo.dhakameet.qrscanner.ScannerWithCamera;

public class Homepage extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    TextView dateOfToday;
    String today_date = "";
    MaterialButton lunchQrScanner, dinnerQrScanner;

    TextClock digitalClock;
    TextView msg;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    Handler timerHandler = new Handler();
    long startTime = 0;
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            getUpdatedTime();

            timerHandler.postDelayed(this, 10000);
        }
    };

//    String lunch_start_time = "09:00 AM";
//    String lunch_stop_time = "05:30 PM";
//    String dinner_start_time = "03:00 PM";
//    String dinner_stop_time = "11:59 PM";

    String lunch_start_time = "";
    String lunch_stop_time = "";
    String dinner_start_time = "";
    String dinner_stop_time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        fullLayout = findViewById(R.id.homepage_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_dashboard);
        circularProgressIndicator.setVisibility(View.GONE);

        lunchQrScanner = findViewById(R.id.lunch_qr_scanner_dm);
        dinnerQrScanner = findViewById(R.id.dinner_qr_scanner_dm);

        dateOfToday = findViewById(R.id.date_of_today);
        digitalClock = findViewById(R.id.text_clock_give_att);
        msg = findViewById(R.id.qr_button_missing_msg);
        msg.setVisibility(View.GONE);

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.poppins_bold);
        digitalClock.setTypeface(typeface);

        lunchQrScanner.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, ScannerWithCamera.class);
            intent.putExtra("QR_TYPE", "3");
            startActivity(intent);
        });

        dinnerQrScanner.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, ScannerWithCamera.class);
            intent.putExtra("QR_TYPE", "5");
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTimeData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void getUpdatedTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

        today_date = sdf.format(calendar.getTime());
        today_date = today_date.toUpperCase(Locale.ENGLISH);
        dateOfToday.setText(today_date);

        System.out.println("NOW TIME: " + calendar.getTime().toString());

        String toDate = simpleDateFormat1.format(calendar.getTime());

        Date nowTime = calendar.getTime();

        String lst = toDate + " " + lunch_start_time;
        String lstopt = toDate + " " + lunch_stop_time;
        String dstopt = toDate + " " + dinner_stop_time;
        String dst = toDate + " " + dinner_start_time;

        Date lunchStart = null;
        Date lunchStop = null;
        Date dinnerStop = null;
        Date dinnerStart = null;

        try {
            lunchStart = simpleDateFormat.parse(lst);
            lunchStop = simpleDateFormat.parse(lstopt);
            dinnerStop = simpleDateFormat.parse(dstopt);
            dinnerStart = simpleDateFormat.parse(dst);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (lunchStart != null && lunchStop != null && dinnerStop != null && dinnerStart != null) {
            if(nowTime.getTime() >= lunchStart.getTime() && nowTime.getTime() <= lunchStop.getTime() && nowTime.getTime() >= dinnerStart.getTime() && nowTime.getTime() <= dinnerStop.getTime()) {
                lunchQrScanner.setVisibility(View.VISIBLE);
                dinnerQrScanner.setVisibility(View.VISIBLE);
                msg.setVisibility(View.GONE);
            }
            else if (nowTime.getTime() >= lunchStart.getTime() && nowTime.getTime() <= lunchStop.getTime()) {
                lunchQrScanner.setVisibility(View.VISIBLE);
                dinnerQrScanner.setVisibility(View.GONE);
                msg.setVisibility(View.GONE);
            }
            else if (nowTime.getTime() >= dinnerStart.getTime() && nowTime.getTime() <= dinnerStop.getTime()) {
                lunchQrScanner.setVisibility(View.GONE);
                dinnerQrScanner.setVisibility(View.VISIBLE);
                msg.setVisibility(View.GONE);
            }
            else {
                if (nowTime.getTime() < lunchStart.getTime()) {
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("Please wait till 09:00 AM to verify for lunch");
                }
                else if (nowTime.getTime() < dinnerStart.getTime()) {
                    msg.setVisibility(View.VISIBLE);
                    msg.setText("Please wait till 07:00 PM to verify for dinner");
                }
                lunchQrScanner.setVisibility(View.GONE);
                dinnerQrScanner.setVisibility(View.GONE);
            }
        }
        else {
            msg.setVisibility(View.VISIBLE);
            msg.setText("Could not process time to verify for Lunch and Dinner");
            lunchQrScanner.setVisibility(View.GONE);
            dinnerQrScanner.setVisibility(View.GONE);
        }

    }

    public void getTimeData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String url = "http://103.56.208.123:8001/apex/dhaka_meet/"+"visitorValidation/getTime";

        RequestQueue requestQueue = Volley.newRequestQueue(Homepage.this);

        StringRequest dataReq = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String qct_id = info.getString("qct_id")
                                .equals("null") ? "" : info.getString("qct_id");

                        if (qct_id.equals("3")) {
                            lunch_start_time = info.getString("qct_start_time")
                                    .equals("null") ? "" : info.getString("qct_start_time");
                            lunch_stop_time = info.getString("qct_end_time")
                                    .equals("null") ? "" : info.getString("qct_end_time");
                        }
                        else {
                            dinner_start_time = info.getString("qct_start_time")
                                    .equals("null") ? "" : info.getString("qct_start_time");
                            dinner_stop_time = info.getString("qct_end_time")
                                    .equals("null") ? "" : info.getString("qct_end_time");
                        }
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(dataReq);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);

                startTime = System.currentTimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                loading = false;

            }
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Homepage.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getTimeData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}