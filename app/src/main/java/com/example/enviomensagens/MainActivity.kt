package com.example.enviomensagens

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.enviomensagens.classe.contato

private val PERMISSIONS_REQUEST_READ_CONTACTS = 100

class MainActivity : AppCompatActivity() {
    private var phones = mutableListOf<contato>()
    private lateinit var adapter: CTT_recyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewContatos)
        adapter = CTT_recyclerViewAdapter(this, phones) // Criação do adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        readContacts(recyclerView)
    }

    fun displayPhones(phones: List<contato>) {
        Log.v("ablube", "cheguei aqui")
        adapter.contatos = phones.toMutableList()
    }

    fun readContacts(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            );
            return
        }
        val getString = findViewById<EditText>(R.id.nomeSerPesquisado).text.toString()
        val seletorQueContem = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
        val nomeaSerContidoNaColuna = arrayOf("%$getString%")
        val ordemDeDisplay = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            ), seletorQueContem, nomeaSerContidoNaColuna, ordemDeDisplay
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
            phones.clear()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val hasPhone = cursor.getInt(phoneColumn)
                if (hasPhone > 0) {
                    contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id.toString()),
                        null
                    )?.use { phoneCursor ->
                        while (phoneCursor.moveToNext()) {
                            val phoneNumber = phoneCursor.getString(
                                with(phoneCursor) { getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) }
                            )
                            Log.v("tele", phoneNumber)
                            phones.add(contato(id, name, phoneNumber))
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
            displayPhones(phones)
        }
    }

    fun sendMessageOnWhatsApp(phone: String, nome: String) {
        val numeroTratado = phone.replace(" ", "")
        if(isInstalled("com.whatsapp", this)){
            Log.v("cheguei", numeroTratado)
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                val message = "Essa mensagem foi enviada pela atividade do trabalho de móvel, Sr(a) $nome!"
                val url = "https://wa.me/$numeroTratado?text=${Uri.encode(message)}"
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "O WhatsApp não está instalado no seu dispositivo.", Toast.LENGTH_SHORT).show()
            }
        }else {
            Log.v("cheguei", "deu bosta")
            showAlert(this)
        }
    }

    fun isInstalled(packgeName: String , context: Context): Boolean {
        try {
            context.packageManager.getPackageInfo(packgeName, PackageManager.GET_ACTIVITIES)
            return true
        }catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    fun showAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Atenção")
        builder.setMessage("você não tem o whatsApp instalado!!!")
        builder.setPositiveButton("ok"){dialog, _ -> }
        var alertaDeDialogo = builder.create()
        alertaDeDialogo.show()
    }
}

