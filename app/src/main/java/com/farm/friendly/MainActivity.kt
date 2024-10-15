package com.farm.friendly


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.farm.friendly.databinding.ActivityMainBinding
import com.farm.friendly.databinding.DialogCustomBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var countries: List<String>
    private lateinit var crops: List<String>

    private lateinit var ipAdd: EditText

    private val client = OkHttpClient()
    private var urlServer = "https://web-production-6ef1.up.railway.app/predict"

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

        ipAdd = findViewById(R.id.ipAdd)
        binding.btnStart.setOnClickListener {
            val ipAddress = ipAdd.text.toString().trim()
            if (ipAddress.isNotEmpty()) {
                urlServer = "http://$ipAddress:5000/predict"
                showDialog()
            }else {
                showDialog()
            }
        }
    }


    // for showing custom dialog
    @SuppressLint("InflateParams")
    private fun showDialog() {
        // Use DialogCustomBinding for the dialog
        val dialogBinding = DialogCustomBinding.inflate(layoutInflater)

        // Set up spinners using the dialog binding
        val spinCntry: Spinner = dialogBinding.spinCntry
        val spinCrop: Spinner = dialogBinding.spinCrop
        val txtRain: EditText = dialogBinding.txtRain
        val txtPest: EditText = dialogBinding.txtPest
        val txtTemp: EditText = dialogBinding.txtTemp
        val result = dialogBinding.result // Get the result TextView from the dialog binding

        val cntryAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        cntryAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinCntry.adapter = cntryAdaptor

        val cropAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, crops)
        cropAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinCrop.adapter = cropAdaptor

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Select Options")
            .setView(dialogBinding.root)

        val dialog = dialogBuilder.create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSubmit.setOnClickListener {
            val selectedCntry = spinCntry.selectedItem.toString()
            val selectedCrop = spinCrop.selectedItem.toString()
            val rain = txtRain.text.toString()
            val pest = txtPest.text.toString()
            val temp = txtTemp.text.toString()

            // Validate input
            if (rain.isEmpty() || pest.isEmpty() || temp.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Return to prevent further execution
            }

            makePrediction(selectedCntry, selectedCrop, rain, pest, temp, result) // Pass result to update

            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun makePrediction(selectedCntry: String, selectedCrop: String, rain: String, pest: String, temp: String, result: TextView) {
        val json = JSONObject().apply {
            put("average_rain_fall_mm_per_year", rain)
            put("pesticides_tonnes", pest)
            put("avg_temp", temp)
            put("Area", selectedCntry)
            put("Item", selectedCrop)
        }

        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(urlServer)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, " Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val myResponse = response.body?.string()
                    runOnUiThread {
                        result.text = "Predicted Yield: ${myResponse?.let { JSONObject(it).getString("prediction") }} Tonnes per Hectare"
                        Toast.makeText(this@MainActivity, "Predicted Yield: ${myResponse?.let { JSONObject(it).getString("prediction") }} Tonnes per Hectare", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.body?.string()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error: ${response.code}, Response: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
