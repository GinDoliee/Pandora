package ts.thunder.storm.pandora

import android.icu.text.DecimalFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ts.thunder.storm.pandora.databinding.MainItemDataBinding
import ts.thunder.storm.pandora.databinding.ThirdItemDataBinding

class ThirdListAdapter(val data: MutableList<Stake>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ThirdHolder(ThirdItemDataBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ThirdHolder).binding

        binding.textName.text = data[position].FCT_Name
        binding.textAddress.text = data[position].FCT_Address

        binding.buttonDelete.setOnClickListener{
            binding.textName.text = ""
            binding.textAddress.text = ""
            notifyDataSetChanged()
            Log.d("Hey","buttonDelete : $position")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun ChangeItem(index:Int, stake:Stake){

        data[index].FCT_Name = stake.FCT_Name
        data[index].FCT_Address = stake.FCT_Address

        notifyDataSetChanged()
    }

    fun AddItem(stake:Stake){


    }

}


class ThirdHolder(val binding : ThirdItemDataBinding): RecyclerView.ViewHolder(binding.root){

}