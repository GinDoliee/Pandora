package ts.thunder.storm.pandora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import ts.thunder.storm.pandora.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnConnect.setOnClickListener(){
            val channel = Channel<Int>()
            val scope = CoroutineScope(Dispatchers.Default + Job())

            scope.launch {


                val result = 10


                channel.send(result)
            }

            val mainScope = GlobalScope.launch(Dispatchers.Main) {
                channel.consumeEach {
                    binding.textViewCoinName.text = "Result : $it"
                }

            }

        }




    }
}



