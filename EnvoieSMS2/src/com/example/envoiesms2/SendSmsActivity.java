package com.example.envoiesms2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SendSmsActivity extends Activity implements OnItemClickListener {

    Button sendSMSBtn;
    EditText toPhoneNumberET;
    EditText smsMessageET;
    
    private static SendSmsActivity inst1;
    ArrayList<String> smsMessagesList1 = new ArrayList<String>();
    ListView smsListView1;
    ArrayAdapter arrayAdapter1;

    public static SendSmsActivity instance() {
        return inst1;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst1 = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        
        smsListView1 = (ListView) findViewById(R.id.SMSList2);
        arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList1);
        smsListView1.setAdapter(arrayAdapter1);
        smsListView1.setOnItemClickListener(this);

        
        
        sendSMSBtn = (Button) findViewById(R.id.btnSendSMS);
        toPhoneNumberET = (EditText) findViewById(R.id.editTextPhoneNo);
        smsMessageET = (EditText) findViewById(R.id.editTextSMS);
        sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMS();
            }
        });
        
        refreshSmsInbox1();
    }

    public void refreshSmsInbox1() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody1 = smsInboxCursor.getColumnIndex("body");
        int indexAddress1 = smsInboxCursor.getColumnIndex("address");
        //long timeMillis = smsInboxCursor.getColumnIndex("date");
       // Date date = new Date(timeMillis);
       // SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
       // String dateText = format.format(date);

        if (indexBody1 < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter1.clear();
        do {
            String str = smsInboxCursor.getString(indexAddress1) +" at "+
                    "\n" + smsInboxCursor.getString(indexBody1) +/*dateText+*/ "\n";
            arrayAdapter1.add(str);
        } while (smsInboxCursor.moveToNext());
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList1.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void sendSMS() {
        String toPhoneNumber = toPhoneNumberET.getText().toString();
        String smsMessage = smsMessageET.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent."+smsMessageET,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void goToInbox(View view) {
        Intent intent = new Intent(SendSmsActivity.this, ReceiveSmsActivity.class);
        startActivity(intent);
    }
}