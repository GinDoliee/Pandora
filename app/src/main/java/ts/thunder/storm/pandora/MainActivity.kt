package ts.thunder.storm.pandora

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
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


    var isUpdateCoin = true
    val DIVIDE_VALUE = 1000000

    var coinName:String = "FCT2"
    var coinPrice:Float = 0.0F
    var coinAvailableAmount:Float = 0.0F
    var coinDelegatedAmount:Float = 0.0F
    var coinRewardAmount:Float = 0.0F

    val COINAVAILABLE = 1
    val COINDELEGATE = 2
    val COINREWARD = 3
    val COINPRICE = 4

    val TRADE_PRICE = "trade_price"


    val FCT2_ADD_LENGTH = 44

    lateinit var FCT_Address:String
    lateinit var filePath:String

    val scopeFCT = CoroutineScope(Dispatchers.Default + Job())
    val scopeUpBit = CoroutineScope(Dispatchers.Default + Job())
    val channel = Channel<Int>()



    lateinit var urlAvailable:String
    lateinit var urlDelegated:String
    lateinit var urlReward:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewCoinName.text = coinName


        filePath = filesDir.path + "/AddText.txt"
        readAddressFile(binding.editAddress, filePath)

        urlAvailable ="https://lcd-mainnet.firmachain.dev:1317/cosmos/bank/v1beta1/balances/" + FCT_Address
        urlDelegated ="https://lcd-mainnet.firmachain.dev:1317/cosmos/staking/v1beta1/delegations/" + FCT_Address
        urlReward = "https://lcd-mainnet.firmachain.dev:1317/cosmos/distribution/v1beta1/delegators/"+FCT_Address+ "/rewards"

        isUpdateCoin = true

        UpdateCoin()



        val mainScope = GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                val decimal = DecimalFormat("#,###.##")
                when(it){
                    COINAVAILABLE ->
                        binding.textAvailable.text = decimal.format(coinAvailableAmount).toString()
                    COINDELEGATE ->
                        binding.textDelegated.text = decimal.format(coinDelegatedAmount).toString()
                    COINREWARD -> {
                        binding.textReward.text = decimal.format(coinRewardAmount).toString()
                        val coinTotalSum = coinAvailableAmount + coinDelegatedAmount + coinRewardAmount
                        binding.textFCTTotalAmount.text =decimal.format(coinTotalSum).toString()
                        binding.textViewTotalKRW.text = decimal.format(coinTotalSum*coinPrice).toString() + "원"
                    }
                    COINPRICE ->
                        binding.textViewCoinName.text = "$coinName : ${coinPrice.toString()}"
                }
            }
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

    fun UpdateCoin(){
        scopeFCT.launch {
            while(isUpdateCoin) {
                UpdateFCTAmount(urlAvailable, "balances","amount")
                channel.send(COINAVAILABLE)

                UpdateFCTAmount(urlDelegated, "delegation_responses","shares")
                channel.send(COINDELEGATE)

                UpdateFCTAmount(urlReward, "total","amount")
                channel.send(COINREWARD)
                Thread.sleep(3000)
            }
        }

        scopeUpBit.launch {
            while(isUpdateCoin) {
                UpdateUpbit(coinName)
                channel.send(COINPRICE)
                Thread.sleep(3000)
            }
        }


    }

    fun UpdateUpbit(name:String){


        Log.d("Hey", "UpdateUpbit Called")
        val url = "https://api.upbit.com/v1/ticker?markets=KRW-"+name

        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                coinPrice = findUpBitItem(it.toString(),TRADE_PRICE).toFloat()
            },{error->
                Toast.makeText(this,"UpBit Error : $error",Toast.LENGTH_SHORT).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    fun UpdateFCTAmount(url:String, type:String, item:String){

        Log.d("Hey", "UpdateFCTAmout Called")
        if(FCT_Address.length == FCT2_ADD_LENGTH) {


            val stringRequest = StringRequest(
                Request.Method.GET,
                url,
                Response.Listener<String> {

                val response = JSONObject(JSONTokener(it))
                val jsonArray: JSONArray = response.optJSONArray(type)


                    if(type.equals("balances")) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        coinAvailableAmount = (jsonObject.getString(item)).toFloat()/DIVIDE_VALUE
                        Log.d("Hey","coinAvailableAmount = $coinAvailableAmount")
                    }

                    if(type.equals("delegation_responses")){

                        val types: List<String> = (0 until jsonArray.length()).map {
                            jsonArray.getString(it).toString()
                        }

                        var delegateSUM = 0.0F

                        for (i in 0 until types.size) {
                            delegateSUM += findFCTItem(types[i], item).toFloat() / DIVIDE_VALUE
                            coinDelegatedAmount = delegateSUM
                            Log.d("Hey", "coinDelegatedAmount[$i] : $coinDelegatedAmount")
                        }
                    }

                    if(type.equals("total")){
                        val jsonObject = jsonArray.getJSONObject(0)
                        coinRewardAmount = (jsonObject.getString(item)).toFloat()/DIVIDE_VALUE
                        Log.d("Hey","coinRewardAmount = $coinRewardAmount")

                    }

                },
                Response.ErrorListener { error ->
                    Log.d("Hey","UpdateFCTAmount = ${error.toString()}")
                })

            val queue = Volley.newRequestQueue(this)
            queue.add(stringRequest)
        }else{
            //Toast.makeText(this,"Need to input address", Toast.LENGTH_SHORT).show()
        }

    }




    fun findUpBitItem(reponse:String, itemName:String):String{

        val itemIndex = reponse.indexOf(itemName)
        val itemLast = reponse.indexOf(":",itemIndex)
        var valueLast = reponse.indexOf(",",itemLast)
        if(valueLast < 1){
            valueLast = reponse.indexOf("}",itemLast)
        }

        return reponse.substring(itemLast+1,valueLast)
    }

    fun findFCTItem(reponse:String, itemName:String):String{
        val itemIndex = reponse.indexOf(itemName)
        val itemLast = reponse.indexOf(":",itemIndex)
        var valueLast = reponse.indexOf("}",itemLast)
       return reponse.substring(itemLast+2,valueLast-1)
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

    override fun onStop() {
        Log.d("Hey","onStop")
        isUpdateCoin = false
        super.onStop()
    }

    override fun onPause() {
        Log.d("Hey","onPause")
        isUpdateCoin = false
        super.onPause()
    }

    override fun onRestart() {
        Log.d("Hey","onRestart")
        isUpdateCoin = true
        UpdateCoin()
        super.onRestart()
    }
}



