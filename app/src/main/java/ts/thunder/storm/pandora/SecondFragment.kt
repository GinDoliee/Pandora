package ts.thunder.storm.pandora

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import ts.thunder.storm.pandora.CommonInfo.Companion.TRADE_CHANGE
import ts.thunder.storm.pandora.CommonInfo.Companion.TRADE_PRICE
import ts.thunder.storm.pandora.CommonInfo.Companion.TRADE_VOLUME
import ts.thunder.storm.pandora.databinding.FragmentMainBinding
import ts.thunder.storm.pandora.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {

    lateinit var binding : FragmentSecondBinding

    var supportCoinType = mutableListOf("ATOM", "MED", "LOOM", "ETH")
    var coinData = mutableListOf<Coin>()

    val channel = Channel<Int>()
    val scopeCoin = CoroutineScope(Dispatchers.Default + Job())

    lateinit var mainActivity: MainActivity

    var isUpdateCoin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d("Hey","onViewCreated Called")

        mainActivity = context as MainActivity

        isUpdateCoin = true

        for(i in 0 until supportCoinType.size){
            coinData.add(Coin(supportCoinType.get(i)))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = SecondListAdapter(mainActivity, coinData)


        GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                (binding.recyclerView.adapter as SecondListAdapter).notifyDataSetChanged()
            }
        }

        UpdateCoins()

    }

    fun UpdateCoins(){
        scopeCoin.launch {

            while(isUpdateCoin) {
                for (i in 0 until supportCoinType.size) {
                    UpdateCoins(supportCoinType.get(i), i)
                    channel.send(i)
                }
                Thread.sleep(1000)
            }
        }
    }

    fun UpdateCoins(name:String, index:Int){
        Log.d("Hey", "UpdateUpbit Called")
        val url = "https://api.upbit.com/v1/ticker?markets=KRW-"+name

        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                coinData.get(index).coinPrice = findCoinItem(it.toString(),TRADE_PRICE).toFloat()
                coinData.get(index).coinChange = findCoinItem(it.toString(),TRADE_CHANGE).toFloat()*100
                coinData.get(index).coinVolume = Math.round(findCoinItem(it.toString(),TRADE_VOLUME).toFloat()/1000000)

            },{error->
                Toast.makeText(activity,"UpBit Error : $error", Toast.LENGTH_SHORT).show()
            }
        )

        val queue = Volley.newRequestQueue(activity)
        queue.add(request)
    }


    fun findCoinItem(reponse:String, itemName:String):String{

        val itemIndex = reponse.indexOf(itemName)
        val itemLast = reponse.indexOf(":",itemIndex)
        var valueLast = reponse.indexOf(",",itemLast)
        if(valueLast < 1){
            valueLast = reponse.indexOf("}",itemLast)
        }

        return reponse.substring(itemLast+1,valueLast)
    }

    override fun onStart() {
        super.onStart()
        if(isUpdateCoin == false) {
            isUpdateCoin = true
            UpdateCoins()
        }
    }

    override fun onResume() {
        super.onResume()
        if(isUpdateCoin == false) {
            isUpdateCoin = true
            UpdateCoins()
        }
    }

    override fun onPause() {
        super.onPause()
        isUpdateCoin = false
    }

    override fun onStop() {
        super.onStop()
        isUpdateCoin = false
    }
}