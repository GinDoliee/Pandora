package ts.thunder.storm.pandora

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import ts.thunder.storm.pandora.databinding.FragmentMainBinding
import ts.thunder.storm.pandora.databinding.MainItemDataBinding
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


class MainFragment : Fragment() {


    lateinit var mainActivity: MainActivity

    lateinit var urlAvailable:String
    lateinit var urlDelegated:String
    lateinit var urlReward:String

    lateinit var FCT_Address:String
    lateinit var filePath:String

    lateinit var binding : FragmentMainBinding

    val FCT2_ADD_LENGTH = 44

    val DIVIDE_VALUE = 1000000
    val COINAVAILABLE = 1
    val COINDELEGATE = 2
    val COINREWARD = 3

    var coinAvailableAmount:Float = 0.0F
    var coinDelegatedAmount:Float = 0.0F
    var coinRewardAmount:Float = 0.0F




    val channel = Channel<Int>()
    val scopeFCT = CoroutineScope(Dispatchers.Default + Job())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainActivity = context as MainActivity
        filePath = mainActivity.filesDir.path + "/AddText.txt"
        readAddressFile(filePath)

        urlAvailable ="https://lcd-mainnet.firmachain.dev:1317/cosmos/bank/v1beta1/balances/" + FCT_Address
        urlDelegated ="https://lcd-mainnet.firmachain.dev:1317/cosmos/staking/v1beta1/delegations/" + FCT_Address
        urlReward = "https://lcd-mainnet.firmachain.dev:1317/cosmos/distribution/v1beta1/delegators/"+FCT_Address+ "/rewards"
        val test = Stake(FCT_Address)

        UpdateCoin()

        val mainScope = GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                when(it){
                    COINAVAILABLE ->
                        test.coinAvailableAmount = coinAvailableAmount
                    COINDELEGATE ->
                        test.coinDelegatedAmount = coinDelegatedAmount
                    COINREWARD -> {
                        test.coinRewardAmount = coinRewardAmount
                    }
                }
                val data = mutableListOf<Stake>()
                data.add(test)

                binding.recyclerView.layoutManager = LinearLayoutManager(activity)
                binding.recyclerView.adapter = MainListAdapter(data)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }


    fun UpdateCoin(){
        scopeFCT.launch {
            while(true) {
                UpdateFCTAmount(urlAvailable, "balances","amount")
                channel.send(COINAVAILABLE)

                UpdateFCTAmount(urlDelegated, "delegation_responses","shares")
                channel.send(COINDELEGATE)

                UpdateFCTAmount(urlReward, "total","amount")
                channel.send(COINREWARD)
                Thread.sleep(3000)
            }
        }
    }


    fun readAddressFile(path: String) {
        val file = File(path)

        if(file.exists()){
            val inputStream: InputStream = file.inputStream()
            Log.d("Hey","Address Length : ${inputStream.read()}")
            val address : InputStreamReader = inputStream.reader()
            FCT_Address = address.readText()
            address.close()
            Log.d("Hey","Address : $FCT_Address")
        }else{
            FCT_Address = "Need to Input Address"
            Toast.makeText(activity,"There is no address file", Toast.LENGTH_SHORT).show()
        }
    }

    fun findFCTItem(reponse:String, itemName:String):String{
        val itemIndex = reponse.indexOf(itemName)
        val itemLast = reponse.indexOf(":",itemIndex)
        var valueLast = reponse.indexOf("}",itemLast)
        return reponse.substring(itemLast+2,valueLast-1)
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

            val queue = Volley.newRequestQueue(activity)
            queue.add(stringRequest)
        }else{
            //Toast.makeText(this,"Need to input address", Toast.LENGTH_SHORT).show()
        }

    }

}




