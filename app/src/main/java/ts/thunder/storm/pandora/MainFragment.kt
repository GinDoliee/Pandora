package ts.thunder.storm.pandora

import android.app.Activity
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
    lateinit var urlRewardLast:String

    lateinit var FCT_Address:String


    lateinit var binding : FragmentMainBinding



    val DIVIDE_VALUE = 1000000


    var coinAvailableAmount:Float = 0.0F
    var coinDelegatedAmount:Float = 0.0F
    var coinRewardAmount:Float = 0.0F

    var isUpdateFCT:Boolean = false


    val channel = Channel<Int>()
    val scopeFCT = CoroutineScope(Dispatchers.Default + Job())
    var stakeData = mutableListOf<Stake>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        isUpdateFCT = true

        mainActivity = context as MainActivity

        urlAvailable ="https://lcd-mainnet.firmachain.dev:1317/cosmos/bank/v1beta1/balances/"
        urlDelegated ="https://lcd-mainnet.firmachain.dev:1317/cosmos/staking/v1beta1/delegations/"
        urlReward = "https://lcd-mainnet.firmachain.dev:1317/cosmos/distribution/v1beta1/delegators/"
        urlRewardLast = "/rewards"








        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = MainListAdapter(stakeData)

        UpdateCoin()

        GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                    if((binding.recyclerView.adapter as MainListAdapter).itemCount != CommonInfo.TotalAddressNumber){
                        ChangeItem()
                    }

                    var stake = Stake(CommonInfo.AddressArray.get(it).toString(),CommonInfo.AddressNameArray.get(it).toString())
                    stake.coinAvailableAmount = coinAvailableAmount
                    stake.coinDelegatedAmount = coinDelegatedAmount
                    stake.coinRewardAmount = coinRewardAmount
                    (binding.recyclerView.adapter as MainListAdapter).ChangeItem(it,stake)
                }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun ChangeItem(){
        stakeData.clear()
        binding.recyclerView.adapter?.notifyDataSetChanged()
        for(i in 0 until CommonInfo.TotalAddressNumber){
            stakeData.add(Stake(CommonInfo.AddressArray.get(i).toString(),CommonInfo.AddressNameArray.get(i).toString()))
        }
    }

    fun UpdateCoin(){
        scopeFCT.launch {
            while(isUpdateFCT) {
                for (i in 0 until CommonInfo.TotalAddressNumber) {

                    Log.d("Hey", "UpdateCoin : $i")

                    UpdateFCTAmount(urlAvailable+CommonInfo.AddressArray.get(i).toString(), "balances", "amount")

                    UpdateFCTAmount(urlDelegated+CommonInfo.AddressArray.get(i).toString(), "delegation_responses", "balance")

                    UpdateFCTAmount(urlReward+CommonInfo.AddressArray.get(i).toString() + urlRewardLast, "total", "amount")
                    Thread.sleep(1000)
                    channel.send(i)

                }
            }
        }
    }


    fun UpdateFCTAmount(url:String, type:String, item:String){

        Log.d("Hey", "UpdateFCTAmout Called")


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

                        var delegateSUM = 0.0F

                        for(i in 0 until jsonArray.length()){
                            var jsonObject = jsonArray.getJSONObject(i)
                            var ToString = jsonObject.getString(item)
                            var ToMap = JSONObject(JSONTokener(ToString))
                            delegateSUM  += ToMap.get("amount").toString().toFloat()/DIVIDE_VALUE
                        }
                        coinDelegatedAmount = delegateSUM
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

    }

    override fun onPause() {
        super.onPause()
        Log.d("Hey", "MainFrag onPause")
        isUpdateFCT = false
    }

    override fun onStop() {
        super.onStop()
        Log.d("Hey", "MainFrag onStop")
        isUpdateFCT = false
    }

    override fun onStart() {
        super.onStart()
        Log.d("Hey", "MainFrag onStart")
        if(isUpdateFCT == false){
            isUpdateFCT = true
            UpdateCoin()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Hey", "MainFrag onResume")
        if(isUpdateFCT == false){
            isUpdateFCT = true
            UpdateCoin()
        }
    }
}




