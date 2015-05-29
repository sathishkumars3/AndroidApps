package com.example.parsechating;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends ActionBarActivity {
	Handler myHandle=new Handler();
	private static final String TAG = MainActivity.class.getName();
    private static String sUserId;
    public static final String USER_ID_KEY = "userId";

    private EditText etMessage;
    private Button btSend;
    private ListView lvChat;
    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
		myHandle.postDelayed(myRun, 100);
		String url="content://"+"com.android.contacts"+"/1";
		//String url="content://"+"com.example.sample.ContentHelper"+"/students";
		Uri uri=Uri.parse(url);
		Cursor c=getContentResolver().query(uri, null, null, null, "sname");
		while (c.moveToNext()) {
			System.out.println(c.getString(0)+c.getString(1));
			
		}
	}
	public Runnable myRun=new Runnable() {
		
		@Override
		public void run() {
			refreshMessage();
			myHandle.postDelayed(this, 100);
			
		}

		
	};
	private void refreshMessage() {
		receiveMessage();	
		
	}
	private void login() {
		 ParseAnonymousUtils.logIn(new LogInCallback() {

			@Override
			public void done(ParseUser arg0, ParseException arg1) {
				 if (arg0 != null) {
	                    Log.d(TAG, "Anonymous login failed: " + arg0.toString());
	                } else {
	                    startWithCurrentUser();
	                }	
				
			}
		 });
		          
		
	}

	private void startWithCurrentUser() {
		sUserId=ParseUser.getCurrentUser().getObjectId();
		setupMessagePosting();
		
	}

	private void setupMessagePosting() {
		  // Find the text field and button
      /*  etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                ParseObject message = new ParseObject("Message");
                message.put(USER_ID_KEY, sUserId);
                message.put("body", data);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(MainActivity.this, "Successfully created message on Parse",
                             Toast.LENGTH_SHORT).show();
                    }
                });
                etMessage.setText("");
            }
        });*/
		 etMessage = (EditText) findViewById(R.id.etMessage);
	        btSend = (Button) findViewById(R.id.btSend);
	        lvChat = (ListView) findViewById(R.id.lvChat);
	        mMessages = new ArrayList<Message>();
	        mAdapter = new ChatListAdapter(MainActivity.this, sUserId, mMessages);
	        lvChat.setAdapter(mAdapter);
	        btSend.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                String body = etMessage.getText().toString();
	                // Use Message model to create new messages now
	                
	                Message message = new Message();
	                message.setUserId(sUserId);
	                message.setBody(body);
	                message.saveInBackground(new SaveCallback() {
	                    @Override
	                    public void done(ParseException e) {
	                        receiveMessage();
	                    }

					
	                });
	                etMessage.setText("");
	            }
	    });
		
		
		
		
		
	}
	private void receiveMessage() {
		
		ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
		// Configure limit and sort order
		query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
		query.orderByAscending("createdAt");
		// Execute query to fetch all messages from Parse asynchronously
		// This is equivalent to a SELECT query with SQL
		query.findInBackground(new FindCallback<Message>() {
			public void done(List<Message> messages, ParseException e) {
				
					mMessages.clear();
					mMessages.addAll(messages);
					if (messages != null) {
					mAdapter.notifyDataSetChanged(); // update adapter
					lvChat.invalidate(); // redraw listview
				} else {
					Log.d("message", "Error: " + e.getMessage());
				}
     }
 });
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
