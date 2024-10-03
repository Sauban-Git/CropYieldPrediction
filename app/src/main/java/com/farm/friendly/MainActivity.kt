package com.farm.friendly


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.farm.friendly.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var countries: List<String>
    private lateinit var crops: List<String>

    private lateinit var result: TextView

    private val client = OkHttpClient()
    private val BASE_URL = "http://192.168.139.251:5000/predict"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        countries = listOf(
            "Albania",
            "Algeria",
            "Angola",
            "Argentina",
            "Armenia",
            "Australia",
            "Austria",
            "Azerbaijan",
            "Bahamas",
            "Bahrain",
            "Bangladesh",
            "Belarus",
            "Belgium",
            "Botswana",
            "Brazil",
            "Bulgaria",
            "Burkina Faso",
            "Burundi",
            "Cameroon",
            "Canada",
            "Central African Republic",
            "Chile",
            "Colombia",
            "Croatia",
            "Denmark",
            "Dominican Republic",
            "Ecuador",
            "Egypt",
            "El Salvador",
            "Eritrea",
            "Estonia",
            "Finland",
            "France",
            "Germany",
            "Ghana",
            "Greece",
            "Guatemala",
            "Guinea",
            "Guyana",
            "Haiti",
            "Honduras",
            "Hungary",
            "India",
            "Indonesia",
            "Iraq",
            "Ireland",
            "Italy",
            "Jamaica",
            "Japan",
            "Kazakhstan",
            "Kenya",
            "Latvia",
            "Lebanon",
            "Lesotho",
            "Libya",
            "Lithuania",
            "Madagascar",
            "Malawi",
            "Malaysia",
            "Mali",
            "Mauritania",
            "Mauritius",
            "Mexico",
            "Montenegro",
            "Morocco",
            "Mozambique",
            "Namibia",
            "Nepal",
            "Netherlands",
            "New Zealand",
            "Nicaragua",
            "Niger",
            "Norway",
            "Pakistan",
            "Papua New Guinea",
            "Peru",
            "Poland",
            "Portugal",
            "Qatar",
            "Romania",
            "Rwanda",
            "Saudi Arabia",
            "Senegal",
            "Slovenia",
            "South Africa",
            "Spain",
            "Sri Lanka",
            "Sudan",
            "Suriname",
            "Sweden",
            "Switzerland",
            "Tajikistan",
            "Thailand",
            "Tunisia",
            "Turkey",
            "Uganda",
            "Ukraine",
            "United Kingdom",
            "Uruguay",
            "Zambia",
            "Zimbabwe"
        )
        crops = listOf(
            "Cassava",
            "Maize",
            "Plantains and others",
            "Potatoes",
            "Rice, paddy",
            "Sorghum",
            "Soybeans",
            "Sweet potatoes",
            "Wheat",
            "Yams"
        )

        binding.btnStart.setOnClickListener {
            showDialog()
        }
    }


    // for showing custom dialog
    @SuppressLint("InflateParams", "MissingInflatedId")
    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null, false)

        val spinCntry: Spinner = dialogView.findViewById(R.id.spinCntry)
        val spinCrop: Spinner = dialogView.findViewById(R.id.spinCrop)
        val txtRain: EditText = dialogView.findViewById(R.id.txtRain)
        val txtPest: EditText = dialogView.findViewById(R.id.txtPest)
        val txtTemp: EditText = dialogView.findViewById(R.id.txtTemp)

        val cntryAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        cntryAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinCntry.adapter = cntryAdaptor

        val cropAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, crops)
        cropAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinCrop.adapter = cropAdaptor


        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Select Options")
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            val selectedCntry = spinCntry.selectedItem.toString()
            val selectedCrop = spinCrop.selectedItem.toString()
            val rain = txtRain.text.toString()
            val pest = txtPest.text.toString()
            val temp = txtTemp.text.toString()

            makePrediction(selectedCntry,selectedCrop,rain,pest,temp)

            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun makePrediction(selectedCntry: String, selectedCrop: String,rain: String, pest: String, temp: String) {
        try {
            val json = JSONObject().apply {
                put("average_rain_fall_mm_per_year", rain)
                put("pesticides_tonnes", pest)
                put("avg_temp", temp)
                put("Area", selectedCntry)
                put("Item", selectedCrop)
            }

            val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())
            val request = Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val myResponse = response.body?.string()
                        runOnUiThread {
                            result.text = "Predicted Yield: ${myResponse?.let { JSONObject(it).getString("prediction") }}"
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
