package edu.umed.cvalderrama.contactsapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.umed.cvalderrama.contactsapplication.bean.Contact;
import edu.umed.cvalderrama.contactsapplication.database.DatabaseHelper;

/**
 * Class to handle the Contact Form
 */
public class ContactForm extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText telephoneInput;
    private ImageView contactView;
    private DatabaseHelper db;
    private Contact bindedContact;
    private File tempFile;
    private String contactPicturePath;

    /**
     * Created when the Activity is launched
     *
     * @param savedInstanceState The bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Init UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        // Bind UI
        firstNameInput = (EditText) findViewById(R.id.inputFirstName);
        lastNameInput = (EditText) findViewById(R.id.inputLastName);
        telephoneInput = (EditText) findViewById(R.id.inputTelephone);
        contactView = (ImageView) findViewById(R.id.imageView);
        Button takePicture = (Button) findViewById(R.id.capture_photo);

        // Bind listeners
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchTakePicture();
            }
        });

        // Check for parameters
        Bundle b = getIntent().getExtras();
        if (b != null) {
            long contactId = b.getLong("editingId");
            bindedContact = db.getContact(contactId);

            firstNameInput.setText(bindedContact.getFirstName());
            lastNameInput.setText(bindedContact.getLastName());
            telephoneInput.setText(bindedContact.getTelephone());

            if (!TextUtils.isEmpty(bindedContact.getPhotoUri())) {
                File storedFile = new File(bindedContact.getPhotoUri());
                Bitmap storedBmp = BitmapFactory.decodeFile(storedFile.getAbsolutePath());
                contactView.setImageBitmap(storedBmp);
            }
        } else {
            bindedContact = new Contact();
        }
    }

    /**
     * Called when the menu options is injected
     * @param menu The menu
     * @return If the creation is correct
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when a menu item is selected
     * @param item The item being selected
     * @return Menu was handled correctly or not
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact_menu_add) {
            saveContact();
            finish();
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
     * Method to save a Contact
     */
    private void saveContact() {
        if (TextUtils.isEmpty(firstNameInput.getText())) {
            Toast.makeText(this, R.string.value_required, Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(lastNameInput.getText())) {
            Toast.makeText(this, R.string.value_required, Toast.LENGTH_SHORT).show();
        }

        String fistName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String telephone = telephoneInput.getText().toString();

        bindedContact.setFirstName(fistName);
        bindedContact.setLastName(lastName);
        bindedContact.setTelephone(telephone);
        if (!TextUtils.isEmpty(contactPicturePath)) {
            bindedContact.setPhotoUri(contactPicturePath);
        }

        db.saveContact(bindedContact);

        Toast.makeText(this, "Saved with id:" + bindedContact.getId(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Launch a intent to take a picture from camera
     */
    private void launchTakePicture() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File destinationFile = null;
            try {
                destinationFile = createTempFile();
            } catch (IOException ioe) {
                Toast.makeText(this, R.string.picture_error_file, Toast.LENGTH_SHORT).show();
            }

            if (destinationFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "edu.umed.cvalderrama.contactsapplication", destinationFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Called when there is a result
     *
     * @param requestCode The request code
     * @param resultCode The result code
     * @param data The data back
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Resize Images
            try {
                Bitmap image = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                File destinationFile = createDestinationFile();
                Bitmap resized = resizeImage(image, 500, 500);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                destinationFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(destinationFile);
                fo.write(bytes.toByteArray());
                fo.close();

                tempFile.delete();

                contactPicturePath = destinationFile.getAbsolutePath();
                contactView.setImageBitmap(resized);
            } catch (IOException ioe) {
                Toast.makeText(this, R.string.picture_error_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Resizes a image maintaining the aspect ratio
     *
     * @param image The image to resize
     * @param maxWidth The maximum width
     * @param maxHeight The maximum height
     * @return The scaled Image
     */
    private static Bitmap resizeImage(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    /**
     * Create the destination file location
     *
     * @return The Destination File
     * @throws IOException When a error happens
     */
    private File createDestinationFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "contact_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Creates a temporal File to store the camera result
     *
     * @return The temporal File
     * @throws IOException When a error happens
     */
    private File createTempFile() throws IOException {
        String imageFileName = "temp_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        tempFile = image;
        return image;
    }
}
