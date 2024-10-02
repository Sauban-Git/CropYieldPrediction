package com.farm.friendly

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

        countries = listOf("USA","India","Canada","Australia")
        crops = listOf("Maze","Potatoes","Rice","Corn")

        binding.btnStart.setOnClickListener {
            Toast.makeText(this, "Hello and Welcome!", Toast.LENGTH_SHORT).show()
            showDialog()
        }
    }

    @SuppressLint("InflateParams", "MissingInflatedId")
    private fun showDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null, false)

        val spinCntry: Spinner = dialogView.findViewById(R.id.spinCntry)
        val spinCrop: Spinner = dialogView.findViewById(R.id.spinCrop)
        val txtRain: EditText = dialogView.findViewById(R.id.txtRain)
        val txtPest: EditText = dialogView.findViewById(R.id.txtPest)
        val txtTemp: EditText = dialogView.findViewById(R.id.txtTemp)

        val cntryAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        val cropAdaptor = ArrayAdapter(this, android.R.layout.simple_spinner_item, crops)

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
            
            println("$selectedCntry")
            println("$selectedCrops")
            println("$rain")
            println("$pest")
            println("$temp")
            dialog.dismiss()
        }
        dialog.show()
    }
}