package kz.kuz.implicitintents

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class MainFragment : Fragment() {
    // методы фрагмента должны быть открытыми
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.toolbar_title)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val btnSendMessage = view.findViewById<Button>(R.id.send_message)
        btnSendMessage.setOnClickListener {
            var i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, "Some long-long-long-long text")
            i.putExtra(Intent.EXTRA_SUBJECT, "Some subject")
//            i = Intent.createChooser(i, "Please select from list:")
            // Необходим для принудительного вывода выбора приложений
            startActivity(i)
        }
        val btnSelectContact = view.findViewById<Button>(R.id.select_contact)
        val pickContact = Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI)
        btnSelectContact.setOnClickListener { startActivityForResult(pickContact, 0) }

        // ниже код для защиты от отсутствия контактных приложений
        val packageManager = activity?.packageManager
        if (packageManager?.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) ==
                null) {
            btnSelectContact.isEnabled = false
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && data != null) {
            val contactUri = data.data
            // определение полей, значения которых должны быть возвращены запросом
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            // выполнение запроса - contactUri здесь выполняет функции условия "where"
            val c = activity?.contentResolver?.query(contactUri!!, queryFields,
                    null, null, null)
            try {
                // проверка получения результатов
                if (c?.count == 0) {
                    return
                }
                // извлечение первого столбца данных - имени контакта
                c?.moveToFirst()
                val contact = c?.getString(0)
                Toast.makeText(context, contact, Toast.LENGTH_SHORT).show()
            } finally {
                c?.close()
            }
        }
    }
}