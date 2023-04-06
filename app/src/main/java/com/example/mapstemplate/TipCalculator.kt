package com.example.mapstemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.mapstemplate.R
import com.example.mapstemplate.databinding.ActivityTipCalculatorBinding
import java.text.NumberFormat

class TipCalculator : AppCompatActivity() {
    // Binding object instance with access to the views in the activity_main.xml layout
    private lateinit var binding: ActivityTipCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout XML file and return a binding object instance
        binding = ActivityTipCalculatorBinding.inflate(layoutInflater)

        // Set the content view of the Activity to be the root view of the layout
        setContentView(binding.root)

        // Setup a click listener on the calculate button to calculate the tip
        binding.calculateButton.setOnClickListener { calculateTip() }

        // Set up a key listener on the EditText field to listen for "enter" button presses
        binding.costOfServiceEditText.setOnKeyListener { view, keyCode, _ ->
            handleKeyEvent(
                view,
                keyCode
            )
        }
    }
    /**
     * Calculates the tip based on the user input.
     */
    private fun calculateTip() {
        // Get the decimal value from the cost of service EditText field
        val stringInTextField = binding.costOfServiceEditText.text.toString()
        val cost = stringInTextField.toDoubleOrNull()

        // If the cost is null or 0, then display 0 tip and exit this function early.
        if (cost == null || cost == 0.0) {
            displayTip(0.0)
            return
        }

        // Get the tip percentage based on which radio button is selected
        val tipPercentage = when (binding.tipOptions.checkedRadioButtonId) {
            R.id.option_America-> 0.20
            R.id.option_Germany-> 0.18
            R.id.option_Spain-> 0.15
            R.id.option_Italy-> 0.12
            R.id.option_Holland-> 0.13
            R.id.option_Iceland-> 0.19
            else -> 0.10
        }

        // Calculate the tip
        var tip = tipPercentage * cost

        // If the switch for rounding up the tip toggled on (isChecked is true), then round up the
        // tip. Otherwise do not change the tip value.
        val roundUp = binding.roundUpSwitch.isChecked
        if (roundUp) {
            // Take the ceiling of the current tip, which rounds up to the next integer, and store
            // the new value in the tip variable.
            tip = kotlin.math.ceil(tip)
        }

        // Display the formatted tip value onscreen
        displayTip(tip)
    }

    /**
     * Format the tip amount according to the local currency and display it onscreen.
     * Example would be "Tip Amount: $10.00".
     */
    private fun displayTip(tip: Double) {
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        binding.tipResult.text = getString(R.string.tip_amount, formattedTip)
    }

    /**
     * Key listener for hiding the keyboard when the "Enter" button is tapped.
     */
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}