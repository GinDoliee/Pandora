package ts.thunder.storm.pandora

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import org.json.JSONObject
import ts.thunder.storm.pandora.databinding.ActivityMainBinding
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONTokener


class MainActivity : AppCompatActivity() {

    var locationFlag: Boolean = false
    var coinName:String = "FCT2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.textViewCoinName.text = coinName

/*
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
*/

        binding.btnConnect.setOnClickListener {

            Toast.makeText(this,"Searching Value : $coinName",Toast.LENGTH_SHORT).show()
            val url = "https://api.upbit.com/v1/ticker?markets=KRW-"+coinName

            val request = StringRequest(
                Request.Method.GET,
                url,
                {
                    binding.textViewCoinValue.text = findItem(it.toString(),"opening_price")
                },{error->
                    Toast.makeText(this,"Error : $error",Toast.LENGTH_SHORT).show()
                }
            )

            val queue = Volley.newRequestQueue(this)
            queue.add(request)

        }

    }

    fun findItem(reponse:String, itemName:String):String{

        val itemIndex = reponse.indexOf(itemName)
        val itemLast = reponse.indexOf(":",itemIndex)
        val valueLast = reponse.indexOf(",",itemLast)
        val value = reponse.substring(itemLast+1,valueLast)

        return value
    }

}



