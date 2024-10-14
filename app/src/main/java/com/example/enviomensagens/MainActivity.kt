package com.example.enviomensagens

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

class MainActivity : AppCompatActivity() {
    private val phones = mutableListOf<contato>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun displayPhones(phones: List<contato>){
        val displayContatos = findViewById<LinearLayout>(R.id.contatos)
        displayContatos.removeAllViews()
            for (contato in phones){
                val newTextView = LayoutInflater.from(this).inflate(R.layout.contacto, null, false)
                newTextView.findViewById<TextView>(R.id.nomeContacto).text = contato.name
                newTextView.findViewById<TextView>(R.id.numeroText).text = contato.phone
                displayContatos.addView(newTextView)
                Log.v("ablube",contato.name)
            }
    }


        fun readContacts(view: View) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS);
                return
            }

            val getString = findViewById<EditText>(R.id.nomeSerPesquisado).text.toString()
            val seletorQueContem = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
            val nomeaSerContidoNaColuna = arrayOf("%$getString%")
            val ordemDeDisplay = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"

            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            ), seletorQueContem, nomeaSerContidoNaColuna, ordemDeDisplay)?.use { cursor ->
                val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

                while (cursor.moveToNext()){
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val phone = cursor.getInt(phoneColumn)
                    phones.add(contato(id, name, phone.toString()))
                }
            }
            displayPhones(phones)
        }
    }

data class contato(
    val id: Long,
    val name: String,
    val phone: String
)

