package com.example.studybuddy;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActionBar mToolbar ;
    private ImageButton SendMessageButton , addFileButton;
    private EditText userMessageInput ;

    private FirebaseAuth mAuth ;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;

    private String currentGroupName ,currentUserID , currentUserName, currentUserImage, currentDate , currentTime;

    private static final int CAMERA_REQUEST_CODE  = 200 ;
    private static final int STORAGE_REQUEST_CODE  = 400 ;

    private static final int IMAGE_PICK_GALLERY_CODE  = 1000 ;
    private static final int IMAGE_PICK_CAMERA_CODE  = 2000 ;

    private String[] cameraPermission  ;
    private String[] storagePermission  ;

    private Uri image_Uri = null;

    RecyclerView recyclerView;
    List<ModelMessage> messageList;
    AdapterMessage adapterMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent i = getIntent();
        currentGroupName = i.getExtras().getString("groupName");

        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Group").
                child(currentGroupName).child("message");

        cameraPermission = new String[]{
                Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        InitializeFields();
        
        getUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveMessageInfoToDatabase();
                userMessageInput.setText("");
            }
        });

        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if access is not given then we will request for permission
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_Uri) ;
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission , STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission , CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_Uri = data.getData();
                sendImageMessage();
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                sendImageMessage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0 )
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this ,"Camera & Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 ){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }
                    else  {
                        Toast.makeText(this ,"Storage permission required", Toast.LENGTH_SHORT).show();
                    }
                }
                onVisibleBehindCanceled();
        }
    }

    private void sendImageMessage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setMessage("Sending Image");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        String fileNamePath = "ChatImages/" + "message" + System.currentTimeMillis();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(fileNamePath);

        storageReference.putFile(image_Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Uri downloadUri = task.getResult();

                            String messageKey = GroupNameRef.push().getKey();
                            GroupMessageKeyRef = GroupNameRef.child(messageKey);

                            Calendar calForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                            currentDate = currentDateFormat.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                            currentTime = currentTimeFormat.format(calForTime.getTime());

                            HashMap<String, Object> messageInfoMap = new HashMap<>();
                            messageInfoMap.put("name", currentUserName);
                            messageInfoMap.put("message", downloadUri.toString());
                            messageInfoMap.put("date", currentDate);
                            messageInfoMap.put("time", currentTime);
                            messageInfoMap.put("uid", currentUserID);
                            messageInfoMap.put("uimage", currentUserImage);
                            messageInfoMap.put("messageID", messageKey);
                            messageInfoMap.put("type", "image");

                            GroupMessageKeyRef.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Scroll to the bottom of the RecyclerView
                                        recyclerView.smoothScrollToPosition(adapterMessage.getItemCount() - 1);
                                    } else {
                                        Toast.makeText(ChatActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            // Handle the error
                            Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if(snapshot.exists())
                {
                    DisplayMessage(snapshot);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if(snapshot.exists())
                {
                    DisplayMessage(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {
        mToolbar = getSupportActionBar();
        mToolbar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setDisplayShowHomeEnabled(true);
        mToolbar.setTitle(currentGroupName);

        addFileButton = findViewById(R.id.add_file_btn);
        SendMessageButton =findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);

        recyclerView = findViewById(R.id.message_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);
        messageList = new ArrayList<>();
    }

    private void getUserInfo() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    currentUserName = snapshot.child("name").getValue().toString();
                    currentUserImage = snapshot.child("image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SaveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString().trim();
        String messageKey = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Cannot send empty message!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            messageInfoMap.put("uid", currentUserID);
            messageInfoMap.put("uimage", currentUserImage);
            messageInfoMap.put("messageID", messageKey);
            messageInfoMap.put("type", "text");

            GroupMessageKeyRef.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Scroll to the bottom of the RecyclerView
                        recyclerView.smoothScrollToPosition(adapterMessage.getItemCount() - 1);
                    } else {
                        Toast.makeText(ChatActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void DisplayMessage(DataSnapshot snapshot)
    {
        GroupNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ModelMessage modelMessage = dataSnapshot1.getValue(ModelMessage.class);
                    messageList.add(modelMessage);
                    adapterMessage = new AdapterMessage(getApplicationContext(), messageList);
                    recyclerView.setAdapter(adapterMessage);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    recyclerView.scrollToPosition(adapterMessage.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("FragmentToLoad", "ChatFragment");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}