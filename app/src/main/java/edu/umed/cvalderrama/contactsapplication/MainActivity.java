package edu.umed.cvalderrama.contactsapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import edu.umed.cvalderrama.contactsapplication.bean.Contact;
import edu.umed.cvalderrama.contactsapplication.database.DatabaseHelper;
import edu.umed.cvalderrama.contactsapplication.ui.ContactsAdapter;

/**
 * MainActivity class
 */
public class MainActivity extends AppCompatActivity {
    public final static int ADD_CONTACT = 1;
    public final static int EDIT_CONTACT = 2;
    public final static int VIEW_CONTACT = 3;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter contactsAdapter;
    private RecyclerView.LayoutManager recyclerManager;
    private DatabaseHelper db;
    private List<Contact> contacts;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState The saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_contact_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactForm();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.contactsListID);

        recyclerView.setHasFixedSize(true);

        recyclerManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerManager);

        db = new DatabaseHelper(this);

        contacts = db.getAllContacts();

        contactsAdapter = new ContactsAdapter(this, contacts);
        recyclerView.setAdapter(contactsAdapter);
    }

    /**
     * Called when a contextItem option is selected
     *
     * @param item The item being selected
     * @return If it was handled or not
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((ContactsAdapter) recyclerView.getAdapter()).getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        if (position == -1) {
            return super.onContextItemSelected(item);
        }

        Contact c = contacts.get(position);
        switch (item.getItemId()) {
            case R.id.menu_view_contact:
                showViewForm(c);
                break;
            case R.id.menu_edit_contact:
                showContactForm(c);
                break;
            case R.id.menu_delete_contact:
                deleteContact(c);
                Toast.makeText(this, R.string.contact_deleted, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
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
            updateContactList();
        }
    }

    /**
     * Called when the menu options is injected
     *
     * @param menu The menu
     * @return If the creation is correct
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when a menu item is selected
     *
     * @param item The item being selected
     * @return Menu was handled correctly or not
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_contact) {
            showContactForm();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes the ContactList and Adapter
     */
    private void updateContactList() {
        contacts.clear();
        contacts.addAll(db.getAllContacts());
        contactsAdapter.notifyDataSetChanged();
    }

    /**
     * Display the Contact Form for viewing
     * @param contact The contact to edit
     */
    private void showViewForm(Contact contact) {
        Intent intent = new Intent(this, ContactView.class);
        intent.putExtra("viewingId", contact.getId());
        startActivityForResult(intent, VIEW_CONTACT);
    }

    /**
     * Display the Contact Form
     */
    private void showContactForm() {
        Intent intent = new Intent(this, ContactForm.class);
        startActivityForResult(intent, ADD_CONTACT);
    }

    /**
     * Display the Contact Form for editting
     * @param contact The contact to edit
     */
    private void showContactForm(Contact contact) {
        Intent intent = new Intent(this, ContactForm.class);
        intent.putExtra("editingId", contact.getId());
        startActivityForResult(intent, EDIT_CONTACT);
    }

    /**
     * Delete the selected contact
     * @param contact The Contact to remove
     */
    private void deleteContact(Contact contact) {
        db.deleteContact(contact);
        if (!TextUtils.isEmpty(contact.getPhotoUri())) {
            File storedFile = new File(contact.getPhotoUri());
            storedFile.delete();
        }
        updateContactList();
    }
}
