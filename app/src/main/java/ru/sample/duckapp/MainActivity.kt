package ru.sample.duckapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nextBtn = findViewById<Button>(R.id.nextButton)
        nextBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val userInput: String = findViewById<EditText>(R.id.textInput).text.toString()
                if (userInput.isBlank()) {
                    showRandomDuck()
                    return
                }

                val code = userInput.toInt()
                if (code !in 100..599) {
                    showError("Bad HTTP code")
                    return
                }

                showHttpDuck(code)
            }
        })
    }

    private fun showError(msg: String) = Toast
        .makeText(this, msg, Toast.LENGTH_SHORT)
        .show()

    private fun loadImage(url: String) =
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .into(findViewById<ImageView>(R.id.imageView))

    private fun showRandomDuck() {
        Api.ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
                override fun onFailure(call: Call<Duck>, t: Throwable) = showError("Something went wrong")

                override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                    if (!response.isSuccessful)
                        return

                    loadImage(response.body()!!.url)
                }
            }
        )
    }

    private fun showHttpDuck(code: Int) = loadImage("https://random-d.uk/api/v2/http/$code")
}