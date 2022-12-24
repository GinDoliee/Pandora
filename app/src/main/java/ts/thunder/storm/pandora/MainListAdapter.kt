package ts.thunder.storm.pandora

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.NonDisposableHandle.parent
import ts.thunder.storm.pandora.databinding.MainItemDataBinding

class MainListAdapter(val data: MutableList<Stake>, fragContext: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val decimal = DecimalFormat("#,###.##")
    var context: Context = fragContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MainHolder(MainItemDataBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MainHolder).binding

        binding.textFCTAddress.text = data[position].FCT_Name
        binding.textAvailable.text = decimal.format(data[position].coinAvailableAmount).toString()
        binding.textDelegated.text = decimal.format(data[position].coinDelegatedAmount).toString()
        binding.textReward.text = decimal.format(data[position].coinRewardAmount).toString()

        val total = data[position].coinAvailableAmount + data[position].coinDelegatedAmount + data[position].coinRewardAmount
        binding.textFCTTotalAmount.text = decimal.format(total).toString()
        binding.textFCTTotalValue.text = decimal.format(total * CommonInfo.FCTValue).toString() + "원"


        binding.btnCopyToClip.setOnClickListener(){

            val text = data[position].FCT_Name + "\n" +
                    "총합 : ${decimal.format(total).toString()}" + "\n" +
                    "한화 : ${decimal.format(total * CommonInfo.FCTValue).toString()} 원"

            val clipboard: ClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("FCTAmount", text)

            clipboard.setPrimaryClip(clip)


            Toast.makeText(context, "Copied to ClipBoard", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun ChangeItem(index:Int, stake:Stake){

        data[index].coinAvailableAmount = stake.coinAvailableAmount
        data[index].coinDelegatedAmount = stake.coinDelegatedAmount
        data[index].coinRewardAmount = stake.coinRewardAmount

        notifyDataSetChanged()
    }

    fun SetItem(index:Int){


    }



}
class MainHolder(val binding : MainItemDataBinding): RecyclerView.ViewHolder(binding.root){

}