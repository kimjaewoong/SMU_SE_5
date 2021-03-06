package com.example.campingapp.upload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.campingapp.R;
import com.example.campingapp.databinding.ActivityUploadInformationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class UploadInformation extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth auth;
    String name;
    String ower;
    String addr;
    String phone;
    int price;
    int addPrice;
    int minPeople;
    int maxPeople;
    boolean bbq;
    boolean wifi;
    boolean parking;
    boolean water;
    boolean pickup;
    ImageView imageView;
    ActivityUploadInformationBinding binding;
    Uri photoUri;
    int PICK_IMAGE_FROM_ALBUM = 0;
    final ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_upload_information);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_information);
        UploadViewModel viewModel = ViewModelProviders.of(this).get(UploadViewModel.class);
        //detail = (Button) findViewById(R.id.set_detail_button);
        Button detail = (Button) binding.setDetailButton;
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"셔틀 or 픽업 서비스",
                        "바베큐", "온수", "주차장", "Wifi"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(UploadInformation.this);
                dialog.setTitle("세부정보 선택")
                        .setMultiChoiceItems(items,
                                new boolean[]{false, false, false, false, false},
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (isChecked) {
                                            list.add(items[which]);
                                        } else {
                                            list.remove(items[which]);
                                        }
                                    }
                                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                for (String item : list) {
                                    switch (item){
                                        case "셔틀 or 픽업 서비스":
                                            pickup=true;
                                            break;
                                        case "바베큐":
                                            bbq=true;
                                            break;
                                        case "온수":
                                            water=true;
                                            break;
                                        case "주차장":
                                            parking=true;
                                            break;
                                        case "Wifi":
                                            wifi=true;
                                            break;
                                    }
                                }

                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bbq=false;
                                pickup=false;
                                parking=false;
                                wifi=false;
                                water=false;
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
        Button imageButton = (Button) binding.imageSelectButton;
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent,PICK_IMAGE_FROM_ALBUM);
            }
        });




        Button upload = (Button) binding.buttonUpload;
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth=FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                name = binding.uploadCampName.getText().toString();
                ower = user.getUid();
                addr = binding.uploadCampAddr.getText().toString();
                phone = binding.uploadCampPhone.getText().toString();
                try{
                    price = Integer.parseInt(binding.uploadCampPrice.getText().toString());
                }catch (Exception e){
                    price = 0;
                }
                try{
                    addPrice = Integer.parseInt(binding.uploadAddPrice.getText().toString());
                }catch (Exception e){
                    addPrice = 0;
                }
                try{
                    minPeople = Integer.parseInt(binding.peopleMin.getText().toString());
                }catch (Exception e){
                    minPeople=0;
                }

                try{
                    maxPeople = Integer.parseInt(binding.peopleMax.getText().toString());
                }catch (Exception e){
                    maxPeople=0;
                }




                if (name == null || addr == null || phone == null || price == 0 || addPrice==0 || minPeople ==0 || maxPeople ==0 ){
                    Toast.makeText(UploadInformation.this,"값을 모두 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else if(photoUri == null){
                    Toast.makeText(UploadInformation.this,"사진을 선택해 주세요",Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.uploadDB(name,ower,addr, phone, price, addPrice, minPeople, maxPeople, bbq, parking, pickup, water, wifi, photoUri);
                    onBackPressed();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            photoUri = data.getData();
            binding.imageSelect.setImageURI(photoUri);
        } else {
            finish();
        }

    }

}