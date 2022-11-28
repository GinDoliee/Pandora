package ts.thunder.storm.pandora

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.DecimalFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONTokener

import java.io.*
import java.lang.Math.round


class MainActivity : AppCompatActivity() {


    var isUpdateCoin = true

    var coinName:String = "FCT2"
    var coinPrice:Float = 0.0F
    var coinChange:Float = 0.0F
    var coinVolume:Int = 0

    val COINPRICE = 4

    val TRADE_PRICE = "trade_price"
    val TRADE_VOLUME = "acc_trade_price_24h"
    val TRADE_CHANGE = "signed_change_rate"

    lateinit var filePath:String

    val scopeUpBit = CoroutineScope(Dispatchers.Default + Job())
    val channel = Channel<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.viewpager.adapter = MyAdapter(this)

        binding.textName.text = coinName

        binding.textTotalFCT.text ="0"
        binding.textTotalKWR.text ="0"

        filePath = filesDir.path + "/Address.txt"
        readAddressFile(filePath)

        isUpdateCoin = true

        UpdateCoin()

        GlobalScope.launch(Dispatchers.Main) {
            channel.consumeEach {
                when(it){
                    COINPRICE -> {
                        val decimal = DecimalFormat("#,###.##")
                        CommonInfo.FCTValue = coinPrice

                        if(coinChange<0) {
                            binding.textChange.setTextColor(Color.BLUE)
                            binding.textPrice.setTextColor(Color.BLUE)
                        }else if(coinChange>0){
                            binding.textChange.setTextColor(Color.RED)
                            binding.textPrice.setTextColor(Color.RED)
                        }else{
                            binding.textChange.setTextColor(Color.BLACK)
                            binding.textPrice.setTextColor(Color.BLACK)
                        }

                        binding.textPrice.text = coinPrice.toString()
                        binding.textChange.text = decimal.format(coinChange).toString() + "%"
                        binding.textVolume.text = decimal.format(coinVolume).toString()
                    }
                }
            }
        }


    }

    fun UpdateCoin(){
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
                coinChange = findUpBitItem(it.toString(),TRADE_CHANGE).toFloat()*100
                coinVolume = round(findUpBitItem(it.toString(),TRADE_VOLUME).toFloat()/1000000)

            },{error->
                Toast.makeText(this,"UpBit Error : $error",Toast.LENGTH_SHORT).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
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


    fun showAddressDialog() {
        AddressDialog(this) {

            val file = File(filePath)

            if (!file.exists()) {
                file.createNewFile()
            }

            val fileWriter = FileWriter(file)
            val bwForFile =BufferedWriter(fileWriter)
            bwForFile.write(it.FCT_Name)
            bwForFile.newLine()
            bwForFile.write(it.FCT_Address)
            bwForFile.flush()
            bwForFile.close()

            CommonInfo.AddressNameArray.set(0,it.FCT_Name)
            CommonInfo.AddressArray.set(0,it.FCT_Address)

            CommonInfo.TotalAddressNumber = 1

        }.show()
    }

    fun readAddressFile(path: String) {
        val file = File(path)

        if(file.exists()){

            val br = BufferedReader(FileReader(file))
            var index = 0
            while(true){

                val nickname = br.readLine()?:break
                val address = br.readLine()?:break

                CommonInfo.AddressArray.set(index, address)
                CommonInfo.AddressNameArray.set(index,nickname)

                index++
            }
            CommonInfo.TotalAddressNumber = index
            br.close()
        }else{
            showAddressDialog()
        }
    }

    fun writeAddAddressFile(){

        val file = File(filePath)
        file.delete()

        if (!file.exists()) {
            file.createNewFile()
            Log.d("Hey","writeAddAddressFile createNewFile" )
        }

        val fileWriter = FileWriter(file)
        val bwForFile =BufferedWriter(fileWriter)
        for(i in 0 until CommonInfo.TotalAddressNumber){
            Log.d("Hey","write[$i]")
            bwForFile.write(CommonInfo.AddressNameArray.get(i))
            bwForFile.newLine()
            bwForFile.write(CommonInfo.AddressArray.get(i))
            bwForFile.newLine()
        }
            bwForFile.flush()
            bwForFile.close()
    }

}

class MyAdapter(activity:FragmentActivity):FragmentStateAdapter(activity){
    val fragments:List<Fragment>
    init{
        fragments = listOf(MainFragment(),SecondFragment(),ThirdFragment())
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}

