package ttit.com.shuvo.dhakameet.qrscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ttit.com.shuvo.dhakameet.R;
import ttit.com.shuvo.dhakameet.qrscanner.Model.QRGEOModel;
import ttit.com.shuvo.dhakameet.qrscanner.Model.QRMECARDModel;
import ttit.com.shuvo.dhakameet.qrscanner.Model.QRURLModel;
import ttit.com.shuvo.dhakameet.qrscanner.Model.QRVCARDModel;

public class QRCodeResult extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    TextView textView;

    ImageView okIcon, warningIcon;

    TextView itemTypeName;

    CircleImageView profileImage;
    TextView visitorName;

    MaterialButton done;
    String qr_type = "";

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";
    String vi_id = "";
    String vi_name = "";
    private boolean imageFound = false;
    String qr_text = "";

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_result);

        fullLayout = findViewById(R.id.qr_result_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_qr_result);
        circularProgressIndicator.setVisibility(View.GONE);

        okIcon = findViewById(R.id.verified_icon);
        warningIcon = findViewById(R.id.warning_icon);
        itemTypeName = findViewById(R.id.item_verified_name);

        profileImage = findViewById(R.id.doc_profile_image);
        visitorName = findViewById(R.id.visitor_profile_name);

        textView = findViewById(R.id.result_text_from_scanner);
        textView.setVisibility(View.GONE);

        done = findViewById(R.id.done_button_for_result);
        done.setVisibility(View.GONE);

        Intent intent = getIntent();
        qr_text = intent.getStringExtra("RESULT");
        qr_type = intent.getStringExtra("QR_TYPE");

        done.setOnClickListener(v -> finish());

//        processRawResult(text);
        if (qr_text != null) {
            getViData(qr_text);
        }
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Failed to Scan Qr Code. Please Try Again");
        }
    }

    @Override
    public void onBackPressed() {
        if (loading) {
            Toast.makeText(this, "Please Wait While Loading", Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    //    private void processRawResult(String text) {
//        if (text.startsWith("BEGIN:")) {
//
//            String[] tokens = text.split("\n");
//            QRVCARDModel qrvcardModel = new QRVCARDModel();
//
//            for (int i = 0 ; i < tokens.length; i++) {
//
//                if (tokens[i].startsWith("BEGIN:")) {
//
//                    qrvcardModel.setType(tokens[i].substring("BEGIN:".length()));
//                }
//                else if (tokens[i].startsWith("N:")) {
//
//                    qrvcardModel.setName(tokens[i].substring("N:".length()));
//                }
//                else if (tokens[i].startsWith("ORG:")) {
//
//                    qrvcardModel.setOrg(tokens[i].substring("ORG:".length()));
//                }
//                else if (tokens[i].startsWith("TITLE:")) {
//
//                    qrvcardModel.setTitle(tokens[i].substring("TITLE:".length()));
//                }
//                else if (tokens[i].startsWith("TEL:")) {
//
//                    qrvcardModel.setTel(tokens[i].substring("TEL:".length()));
//                }
//                else if (tokens[i].startsWith("URL:")) {
//
//                    qrvcardModel.setUrl(tokens[i].substring("URL:".length()));
//                }
//                else if (tokens[i].startsWith("EMAIL:")) {
//
//                    qrvcardModel.setEmail(tokens[i].substring("EMAIL:".length()));
//                }
//                else if (tokens[i].startsWith("ADR:")) {
//
//                    qrvcardModel.setAddress(tokens[i].substring("ADR:".length()));
//                }
//                else if (tokens[i].startsWith("NOTE:")) {
//
//                    qrvcardModel.setNote(tokens[i].substring("NOTE:".length()));
//                }
//                else if (tokens[i].startsWith("SUMMARY:")) {
//
//                    qrvcardModel.setSummary(tokens[i].substring("SUMMARY:".length()));
//                }
//                else if (tokens[i].startsWith("DTSTART:")) {
//
//                    qrvcardModel.setDtstart(tokens[i].substring("DTSTART:".length()));
//                }
//                else if (tokens[i].startsWith("DTEND:")) {
//
//                    qrvcardModel.setDtend(tokens[i].substring("DTEND:".length()));
//                }
//            }
//            String details =
//                    " Name: "+ qrvcardModel.getName()+
//                            "\n Organization: "+ qrvcardModel.getOrg()+
//                            "\n Title: "+ qrvcardModel.getTitle()+
//                            "\n Phone: "+ qrvcardModel.getTel()+
//                            "\n URL: "+ qrvcardModel.getUrl()+
//                            "\n Email: "+ qrvcardModel.getEmail()+
//                            "\n Address: "+ qrvcardModel.getAddress()+
//                            "\n Note: "+ qrvcardModel.getNote();
//            if (qrvcardModel.getType().equals("VCARD")) {
//                textView.setText(qrvcardModel.getType()+"\n"+details);
//            } else {
//                textView.setText(qrvcardModel.getType()+"\n"+text);
//            }
//
//        }
//        else if (text.startsWith("http://") || text.startsWith("https://") || text.startsWith("www.")) {
//
//            QRURLModel qrurlModel = new QRURLModel(text);
//            textView.setText("URL\n"+qrurlModel.getUrl());
//        }
//        else if (text.startsWith("geo:")) {
//            QRGEOModel qrgeoModel = new QRGEOModel();
//            String  delims = "[ , ?q= ]+";
//            String[] tokens = text.split(delims);
//
//            qrgeoModel.setLat(tokens[0].substring("geo:".length()));
//            qrgeoModel.setLng(tokens[1]);
//            qrgeoModel.setGeo_place(tokens[2]);
//            textView.setText("GEO LOCATION\n"+"Latitude: "+qrgeoModel.getLat()+"\nLongitude: "+qrgeoModel.getLng()+"\nPlace: "+qrgeoModel.getGeo_place());
//        }
//        else if(text.startsWith("MECARD:")) {
//
//            QRMECARDModel qrmecardModel = new QRMECARDModel();
//            qrmecardModel.setType("MECARD");
//            text = text.substring("MECARD:".length());
//            String[] tokens = text.split(";");
//
//            for (int i = 0 ; i < tokens.length; i++) {
//
//                if (tokens[i].startsWith("N:")) {
//
//                    qrmecardModel.setName(tokens[i].substring("N:".length()));
//                }
//                else if (tokens[i].startsWith("ORG:")) {
//
//                    qrmecardModel.setOrg(tokens[i].substring("ORG:".length()));
//                }
//                else if (tokens[i].startsWith("TEL:")) {
//
//                    qrmecardModel.setTel(tokens[i].substring("TEL:".length()));
//                }
//                else if (tokens[i].startsWith("URL:")) {
//
//                    qrmecardModel.setUrl(tokens[i].substring("URL:".length()));
//                }
//                else if (tokens[i].startsWith("EMAIL:")) {
//
//                    qrmecardModel.setEmail(tokens[i].substring("EMAIL:".length()));
//                }
//                else if (tokens[i].startsWith("ADR:")) {
//
//                    qrmecardModel.setAddress(tokens[i].substring("ADR:".length()));
//                }
//                else if (tokens[i].startsWith("NOTE:")) {
//
//                    qrmecardModel.setNote(tokens[i].substring("NOTE:".length()));
//                }
//                else if (tokens[i].startsWith("SUMMARY:")) {
//
//                    qrmecardModel.setSummary(tokens[i].substring("SUMMARY:".length()));
//                }
//                else if (tokens[i].startsWith("DTSTART:")) {
//
//                    qrmecardModel.setDtstart(tokens[i].substring("DTSTART:".length()));
//                }
//                else if (tokens[i].startsWith("DTEND:")) {
//
//                    qrmecardModel.setDtend(tokens[i].substring("DTEND:".length()));
//                }
//            }
//
//            String details =
//                    " Name: "+ qrmecardModel.getName()+
//                            "\n Organization: "+ qrmecardModel.getOrg()+
//                            "\n Note: "+ qrmecardModel.getNote()+
//                            "\n Phone: "+ qrmecardModel.getTel()+
//                            "\n URL: "+ qrmecardModel.getUrl()+
//                            "\n Email: "+ qrmecardModel.getEmail()+
//                            "\n Address: "+ qrmecardModel.getAddress();
//
//            textView.setText(qrmecardModel.getType()+"\n"+details);
//
//        }
//        else if (text.startsWith("mailto:")) {
//            text = text.substring("mailto:".length());
//            textView.setText("EMAIL\n"+text);
//        }
//        else if (text.startsWith("tel:")) {
//            text = text.substring("tel:".length());
//            textView.setText("PHONE NO\n"+text);
//        }
//        else if (text.startsWith("smsto:")) {
//            text = text.substring("smsto:".length());
//
//            String phone = text.substring(0,text.indexOf(":"));
//            System.out.println(phone);
//
//            String sms = text.substring(text.indexOf(":")+1);
//            System.out.println(sms);
//
//            textView.setText("SMS\n"+"Phone: " + phone + "\n Message: " + sms);
//        }
//        else if (text.startsWith("WIFI:")) {
//
//            text = text.substring("WIFI:".length());
//            String[] tokens = text.split(";");
//
//            String name = "";
//            String type = "";
//            String pass = "";
//
//
//            for (int i = 0; i < tokens.length; i++) {
//                if (tokens[i].startsWith("S:")) {
//                    name = tokens[i].substring("S:".length());
//                }
//                else if (tokens[i].startsWith("T:")) {
//                    type = tokens[i].substring("T:".length());
//                }
//                else if (tokens[i].startsWith("P:")) {
//                    pass = tokens[i].substring("P:".length());
//                }
//            }
//
//            String details =
//                    " Connection Name: "+ name+
//                            "\n Password: "+ pass+
//                            "\n Type: "+ type;
//
//
//            textView.setText("WIFI\n"+details);
//        }
//        else {
//            textView.setText("TEXT\n"+text);
//        }
//    }

    public void getViData(String vi_code) {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        done.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        String url = "http://103.56.208.123:8001/apex/dhaka_meet/"+"visitorValidation/getVisitorInfo?p_vi_code="+vi_code;

        RequestQueue requestQueue = Volley.newRequestQueue(QRCodeResult.this);

        StringRequest docDataReq = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        vi_id = info.getString("vi_id")
                                .equals("null") ? "" : info.getString("vi_id");

                        vi_name = info.getString("vi_name")
                                .equals("null") ? "" : info.getString("vi_name");

                        String profile_pic = info.optString("vi_image");

                        if (profile_pic.equals("null") || profile_pic.equals("") ) {
                            System.out.println("NULL IMAGE");
                            imageFound = false;
                        }
                        else {
                            byte[] decodedString = Base64.decode(profile_pic,Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                            if (bitmap != null) {
                                System.out.println("OK");
                                imageFound = true;
                            }
                            else {
                                System.out.println("NOT OK");
                                imageFound = false;
                            }
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

        requestQueue.add(docDataReq);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;

                visitorName.setText(vi_name);

                if (imageFound) {
                    Glide.with(getApplicationContext())
                            .load(bitmap)
                            .fitCenter()
                            .into(profileImage);
                }
                else {
                    profileImage.setImageResource(R.drawable.doctor);
                }
                loading = false;

                checkValidation();

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
        done.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(QRCodeResult.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getViData(qr_text);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    textView.setVisibility(View.VISIBLE);
                    System.out.println("1");
                    textView.setText("Failed to Load Data. Please Try Again");
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void checkValidation() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        done.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        String toDate = simpleDateFormat1.format(calendar.getTime());

        String url = "http://103.56.208.123:8001/apex/dhaka_meet/"+"visitorValidation/qrValidation";

        RequestQueue requestQueue = Volley.newRequestQueue(QRCodeResult.this);

        StringRequest cancelAppReq = new StringRequest(Request.Method.POST, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                parsing_message = jsonObject.getString("msg").equals("null") ? "" : jsonObject.getString("msg");

                connected = true;
                updateAfterValidation();

            }
            catch (JSONException e) {
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateAfterValidation();
            }
        }, error -> {
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateAfterValidation();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_VI_ID",vi_id);
                headers.put("P_DATE",toDate);
                headers.put("QR_TYPE_ID",qr_type);
                return headers;
            }
        };

        cancelAppReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(cancelAppReq);
    }

    private void updateAfterValidation() {
        if(conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                conn = false;
                connected = false;

                System.out.println("PARSE: " + parsing_message);
                switch (parsing_message) {
                    case "no msg":
                        fullLayout.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Failed to Verify Data. Please Try Again");
                        break;
                    case "Verified":
                        fullLayout.setVisibility(View.VISIBLE);
                        warningIcon.setVisibility(View.GONE);
                        okIcon.setVisibility(View.VISIBLE);
                        if (qr_type.equals("3")) {
                            String tt = "Lunch Verified";
                            itemTypeName.setText(tt);
                        } else if (qr_type.equals("5")) {
                            String tt = "Dinner Verified";
                            itemTypeName.setText(tt);
                        }
                        break;
                    case "Already Verified":
                        fullLayout.setVisibility(View.VISIBLE);
                        warningIcon.setVisibility(View.VISIBLE);
                        okIcon.setVisibility(View.GONE);
                        if (qr_type.equals("3")) {
                            String tt = "Lunch Already Verified";
                            itemTypeName.setText(tt);
                        } else if (qr_type.equals("5")) {
                            String tt = "Dinner Already Verified";
                            itemTypeName.setText(tt);
                        }
                        break;
                    default:
                        fullLayout.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        System.out.println("2");
                        textView.setText("No Data Found. Please Try Again");
                        break;
                }

                loading = false;
            }
            else {
                alertMessageValidation();
            }
        }
        else {
            alertMessageValidation();
        }
    }

    public void alertMessageValidation() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        done.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(QRCodeResult.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    checkValidation();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Failed to Validate Data. Please Try Again");
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}