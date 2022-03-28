package com.example.readcontact.database

import com.example.readcontact.models.Contacts

interface DbService {
    fun addContact(contacts: Contacts)
    fun showContact(): List<Contacts>
    fun editContact(contacts: Contacts): Int
    fun deleteContact(contacts: Contacts)
}