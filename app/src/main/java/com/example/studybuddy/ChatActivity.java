package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {

    private ActionBar mToolbar ;
    private ImageButton SendMessageButton , addFileButton;
    private EditText userMessageInput ;
    private ScrollView mScrollView;
    private TextView displayTextMessage ;

    private FirebaseAuth mAuth ;
    private DatabaseReference UsersRef, GroupNameRef, GroupMessageKeyRef;

    private String currentGroupName ,currentUserID , currentUserName, currentDate , currentTime;

    private static final int CAMERA_REQUEST_CODE  = 200 ;
    private static final int STORAGE_REQUEST_CODE  = 400 ;

    private static final int IMAGE_PICK_GALLERY_CODE  = 1000 ;
    private static final int IMAGE_PICK_CAMERA_CODE  = 2000 ;

    private String[] cameraPermission  ;
    private String[] storagePermission  ;

    private Uri image_Uri = null ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent i = getIntent();
        currentGroupName = i.getExtras().getString("groupName");

        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Group").child(currentGroupName).child("message");



        Toast.makeText(ChatActivity.this,currentGroupName , Toast.LENGTH_SHORT).show();

        cameraPermission = new String[]{
                Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        InitializeFields();
        
        getUserInfor();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveMessageInfoToDatabase();

                userMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageImportDialog();
            }
        });
    }

    private void showImageImportDialog()
    {
        String[]options = {"Camera" , "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(which == 0)
                {
                    if(!checkCameraPermission()){
                        requestCameraPermission();

                    }
                    else {
                        pickCamera();
                    }
                    //Camera
                }
                else {
                    //Gallery
                    if(!checkStoragePermission()){
                        requestStoragePermission();

                    }
                    else {
                        pickGallery();
                    }
                }

            }
        })
                .show();
    }

    private void   pickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"GroupImageTitle");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"GroupImageDescription");

        image_Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues );

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

    private void sendImageMessage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setMessage("Send Image");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        String fileNamePath = "/ChatImages" +"" + System.currentTimeMillis();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNamePath);

        storageReference.putFile(image_Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTask.isSuccessful()){
                    Uri p_downloadUri = p_uriTask.getResult();

                    if (p_uriTask.isSuccessful()){
                        HashMap<String, Object> messageInfoMap = new HashMap<>();
                        messageInfoMap.put("name", currentUserName);
                        messageInfoMap.put("message", image_Uri);
                        messageInfoMap.put("date", currentDate);
                        messageInfoMap.put("time", currentTime);
                    }



                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(currentGroupName);

        addFileButton = (ImageButton)findViewById(R.id.add_file_btn);
        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessage = (TextView) findViewById(R.id.group_chat_text);

        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }


    private void getUserInfor() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    currentUserName = snapshot.child("name").getValue().toString()  ;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SaveMessageInfoToDatabase() {
        String message = userMessageInput.getText().toString();
        String messagekEY = GroupNameRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first...", Toast.LENGTH_SHORT).show();
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

            GroupMessageKeyRef = GroupNameRef.child(messagekEY);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void DisplayMessage(DataSnapshot snapshot)
    {
        Iterator iterator = snapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String)  ((DataSnapshot)iterator.next()) .getValue();
            String chatMessage = (String)  ((DataSnapshot)iterator.next()) .getValue();
            String chatName = (String)  ((DataSnapshot)iterator.next()) .getValue();
            String chatTime = (String)  ((DataSnapshot)iterator.next()) .getValue();

            displayTextMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "   "  + chatDate + "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
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
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this ,"Camera & Storage permission are required..... ", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 ){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else  {
                        Toast.makeText(this ,"Storage permission  required..... ", Toast.LENGTH_SHORT).show();
                    }
                }
                onVisibleBehindCanceled();
        }
    }
}