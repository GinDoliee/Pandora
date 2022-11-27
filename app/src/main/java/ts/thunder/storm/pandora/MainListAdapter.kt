package ts.thunder.storm.pandora

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ts.thunder.storm.pandora.databinding.MainItemDataBinding

class MainListAdapter(val data: MutableList<Stake>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MainHolder(MainItemDataBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MainHolder).binding
        val decimal = DecimalFormat("#,###.##")

        binding.textFCTAddress.text = data[position].FCT_Address
        binding.textAvailable.text = decimal.format(data[position].coinAvailableAmount).toString()
        binding.textDelegated.text = decimal.format(data[position].coinDelegatedAmount).toString()
        binding.textReward.text = decimal.format(data[position].coinRewardAmount).toString()

        val total = data[position].coinAvailableAmount + data[position].coinDelegatedAmount + data[position].coinRewardAmount
        binding.textFCTTotalAmount.text = decimal.format(total).toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
class MainHolder(val binding : MainItemDataBinding): RecyclerView.ViewHolder(binding.root){

}