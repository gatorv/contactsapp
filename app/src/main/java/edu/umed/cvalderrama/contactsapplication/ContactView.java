package edu.umed.cvalderrama.contactsapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import edu.umed.cvalderrama.contactsapplication.bean.Contact;
import edu.umed.cvalderrama.contactsapplication.database.DatabaseHelper;

/**
 * Handles displaying a contact
 */
public class ContactView extends AppCompatActivity {
    public final static int EDIT_CONTACT = 2;

    private long contactId;
    private Contact bindedContact;
    private DatabaseHelper db;
    private TextView nameView;
    private TextView telephoneView;
    private ImageView contactPictureView;

    /**
     * Called when a Activity is created
     * @param savedInstanceState The called bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_view_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        nameView = (TextView) findViewById(R.id.contact_view_name);
        telephoneView = (TextView) findViewById(R.id.contact_view_telephone);
        contactPictureView = (ImageView) findViewById(R.id.contact_view_image);

        // Check for parameters
        Bundle b = getIntent().getExtras();
        if (b != null) {
            contactId = b.getLong("viewingId");
            loadContactToUi();
        }
    }

    /**
     * Called when the menu options is injected
     * @param menu The menu
     * @return If the creation is correct
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when a menu item is selected
     * @param item The item being selected
     * @return Menu was handled correctly or not
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact_view_edit:
                showContactForm();
                break;
            case R.id.contact_view_delete:
                deleteContact();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when moving to parent Activity
     *
     * @return To continue the up navigation or not
     */
    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    /**
     * Called when the activity is finished
     */
    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    /**
     * Process the result when editing the form
     *
     * @param requestCode The request code that was created
     * @param resultCode The result code that was returned
     * @param data The original intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            loadContactToUi();
        }
    }

    /**
     * Binds the contact to the Ui
     */
    private void loadContactToUi() {
        bindedContact = db.getContact(contactId);
        nameView.setText(bindedContact.getFullName());
        telephoneView.setText(bindedContact.getTelephone());

        if (!TextUtils.isEmpty(bindedContact.getPhotoUri())) {
            File storedFile = new File(bindedContact.getPhotoUri());
            Bitmap storedBmp = BitmapFactory.decodeFile(storedFile.getAbsolutePath());
            contactPictureView.setImageBitmap(storedBmp);
        }
    }

    /**
     * Display the Contact Form for editting
     */
    private void showContactForm() {
        Intent intent = new Intent(this, ContactForm.class);
        intent.putExtra("editingId", bindedContact.getId());
        startActivityForResult(intent, EDIT_CONTACT);
    }

    /**
     * Delete the selected contact
     */
    private void deleteContact() {
        db.deleteContact(bindedContact);
        if (!TextUtils.isEmpty(bindedContact.getPhotoUri())) {
            File storedFile = new File(bindedContact.getPhotoUri());
            storedFile.delete();
        }
        finish();
    }
}
