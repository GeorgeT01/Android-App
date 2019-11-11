package com.gea.econtact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactActivity extends AppCompatActivity {

    private Button datePickerBtn;
    private Context context;
    private CircleImageView imageView;
    public static final int PICK_IMAGE = 7777;
    private Database database;
    private EditText nameEt, emailEt, phoneEt, desEt;
    RadioGroup genderRadioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        // Title
        setTitle("Create Contact");
        context = this;
        database = new Database(context);
        //force-edittext-to-remove-focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        nameEt = findViewById(R.id.nameEditText);
        emailEt = findViewById(R.id.emailEditText);
        phoneEt = findViewById(R.id.phoneEditText);
        desEt = findViewById(R.id.desEditTxt);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        datePickerBtn = findViewById(R.id.datePicker);

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                month += 1;
                                String _month, _day;
                                if(month< 10){ _month = "0"+(month); }
                                else{ _month = Integer.toString(month); }
                                if(day < 10){ _day = "0"+day; }
                                else{ _day = Integer.toString(day); }
                                datePickerBtn.setText(_day + "." + _month + "." + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show(); // show dialog
            }

        });

        // browse images
        imageView = findViewById(R.id.imgView);


        Random rand = new Random();
        Uri _imgUri= null;
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt(((6 - 1) + 1)+1);

        switch (randomNum){
            case 1:
                _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default);
                break;
            case 2:
                _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default_blue);
                break;
            case 3:
                _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default_green);
                break;
            case 4:
                _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default_red);
                break;
            case 5:
                _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default_yellow);
                break;
            case 6:
                _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default_black);
                break;
                default:
                    _imgUri =Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default);
        }
        imageView.setImageURI(_imgUri);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
          //  }
        //}catch (Exception e){
          //  e.printStackTrace();
        }else{
                Uri imgUri=Uri.parse("android.resource://com.gea.econtact/"+R.drawable.contact_default);
                imageView.setImageURI(imgUri);
            }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_btn, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // get selected radio button from radioGroup
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButton = findViewById(selectedId);

        if (id == R.id.saveBtn) {

            if (TextUtils.isEmpty(nameEt.getText().toString())){
                nameEt.setError("Name is required");
                nameEt.requestFocus();
            }else {
                // create a uniqe id
                String uniqueID = UUID.randomUUID().toString();

                //onvert-an-imageview-to-byte-array
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] imageInByte = stream.toByteArray();

                database.addContact(new ContactModel(
                        uniqueID,
                        nameEt.getText().toString(),
                        emailEt.getText().toString(),
                        phoneEt.getText().toString(),
                        datePickerBtn.getText().toString(),
                        radioButton.getText().toString(),
                        desEt.getText().toString(),
                        imageInByte
                ));
                Toast.makeText(context, "Contact saved", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
