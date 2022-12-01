package ts.thunder.storm.pandora

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import ts.thunder.storm.pandora.databinding.SecondItemDataBinding
import ts.thunder.storm.pandora.databinding.ThirdItemDataBinding

class SecondListAdapter(var context: Context, val data: MutableList<Coin>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val decimal = DecimalFormat("#,###.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SecondHolder(SecondItemDataBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as SecondHolder).binding

        if(data[position].coinChange<0) {
            binding.textPrice.setTextColor(Color.BLUE)
            binding.textChange.setTextColor(Color.BLUE)
        }else if(data[position].coinChange>0){
            binding.textPrice.setTextColor(Color.RED)
            binding.textChange.setTextColor(Color.RED)
        }else{
            binding.textPrice.setTextColor(Color.BLACK)
            binding.textChange.setTextColor(Color.BLACK)
        }

        binding.textName.text = data[position].cointName
        binding.textPrice.text = decimal.format(data[position].coinPrice).toString()
        binding.textChange.text = decimal.format(data[position].coinChange).toString() + "%"
        binding.textVolume.text = "(mil."+ decimal.format(data[position].coinVolume).toString() +")"

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class SecondHolder(val binding : SecondItemDataBinding): RecyclerView.ViewHolder(binding.root){

        init {

        }
    }

}




