package com.example.readcontact.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.readcontact.R
import com.example.readcontact.adapters.Adapter
import com.example.readcontact.adapters.ItemClick
import com.example.readcontact.database.Dbhelper
import com.example.readcontact.databinding.DeleteContactDialogItemBinding
import com.example.readcontact.databinding.EditContactDialogItemBinding
import com.example.readcontact.databinding.FragmentListContactsBinding
import com.example.readcontact.models.Contacts


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("Range")

class ListContactsFragment : Fragment(), ItemClick {

    lateinit var binding: FragmentListContactsBinding
    lateinit var list: ArrayList<Contacts>
    lateinit var context: FragmentActivity
    private lateinit var adapter: Adapter
    private lateinit var database: Dbhelper
    private val permission = Manifest.permission.READ_CONTACTS
    private var permissionCall = Manifest.permission.CALL_PHONE
    private val permissionGranted = PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Dbhelper(requireActivity())
        context = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentListContactsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hasPermitted()
    }

    override fun callClick(contact: Contacts) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.number))

        if (ContextCompat.checkSelfPermission(context, permissionCall) != permissionGranted) {
            requestPermissions(arrayOf(permissionCall), 2)
        } else {
            startActivity(intent)
        }
    }

    override fun messageClick(position: Int) {
        findNavController().navigate(R.id.action_listContactsFragment_to_sendMessageFragment,
            bundleOf("contact" to list[position])
        )
    }

    override fun popupClick(contact: Contacts, position: Int, view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.popup_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.edit -> edit(contact)
                R.id.delete -> delete(contact)
            }
            true
        }
        popupMenu.show()
    }

    private fun edit(contact: Contacts) {
        val customDialog = AlertDialog.Builder(context).create()
        val bindingDialog = EditContactDialogItemBinding.inflate(layoutInflater)
        customDialog.setView(bindingDialog.root)
        customDialog.setCancelable(false)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingDialog.TIEName.setText(contact.name)
        bindingDialog.TIENumber.setText(contact.number)

        close(bindingDialog, customDialog)
        save(contact, bindingDialog, customDialog)

        customDialog.show()
    }

    private fun close(bindingDialog: EditContactDialogItemBinding, customDialog: AlertDialog) {
        bindingDialog.closeDialog.setOnClickListener {
            customDialog.dismiss()
        }
    }

    private fun save(
        contact: Contacts,
        bindingDialog: EditContactDialogItemBinding,
        customDialog: AlertDialog,
    ) {
        bindingDialog.save.setOnClickListener {
            contact.name = bindingDialog.TIEName.text.toString().trim()
            contact.number = bindingDialog.TIENumber.text.toString().trim()
            database.editContact(contact)
            loadData()
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            customDialog.dismiss()
        }
    }

    private fun delete(contact: Contacts) {
        val customDialog = AlertDialog.Builder(context).create()
        val bindingDialog = DeleteContactDialogItemBinding.inflate(layoutInflater)
        customDialog.setView(bindingDialog.root)
        customDialog.setCancelable(false)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        yes(contact, customDialog, bindingDialog)
        no(customDialog, bindingDialog)

        customDialog.show()
    }

    private fun yes(
        contact: Contacts,
        customDialog: AlertDialog,
        bindingDialog: DeleteContactDialogItemBinding,
    ) {
        bindingDialog.yes.setOnClickListener {
            database.deleteContact(contact)
            loadData()
            customDialog.dismiss()
        }
    }

    private fun no(customDialog: AlertDialog, bindingDialog: DeleteContactDialogItemBinding) {
        bindingDialog.no.setOnClickListener {
            customDialog.dismiss()
        }
    }


    private fun loadData() {
        list = ArrayList()
        val mlist = database.showContact() as ArrayList<Contacts>

        mlist.forEach {
            list.add(it)
        }

        adapter = Adapter(list, this)
        binding.recyclerView.adapter = adapter
    }

    private fun readContacts() {
        val mlist = database.showContact() as ArrayList<Contacts>

        val contact =
            context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null)
        while (contact!!.moveToNext()) {
            val contact = Contacts(
                contact.getString(contact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                contact.getString(contact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            )
            var check = true
            for (i in mlist) {
                if (i.number == contact.number) {
                    check = false
                    break
                }
            }
            if (check)
                database.addContact(contact)
        }
        contact.close()
    }

    private fun checkPermission() {
//        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)   // For Activity //
        requestPermissions(arrayOf(permission), 1)   // For Fragment //
    }

    private fun hasPermitted() {
        if (ContextCompat.checkSelfPermission(context, permission) == permissionGranted) {
            loadData()
//            Toast.makeText(requireActivity(), "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            checkPermission()
//            Toast.makeText(requireActivity(), "Permission isn't granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (permissions[0] == permission && grantResults[0] == permissionGranted) {
//                Toast.makeText(requireActivity(), "Permission granted", Toast.LENGTH_SHORT).show()
                readContacts()
                loadData()
            } else {
                requireActivity().finish()
//                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show()
//                checkPermission()
            }
        }
    }
}