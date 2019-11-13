package com.gea.econtact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ContactDetailsActivity extends AppCompatActivity {
    private Database database;
    private Context context;
    private Button contactName, contactEmail, contactPhone, contactBirthDate, contactGender, contactDes;
    ImageButton callBtn, mailBtn, shareBtn;
    ImageView contactImage;
    String _contactId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Intent intent = getIntent();
        String contact_id = intent.getStringExtra("contact_id");
        contactName = findViewById(R.id.nameDetails);
        contactEmail = findViewById(R.id.emailDetails);
        contactPhone = findViewById(R.id.phoneDetails);
        contactBirthDate = findViewById(R.id.dateDetails);
        contactGender = findViewById(R.id.genderDetails);
        contactDes = findViewById(R.id.desDetails);
        contactImage = findViewById(R.id.imgViewDetails);

        callBtn = findViewById(R.id.callBtn);
        mailBtn = findViewById(R.id.mailBtn);
        shareBtn = findViewById(R.id.shareBtn);

        context = this;
        database = new Database(context);
        final String[] data = database.readSingle(contact_id); // get info
        byte[] imageByte = database.getContactImage(contact_id); //get Image

        Bitmap bmp = BitmapFactory.decodeByteArray(imageByte,0, imageByte.length);
        contactImage.setImageBitmap(bmp);

        _contactId = data[0];
        contactName.setText(data[1]);
        contactEmail.setText(data[2]);
        contactPhone.setText(data[3]);
        contactBirthDate.setText(data[4]);
        contactGender.setText(data[5]);
        contactDes.setText(data[6]);

        setTitle(data[1]);

        //call btn
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ data[3]));
                startActivity(callIntent);
            }
        });

        mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{data[2]});
                email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Email:"));
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = data[1]+"\n"+data[2]+"\n"+data[3];
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, data[1]);
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.editMenuBtn) {
            Intent intent = new Intent(context, EditContactActivity.class);
            intent.putExtra("contact_id", _contactId); //Optional parameters
            context.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
