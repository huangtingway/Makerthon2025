package com.example.makerthon;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import org.jetbrains.annotations.NotNull;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private Button scanQRcodeBtn, uploadPictureBtn, reportHistoryBtn, btnAddReport;
    private EditText locationEditText, discriptionEditText;
    private ZXingScannerView zXingScannerView;
    private Spinner typeSpinner, locationSpinner;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ImageView image1, image2, image3;
    private String location;
    private ArrayList<ReportItem> reportItems = new ArrayList<>();

    private String historyListFrom[] = {"list_item_type", "list_item_info", "list_item_location"};
    private int historyListTo[] = {R.id.list_item_type, R.id.list_item_info, R.id.list_item_location};
    private ArrayList<HashMap<String, String>>  historyListData;//清單資料

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanQRcodeBtn = findViewById(R.id.btn_scanQRCode);
        uploadPictureBtn = findViewById(R.id.btn_uploadImage);
        locationEditText = findViewById(R.id.et_location);
        locationSpinner = findViewById(R.id.spinner_location_city);
        zXingScannerView = findViewById(R.id.ZXingScannerView_QRCode);
        image1 = findViewById(R.id.iv_image1);
        image2 = findViewById(R.id.iv_image2);
        image3 = findViewById(R.id.iv_image3);
        btnAddReport = findViewById(R.id.btn_addReport);
        reportHistoryBtn = findViewById(R.id.btn_reportHistory);
        typeSpinner = findViewById(R.id.spinner_reportType);
        discriptionEditText = findViewById(R.id.et_description);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), uris -> {
            if (!uris.isEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: " + uris.size());

                if (uris.size() > 0) image1.setImageURI(uris.get(0));
                if (uris.size() > 1) image2.setImageURI(uris.get(1));
                if (uris.size() > 2) image3.setImageURI(uris.get(2));
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        checkPremission();
        setBtnListener();
    }


    private void checkPremission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}, 0);

            }

            if (Build.VERSION.SDK_INT >= 34) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, 0);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) finish();
        }
    }

    private void setBtnListener() {
        scanQRcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", "click");
                openQRCamera();
            }
        });

        uploadPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationEditText.getText().toString();
                if (location.isEmpty()) {
                    Toast.makeText(MainActivity.this, "請填入地址或掃描QRcode", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (discriptionEditText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "請填入描述", Toast.LENGTH_SHORT).show();
                    return;
                }

                ReportItem reportItem = new ReportItem(typeSpinner.getSelectedItem().toString(),
                        location, discriptionEditText.getText().toString(),
                        image1,
                        image2,
                        image3);
                reportItems.add(reportItem);

                Toast.makeText(MainActivity.this, "通報成功", Toast.LENGTH_SHORT).show();

                locationSpinner.setSelection(0);
                typeSpinner.setSelection(0);
                locationEditText.setText("");
                discriptionEditText.setText("");
                image1.setImageResource(R.drawable.baseline_image_not_supported_24);
                image2.setImageResource(R.drawable.baseline_image_not_supported_24);
                image3.setImageResource(R.drawable.baseline_image_not_supported_24);
                location = null;
                zXingScannerView.setVisibility(View.INVISIBLE);
            }
        });

        reportHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ReportItem item : reportItems) {
                    Log.d("ReportItem", "type:" + item.getType() + "\nLocation: " + item.getLocation() +
                            "\nDescription: " + item.getDescription() +
                            "\nImage1: " + item.getImageUri1() +
                            "\nImage2: " + item.getImageUri2() +
                            "\nImage3: " + item.getImageUri3());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = getLayoutInflater();
                        View historyView = inflater.inflate(R.layout.history_dialog, null, false);//初始化對話框資料
                        ListView historyListView = historyView.findViewById(R.id.historyListView);
                        historyListData = new ArrayList<>();//清單資料
                        for (ReportItem item : reportItems) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("list_item_type", item.getType());
                            map.put("list_item_info", "說明：" + item.getDescription());
                            map.put("list_item_location", item.getLocation());
                            historyListData.add(map);
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, historyListData, R.layout.history_listitem, historyListFrom, historyListTo);
                        historyListView.setAdapter(simpleAdapter);//設置清單資料


                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this, com.google.android.material.R.style.AlertDialog_AppCompat); //設置對話框
                        dialogBuilder.setTitle("通報記錄");
                        dialogBuilder.setView(historyView);
                        dialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() { //data confirm -> checkin success
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        Dialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                });

            }
        });
    }

    private void openQRCamera() {
        zXingScannerView.setVisibility(View.VISIBLE);
        zXingScannerView.startCamera();
        zXingScannerView.setResultHandler(this);
    }

    @Override
    protected void onStop() {
        zXingScannerView.stopCamera();
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        if (result == null || result.getText() == null || result.getText().isEmpty() || result.getText().length() < 5 || !result.getText().contains("市")){
            Toast.makeText(this, "掃描錯誤", Toast.LENGTH_SHORT).show();
            openQRCamera();
            return;
        }

        location = result.getText();
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        String city = result.getText().substring(3, result.getText().indexOf("市") + 1);

        int index = -1;
        String[] cities = getResources().getStringArray(R.array.city);
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].equals(city)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            locationSpinner.setSelection(index);
        }
        locationEditText.setText(result.getText().substring(result.getText().indexOf("市") + 1, result.getText().length()));
        zXingScannerView.setVisibility(View.INVISIBLE);
    }
}