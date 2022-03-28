package com.example.readcontact.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.readcontact.database.DbConstants.CONTACT
import com.example.readcontact.database.DbConstants.DB_NAME
import com.example.readcontact.database.DbConstants.DB_VERSION
import com.example.readcontact.database.DbConstants.ID
import com.example.readcontact.database.DbConstants.NAME
import com.example.readcontact.database.DbConstants.NUMBER
import com.example.readcontact.models.Contacts

class Dbhelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), DbService {

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "create table $CONTACT($ID integer not null primary key autoincrement unique, $NAME text not null, $NUMBER text not null)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


    override fun addContact(contacts: Contacts) {
        val writableDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NAME, contacts.name)
        contentValues.put(NUMBER, contacts.number)
        writableDatabase.insert(CONTACT, null, contentValues)
        writableDatabase.close()
    }

    override fun showContact(): List<Contacts> {
        val list = ArrayList<Contacts>()
        val readableDatabase = this.readableDatabase
        val query = "select * from $CONTACT"
        val cursor = readableDatabase.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val name = cursor.getString(1)
                val number = cursor.getString(2)
                val contact = Contacts(id, name, number)
                list.add(contact)
            } while (cursor.moveToNext())
        }
        return list
    }

    override fun editContact(contacts: Contacts): Int {
        val readableDatabase = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, contacts.id)
        contentValues.put(NAME, contacts.name)
        contentValues.put(NUMBER, contacts.number)

        return readableDatabase.update(CONTACT,
            contentValues,
            "$ID = ?",
            arrayOf("${contacts.id}"))
    }

    override fun deleteContact(contacts: Contacts) {
        val writableDatabase = this.writableDatabase
        writableDatabase.delete(CONTACT, "$ID = ?", arrayOf("${contacts.id}"))
        writableDatabase.close()
    }
}