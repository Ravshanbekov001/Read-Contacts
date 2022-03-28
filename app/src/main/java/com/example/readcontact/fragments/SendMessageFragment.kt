package com.example.readcontact.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.readcontact.R
import com.example.readcontact.databinding.FragmentSendMessageBinding
import com.example.readcontact.models.Contacts

class SendMessageFragment : Fragment() {

    private lateinit var binding: FragmentSendMessageBinding
    private lateinit var contacts: Contacts
    private lateinit var contact: Contacts
    lateinit var context: FragmentActivity
    private val permission = Manifest.permission.SEND_SMS
    private val permissionGranted = PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireActivity()
        contact = Contacts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentSendMessageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactInfo()
        sendClick(contact)
        back()
    }

    private fun back() {
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun contactInfo() {
        contacts = arguments?.getSerializable("contact") as Contacts
        binding.contactName.text = contacts.name
        binding.contactNumber.text = contacts.number
    }

    private fun sendClick(contact: Contacts) {
        binding.sendBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context, permission) != permissionGranted) {
                requestPermissions(arrayOf(permission), 1)
            } else {
                val message = binding.message.text.toString()
                val sms = SmsManager.getDefault()
                sms.sendTextMessage("${binding.contactNumber.text}", null, message, null, null)
                Toast.makeText(context, "Sending...", Toast.LENGTH_SHORT).show()
                binding.message.text.clear()
            }
        }
    }
}