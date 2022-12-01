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
import ts.thunder.storm.pandora.CommonInfo.Companion.DIVIDE_VALUE
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

    lateinit var binding : FragmentMainBinding

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

        UpdateItem()
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = MainListAdapter(stakeData)


        UpdateCoin()

        GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                if((binding.recyclerView.adapter as MainListAdapter).itemCount != CommonInfo.AddressInfo.size){
                    UpdateItem()
                }

                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun UpdateItem(){

        stakeData.clear()
        for(i in 0 until CommonInfo.AddressInfo.size){
            stakeData.add(Stake(CommonInfo.AddressInfo.get(i).FCT_Address,CommonInfo.AddressInfo.get(i).FCT_Name))
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    fun UpdateCoin(){
        scopeFCT.launch {
            while(isUpdateFCT) {

                for (i in 0 until CommonInfo.AddressInfo.size) {

                    Log.d("Hey", "UpdateCoin : $i")

                    val address =CommonInfo.AddressInfo.get(i).FCT_Address

                    UpdateFCTAmount(urlAvailable+address, "balances", "amount",i)
                    Thread.sleep(600/CommonInfo.AddressInfo.size.toLong())
                    UpdateFCTAmount(urlDelegated+address, "delegation_responses", "balance",i)
                    Thread.sleep(600/CommonInfo.AddressInfo.size.toLong())
                    UpdateFCTAmount(urlReward+address + urlRewardLast, "total", "amount",i)
                    Thread.sleep(600/CommonInfo.AddressInfo.size.toLong())

                    channel.send(i)
                }
            }
        }
    }



    fun UpdateFCTAmount(url:String, type:String, item:String, index:Int){

        Log.d("Hey", "UpdateFCTAmout Called[$index]")


            val stringRequest = StringRequest(
                Request.Method.GET,
                url,
                Response.Listener<String> {

                    val response = JSONObject(JSONTokener(it))
                    val jsonArray: JSONArray = response.optJSONArray(type)


                    if(type.equals("balances")) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        stakeData.get(index).coinAvailableAmount =(jsonObject.getString(item)).toFloat()/DIVIDE_VALUE
                        Log.d("Hey","coinAvailableAmount[$index] = ${stakeData.get(index).coinAvailableAmount}")
                    }

                    else if(type.equals("delegation_responses")){

                        var delegateSUM = 0.0F

                        for(i in 0 until jsonArray.length()){
                            var jsonObject = jsonArray.getJSONObject(i)
                            var ToString = jsonObject.getString(item)
                            var ToMap = JSONObject(JSONTokener(ToString))
                            delegateSUM  += ToMap.get("amount").toString().toFloat()/DIVIDE_VALUE
                        }
                        stakeData.get(index).coinDelegatedAmount = delegateSUM
                        Log.d("Hey","coinDelegatedAmount[$index] = ${stakeData.get(index).coinDelegatedAmount}")
                    }

                    else if(type.equals("total")){
                        val jsonObject = jsonArray.getJSONObject(0)
                        stakeData.get(index).coinRewardAmount = (jsonObject.getString(item)).toFloat()/DIVIDE_VALUE

                        Log.d("Hey","coinRewardAmount[$index] = ${stakeData.get(index).coinRewardAmount}")

                    }
                    else{
                        Log.d("Hey","There is nothing")
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
        Log.d("Hey", "MainFrag onStart ${(binding.recyclerView.adapter as MainListAdapter).itemCount}")
        if(isUpdateFCT == false){
            isUpdateFCT = true
            if((binding.recyclerView.adapter as MainListAdapter).itemCount != CommonInfo.AddressInfo.size){
                UpdateItem()
            }
            UpdateCoin()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Hey", "MainFrag onResume ${(binding.recyclerView.adapter as MainListAdapter).itemCount} ")
        if(isUpdateFCT == false){
            isUpdateFCT = true
            if((binding.recyclerView.adapter as MainListAdapter).itemCount != CommonInfo.AddressInfo.size){
                UpdateItem()
            }
            UpdateCoin()
        }
    }
}




