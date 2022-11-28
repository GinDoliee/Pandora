package ts.thunder.storm.pandora

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ts.thunder.storm.pandora.databinding.MainItemDataBinding

class MainListAdapter(val data: MutableList<Stake>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val decimal = DecimalFormat("#,###.##")

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