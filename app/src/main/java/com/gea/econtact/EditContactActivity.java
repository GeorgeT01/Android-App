package com.gea.econtact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditContactActivity extends AppCompatActivity {
    private Button datePickerBtnEdit;
    private Context context;
    private CircleImageView imageViewEdit;
    public static final int PICK_IMAGE = 7777;
    private Database database;
    private EditText nameEdit, emailEdit, phoneEdit, desEdit;
    RadioGroup genderRadioGroupEdit;
    private RadioButton radioButtonEdit;
    private String _contactId;
    private  Bitmap bmp;
    private boolean emptyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        setTitle("Edit Contact");
        context = this;
        database = new Database(context);
        //force-edittext-to-remove-focus
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        emptyDate =true;



        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        desEdit = findViewById(R.id.desEdit);
        genderRadioGroupEdit = findViewById(R.id.genderRadioGroupEdit);
        phoneEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        datePickerBtnEdit = findViewById(R.id.datePickerEdit);

        datePickerBtnEdit.setOnClickListener(new View.OnClickListener() {
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
                                datePickerBtnEdit.setText(_day + "." + _month + "." + year);
                                emptyDate = false;
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show(); // show dialog
            }

        });

        // browse images
        imageViewEdit = findViewById(R.id.imgViewEdit);

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });


        Intent intent = getIntent();
        String contact_id = intent.getStringExtra("contact_id"); //if it's a string you stored.

        String[] data = database.readSingle(contact_id); // get info
        byte[] imageByte = database.getContactImage(contact_id); //get Image

        bmp = BitmapFactory.decodeByteArray(imageByte,0, imageByte.length);
        imageViewEdit.setImageBitmap(bmp);

        _contactId = data[0];
        nameEdit.setText(data[1]);
        emailEdit.setText(data[2]);
        phoneEdit.setText(data[3]);
        String _birthDate = data[4];
        if (_birthDate.isEmpty()){
            datePickerBtnEdit.setText("Choose Date");
            emptyDate =true;
        }else{
            datePickerBtnEdit.setText(_birthDate);
            emptyDate =false;
        }
        //contactGender.setText(data[5]);
        desEdit.setText(data[6]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            imageViewEdit.setImageURI(selectedImage);
        }else{
            imageViewEdit.setImageBitmap(bmp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_btn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        // get selected radio button from radioGroup
        int selectedId = genderRadioGroupEdit.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioButtonEdit = findViewById(selectedId);

        if (id == R.id.saveBtn) {

            if (TextUtils.isEmpty(nameEdit.getText().toString())){
                nameEdit.setError("Name is required");
                nameEdit.requestFocus();
            }else {

                //onvert-an-imageview-to-byte-array
                Bitmap bitmap = ((BitmapDrawable) imageViewEdit.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] imageInByte = stream.toByteArray();
                String _birthDate;
                if (emptyDate){
                    _birthDate ="";
                }else{
                    _birthDate = datePickerBtnEdit.getText().toString();
                }

                database.updateContact(new ContactModel(
                        _contactId,
                        nameEdit.getText().toString(),
                        emailEdit.getText().toString(),
                        phoneEdit.getText().toString(),
                        _birthDate,
                        radioButtonEdit.getText().toString(),
                        desEdit.getText().toString(),
                        imageInByte
                ));
                Toast.makeText(context, "Contact saved", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }else if(id == android.R.id.home){
           super.onBackPressed();
           return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
