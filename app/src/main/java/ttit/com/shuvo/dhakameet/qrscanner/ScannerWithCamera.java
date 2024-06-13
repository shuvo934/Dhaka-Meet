package ttit.com.shuvo.dhakameet.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import ttit.com.shuvo.dhakameet.R;

public class ScannerWithCamera extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    boolean flash = false;
    boolean focus = true;

    ImageView flashSwitch;
    ImageView focusSwitch;
    ImageView cameraSwitch;

    int cameraId = 0;
    String qr_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_with_camera);

        scannerView = findViewById(R.id.zxscan_dm7);
        flashSwitch = findViewById(R.id.flash_switch);
        flashSwitch.setImageResource(R.drawable.flash_off_24);

        focusSwitch = findViewById(R.id.focus_switch);
        focusSwitch.setImageResource(R.drawable.center_focus_strong_24);

        cameraSwitch = findViewById(R.id.camera_switch);

        scannerView.setFlash(flash);
        scannerView.setAutoFocus(focus);

        Intent intent = getIntent();
        qr_type = intent.getStringExtra("QR_TYPE");

        String deviceName = getDeviceName();
        System.out.println(deviceName);
        if (deviceName.contains("HUAWEI")) {
            scannerView.setAspectTolerance(0.5f);
        }

        flashSwitch.setOnClickListener(view -> {
            if (!flash) {
                flashSwitch.setImageResource(R.drawable.flash_on_24);
                flash = true;
                scannerView.setFlash(flash);
            }
            else {
                flashSwitch.setImageResource(R.drawable.flash_off_24);
                flash = false;
                scannerView.setFlash(flash);
            }
        });

        focusSwitch.setOnClickListener(view -> {
            if (!focus) {
                focusSwitch.setImageResource(R.drawable.center_focus_strong_24);
                focus = true;
                scannerView.setAutoFocus(focus);
            }
            else {
                focusSwitch.setImageResource(R.drawable.center_focus_weak_24);
                focus = false;
                scannerView.setAutoFocus(focus);
            }
        });

        cameraSwitch.setOnClickListener(view -> {
            if (cameraId == 0) {
                flashSwitch.setImageResource(R.drawable.flash_off_24);
                flash = false;
                scannerView.setFlash(flash);

                focusSwitch.setImageResource(R.drawable.center_focus_strong_24);
                focus = true;
                scannerView.setAutoFocus(focus);

                scannerView.stopCamera();

                cameraId = 1;
                scannerView.setResultHandler(ScannerWithCamera.this);
                scannerView.startCamera(cameraId);
            }
            else {
                flashSwitch.setImageResource(R.drawable.flash_off_24);
                flash = false;
                scannerView.setFlash(flash);

                focusSwitch.setImageResource(R.drawable.center_focus_strong_24);
                focus = true;
                scannerView.setAutoFocus(focus);

                scannerView.stopCamera();

                cameraId = 0;
                scannerView.setResultHandler(ScannerWithCamera.this);
                scannerView.startCamera(cameraId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(ScannerWithCamera.this);
        System.out.println(cameraId);
        scannerView.startCamera(cameraId);
    }

    @Override
    public void onBackPressed() {
        flashSwitch.setImageResource(R.drawable.flash_off_24);
        flash = false;
        scannerView.setFlash(flash);

        focusSwitch.setImageResource(R.drawable.center_focus_strong_24);
        focus = true;
        scannerView.setAutoFocus(focus);

        scannerView.stopCamera();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        flashSwitch.setImageResource(R.drawable.flash_off_24);
        flash = false;
        scannerView.setFlash(flash);

        focusSwitch.setImageResource(R.drawable.center_focus_strong_24);
        focus = true;
        scannerView.setAutoFocus(focus);

        scannerView.stopCamera();
        super.onPause();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent(ScannerWithCamera.this, QRCodeResult.class);
        intent.putExtra("RESULT",rawResult.getText());
        intent.putExtra("QR_TYPE",qr_type);
        startActivity(intent);
    }
}