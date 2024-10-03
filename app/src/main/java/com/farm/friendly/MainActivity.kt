package com.farm.friendly

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.farm.friendly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var countries: List<String>
    private lateinit var crops: List<String>

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

        val featureMin = doubleArrayOf(
            51.0, 0.04, 1.3, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )

        val featureMax = doubleArrayOf(
            3240.0, 367778.0, 30.65, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0
        )

        val rawInput = doubleArrayOf(
            100.0, 200000.0, 10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )

        binding.btnStart.setOnClickListener {
            showDialog()
        }

        val scaledInput = DoubleArray(rawInput.size)
        for (i in rawInput.indices) {
            scaledInput[i] = scaler.scale(rawInput[i], featureMin[i], featureMax[i])
        }

        // Run inference with ONNX model
        runInference(scaledInput)
    }

    private fun runInference(scaledInput: DoubleArray) {
        // Initialize ONNX Runtime environment
        val env = OrtEnvironment.getEnvironment()
        val session = env.createSession("model.onnx", OrtSession.SessionOptions())

        // Convert scaled input to ONNX Tensor
        val inputTensor = OrtTensor.createTensor(
            env, scaledInput, longArrayOf(
                1,
                scaledInput.size.toLong()
            )
        )

        // Run the model
        val result = session.run(mapOf("input" to inputTensor))

        // Process the result as needed
        val outputTensor = result[0].value as OrtTensor
        val outputArray = outputTensor.getValue() as FloatArray

        // Print or handle the output
        println("Model output: ${outputArray.joinToString(", ")}")
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
            val selectedCrops = spinCntry.selectedItem.toString()
            val rain = txtRain.text.toString()
            val pest = txtPest.text.toString()
            val temp = txtTemp.text.toString()

            println(selectedCntry)
            println(selectedCrops)
            println(rain)
            println(pest)
            println(temp)
            dialog.dismiss()
        }
        dialog.show()
    }
}
