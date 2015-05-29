package com.sample.contentProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parsechating.R;
import com.sample.contentProvider.MyProvider.MyDbHelper;

public class ProviderEx extends ActionBarActivity {
	EditText sid,sname,sage;
	Button btnSubmit,btnCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stud_register);
		sid=(EditText) findViewById(R.id.sid);
		sname=(EditText) findViewById(R.id.sname);
		sage=(EditText) findViewById(R.id.sage);
		btnSubmit=(Button) findViewById(R.id.btnSubmit);
		btnCancel=(Button) findViewById(R.id.btnCancel);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContentValues cv=new ContentValues();
				cv.put(MyProvider.id, sid.getText().toString());
				cv.put(MyProvider.name, sname.getText().toString());
				cv.put(MyProvider.age, sage.getText().toString());
				Uri uri=getContentResolver().insert(MyProvider.myuri, cv);
				Toast.makeText(getApplicationContext(), "Record Inserted :"+uri, Toast.LENGTH_SHORT).show();
				
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Cursor c=getContentResolver().query(MyProvider.myuri, null, null, null, MyProvider.name);
				while(c.moveToNext()){
					System.out.println("id :"+c.getString(0)+" name :"+c.getString(1)+"  age :"+c.getString(2));
				}
				
			}
		});
		
	}

}
