package com.example.currencyconversionapi_icetask2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitInstance {
    private const val BASE_URL = "https://api.getgeoapi.com/"

    val api: CurrencyApiService by lazy {
        val gson = GsonBuilder()
            .setLenient()  // Enable lenient mode
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CurrencyApiService::class.java)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: CurrencyApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Retrofit and API service
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.getgeoapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(CurrencyApiService::class.java)

        val fromCurrencyInput = findViewById<EditText>(R.id.fromCurrencyInput)
        val toCurrencyInput = findViewById<EditText>(R.id.toCurrencyInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val convertButton = findViewById<Button>(R.id.convertButton)

        convertButton.setOnClickListener {
            val fromCurrency = fromCurrencyInput.text.toString().uppercase()
            val toCurrency = toCurrencyInput.text.toString().uppercase()
            val amount = amountInput.text.toString().toDoubleOrNull()

            if (fromCurrency.isEmpty() || toCurrency.isEmpty() || amount == null) {
                Toast.makeText(this, "Please enter valid inputs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val apiKey = "28ecf0d12635f97135352eb77e66fbb92a73255b"

            apiService.getExchangeRate(apiKey, fromCurrency, toCurrency, amount)
                .enqueue(object : Callback<CurrencyResponse> {
                    override fun onResponse(
                        call: Call<CurrencyResponse>,
                        response: Response<CurrencyResponse>
                    ) {
                        if (response.isSuccessful) {
                            // Log the JSON response (optional)
                            val currencyResponse = response.body()
                            val jsonResponse = GsonBuilder().create().toJson(currencyResponse)
                            println("JSON Response: $jsonResponse")

                            if (currencyResponse != null) {
                                val exchangeRate = currencyResponse.rates[toCurrency]?.rate_for_amount
                                resultTextView.text = "Result: $exchangeRate $toCurrency"
                                resultTextView.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(this@MainActivity, "Error: Empty response body", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // Log or display the error response body
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@MainActivity, "Error: $errorBody", Toast.LENGTH_LONG).show()
                            println("Error body: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // Handle window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
