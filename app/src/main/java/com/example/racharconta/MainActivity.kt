package com.example.racharconta

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var resultFinal: TextView
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        // Referências aos componentes da interface grafica
        val totalAmountEditText = findViewById<EditText>(R.id.editText)
        val numPeopleEditText = findViewById<EditText>(R.id.editTextNumber2)
        resultFinal = findViewById<TextView>(R.id.textView3)
        val shareResult = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val speakResult = findViewById<FloatingActionButton>(R.id.floatingActionButton2)


        // Função para calcular o valor rachado
        fun calculateAndDisplayResult() {
            val totalAmountString = totalAmountEditText.text.toString()
            val numPeopleString = numPeopleEditText.text.toString()

            if (totalAmountString.isNotEmpty() && numPeopleString.isNotEmpty()) {
                val totalAmount = totalAmountString.toDoubleOrNull()
                val numPeople = numPeopleString.toIntOrNull()

                if (totalAmount != null && numPeople != null && numPeople != 0) {
                    val result = totalAmount / numPeople
                    resultFinal.text = "R$ %.2f".format(result)
                } else {
                    resultFinal.text = "Invalido"
                }
            } else {
                resultFinal.text = ""
            }
        }
        totalAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndDisplayResult()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        numPeopleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndDisplayResult()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        fun shareResult() {
            val resultText = resultFinal.text.toString()
            if (resultText.isNotEmpty() && resultText != "Invalido" && resultText != "R$ 0,00") {
                val shareText = "Cada pessoa deve pagar: $resultText"

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "Text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)

            }

        }

        shareResult.setOnClickListener {
            shareResult()
        }
        speakResult.setOnClickListener {
            val resultText = resultFinal.text.toString()
            if (resultText.isNotEmpty() && resultText != "Invalido") {
                speakOut(resultText)
            }
        }


    }
    private fun speakOut(text: String) {
        if (this::tts.isInitialized && tts.isLanguageAvailable(Locale.getDefault()) >= TextToSpeech.LANG_AVAILABLE) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    override fun onDestroy() {
        if (this::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()

    }
}
