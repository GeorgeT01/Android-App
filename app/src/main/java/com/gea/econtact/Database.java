package com.gea.econtact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }
    //////////////////////////SHOPPING CART////////////////////////////////////////////
    public List<ContactModel> readContacts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"Id", "Image", "Name","Email", "Phone", "BirthDate", "Gender", "Description"};
        String sqlTable = "contacts";
        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect,null,null,null,null,"Name ASC");

        final List<ContactModel> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new ContactModel(
                        c.getString(c.getColumnIndex("Id")),
                        c.getString(c.getColumnIndex("Name")),
                        c.getString(c.getColumnIndex("Email")),
                        c.getString(c.getColumnIndex("Phone")),
                        c.getString(c.getColumnIndex("BirthDate")),
                        c.getString(c.getColumnIndex("Gender")),
                        c.getString(c.getColumnIndex("Description")),
                        c.getBlob(c.getColumnIndex("Image"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void  addContact(ContactModel contactModel){
         SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", contactModel.getId());
        cv.put("Image", contactModel.getImage());
        cv.put("Name", contactModel.getName());
        cv.put("Email", contactModel.getEmail());
        cv.put("Phone", contactModel.getPhone());
        cv.put("BirthDate", contactModel.getDateBirth());
        cv.put("Gender", contactModel.getGender());
        cv.put("Description", contactModel.getDescription());
        db.insert("contacts", null, cv);
        db.close();
    }

    public void deleteContact(String contact_id){
        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM contacts WHERE Id='"+contact_id+"'";
        db.execSQL(query);
        db.close();
    }


    public String[] readSingle(String contact_id){
        String[] results = new String[7];
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT *FROM contacts WHERE Id = '"+contact_id+"'",null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                results[0] = cursor.getString(cursor.getColumnIndex("Id"));
                //results[1] = cursor.getString(cursor.getColumnIndex("Image"));
                results[1] = cursor.getString(cursor.getColumnIndex("Name"));
                results[2] = cursor.getString(cursor.getColumnIndex("Email"));
                results[3] = cursor.getString(cursor.getColumnIndex("Phone"));
                results[4] = cursor.getString(cursor.getColumnIndex("BirthDate"));
                results[5] = cursor.getString(cursor.getColumnIndex("Gender"));
                results[6] = cursor.getString(cursor.getColumnIndex("Description"));
            }
            cursor.close();
        }
        db.close();
        return results;
    }

    public byte[] getContactImage(String contact_id){
        byte[] results = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT *FROM contacts WHERE Id = '"+contact_id+"'",null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                results = cursor.getBlob(cursor.getColumnIndex("Image"));
            }
            cursor.close();
        }
        db.close();
        return results;
    }


    public void updateContact(ContactModel contactModel){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Id", contactModel.getId());
        cv.put("Image", contactModel.getImage());
        cv.put("Name", contactModel.getName());
        cv.put("Email", contactModel.getEmail());
        cv.put("Phone", contactModel.getPhone());
        cv.put("BirthDate", contactModel.getDateBirth());
        cv.put("Gender", contactModel.getGender());
        cv.put("Description", contactModel.getDescription());
        //db.insert("contacts", null, cv);
        db.update("contacts", cv, "Id='"+contactModel.getId()+"'", null);

        db.close();
    }
}
