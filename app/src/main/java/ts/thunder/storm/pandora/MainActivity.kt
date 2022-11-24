package ts.thunder.storm.pandora

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
import java.io.*


class MainActivity : AppCompatActivity() {


    val DIVIDE_VALUE = 1000000

    var locationFlag: Boolean = false
    var coinName:String = "FCT2"
    var coinPrice:Float = 0.0F
    var coinAmount:Float = 0.0F
    var coinTotalSum:Long = 0L

    val TRADE_PRICE = "trade_price"
    val FCT2_AMOUNT = "amount"


    val FCT2_ADD_LENGTH = 44

    lateinit var FCT_Address:String
    lateinit var filePath:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewCoinName.text = coinName
        binding.textViewAmount.text = coinAmount.toString()
        binding.textViewCoinValue.text = coinPrice.toString()
        binding.textViewTotalKRW.text = coinTotalSum.toString()


        filePath = filesDir.path + "/AddText.txt"
        readAddressFile(binding.editAddress, filePath)


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

        binding.btnConnectFCT.setOnClickListener(){

            if(FCT_Address.length == FCT2_ADD_LENGTH) {

                Toast.makeText(this, "Connecting FCT2 Wallet", Toast.LENGTH_SHORT).show()
                val url =
                    "https://lcd-mainnet.firmachain.dev:1317/cosmos/staking/v1beta1/delegations/" + FCT_Address
                coinAmount = 0F
                val stringRequest = StringRequest(
                    Request.Method.GET,
                    url,
                    Response.Listener<String> {

                        val response = JSONObject(JSONTokener(it))

                        val jsonArray: JSONArray = response.optJSONArray("delegation_responses")
                        val types: List<String> = (0 until jsonArray.length()).map {
                            jsonArray.getString(it).toString()
                        }

                        for (i in 0 until types.size) {
                            coinAmount += findItem(types[i], FCT2_AMOUNT).toFloat() / DIVIDE_VALUE
                            Log.d("Hey", "coinAmount : $coinAmount")
                        }
                        binding.textViewAmount.text = coinAmount.toString()
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, "FCT Error : $error", Toast.LENGTH_SHORT).show()
                    })

                val queue = Volley.newRequestQueue(this)
                queue.add(stringRequest)
            }else{
                Toast.makeText(this,"Need to input address", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnConnectUpBit.setOnClickListener {
            Toast.makeText(this,"Searching Value : $coinName",Toast.LENGTH_SHORT).show()

            val url = "https://api.upbit.com/v1/ticker?markets=KRW-"+coinName

            val request = StringRequest(
                Request.Method.GET,
                url,
                {
                    coinPrice = findItem(it.toString(),TRADE_PRICE).toFloat()
                    binding.textViewCoinValue.text = coinPrice.toString()
                },{error->
                    Toast.makeText(this,"UpBit Error : $error",Toast.LENGTH_SHORT).show()
                }
            )

            val queue = Volley.newRequestQueue(this)
            queue.add(request)
        }

        binding.btnTotalSum.setOnClickListener(){
            coinTotalSum = (coinAmount * coinPrice).toLong()
            val decimal = DecimalFormat("#,###.##")
            binding.textViewTotalKRW.text = decimal.format(coinTotalSum).toString() + "원"


        }

        binding.btnAddSave.setOnClickListener(){

            FCT_Address = binding.editAddress.getText().toString()
            Log.d("Hey","Edit Address : $FCT_Address , Length : ${FCT_Address.length}")

            if(FCT_Address.length != FCT2_ADD_LENGTH ){
                Toast.makeText(this,"Need to input address", Toast.LENGTH_SHORT).show()
            }else {

                val file = File(filePath)

                if (!file.exists()) {
                    file.createNewFile()
                }

                val outputStream: OutputStream = file.outputStream()
                outputStream.write(FCT_Address.length + 1)

                val osw: OutputStreamWriter = outputStream.writer()
                osw.write(FCT_Address)
                osw.close()
            }

        }
    }

    fun findItem(reponse:String, itemName:String):String{

        val itemIndex = reponse.indexOf(itemName)
        val itemLast = reponse.indexOf(":",itemIndex)
        var valueLast = reponse.indexOf(",",itemLast)
        if(valueLast < 1){
            valueLast = reponse.indexOf("}",itemLast)
        }

        if(itemName.equals(FCT2_AMOUNT)){
            return reponse.substring(itemLast+2,valueLast-1)
        }else{
            return reponse.substring(itemLast+1,valueLast)
        }
    }

    fun readAddressFile(editText: EditText,path: String) {
        val file = File(path)

        if(file.exists()){
            val inputStream:InputStream = file.inputStream()
            Log.d("Hey","Address Length : ${inputStream.read()}")
            val address : InputStreamReader= inputStream.reader()
            FCT_Address = address.readText()
            address.close()
            Log.d("Hey","Address : $FCT_Address")
        }else{
            FCT_Address = "Need to Input Address"
            Toast.makeText(this,"There is no address file", Toast.LENGTH_SHORT).show()
        }
        editText.setText(FCT_Address)
    }

}



