package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar ;
    private ImageButton SendMessageButton ;
    private EditText userMessageInput ;
    private ScrollView mScrollView;
    private TextView displayTextView ;

    private String currentGroupName ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(ChatActivity.this,currentGroupName , Toast.LENGTH_SHORT).show();

        InitializeFields();
    }

    private void InitializeFields() {
        mToolbar = (Toolbar)  findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getActionBar().setTitle("Group Name");

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextView = (TextView) findViewById(R.id.group_chat_text);

        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }
}