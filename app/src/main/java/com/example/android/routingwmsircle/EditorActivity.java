package com.example.android.routingwmsircle;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.android.routingwmsircle.data.UserInfo;

/**
 * Created by Rahul Setty on 2/20/2018.
 */

public class EditorActivity extends AppCompatActivity{

    /** EditText field to enter the user's name */
    private EditText mNameEditText;

    /** EditText field to enter the user's address */
    private EditText mAddressEditText;

    /** Spinner field to enter the user's reply */
    private Spinner mReplySpinner;

    // reply
    private int mReply = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_editor);

        //Find all the relevant views
        mNameEditText = findViewById(R.id.edit_user_name);
        mAddressEditText = findViewById(R.id.edit_user_address);
        mReplySpinner = findViewById(R.id.spinner_reply);

        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the reply.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        final ArrayAdapter replySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_reply_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        replySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mReplySpinner.setAdapter(replySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mReplySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.reply_yes))) {
                        mReply = UserInfo.UserEntry.YES; // Yes
                    } else if (selection.equals(getString(R.string.reply_no))) {
                        mReply = UserInfo.UserEntry.NO; // No
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mReply = 0; // No
            }
        });
    }

    private void insertUserInfo(){

        // Get the items from the Edit text of the editor activity
        Editable name = mNameEditText.getText();
        Editable address = mAddressEditText.getText();

        // Create and/or open a database to read from it
        // This is the object where we connect it to the from activity to the database
        SQLiteDatabase db = MainActivity.userDbHelper.getWritableDatabase();

        // Create new map of values
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserInfo.UserEntry.COLUMN_USER_NAME,name.toString());
        contentValues.put(UserInfo.UserEntry.COLUMN_USER_ADDRESS,address.toString());
        contentValues.put(UserInfo.UserEntry.COLUMN_USER_REPLY, mReply);

        long newID;
        newID = db.insert(UserInfo.UserEntry.TABLE_NAME, null, contentValues);

        if(newID == -1){
            Toast.makeText(this,"Error with saving user info",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"User saved with row id : " + newID,Toast.LENGTH_SHORT).show();
        }
        Log.v("MainActivity","New row ID : " + newID);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertUserInfo();
                // Jump back to main activity by navigating it, not
                NavUtils.navigateUpFromSameTask(this);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
