package ts.thunder.storm.pandora

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import ts.thunder.storm.pandora.databinding.ThirdItemDataBinding

class ThirdListAdapter(var context: Context, val data: MutableList<Stake>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ThirdHolder(ThirdItemDataBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ThirdHolder).binding

        binding.textName.text = data[position].FCT_Name
        binding.textAddress.text = data[position].FCT_Address


    }

    override fun getItemCount(): Int {
        return data.size
    }



    inner class ThirdHolder(val binding : ThirdItemDataBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.buttonDelete.setOnClickListener{
                DeleteAddressDialog(context) {
                    if(it == true){
                        Log.d("Hey","adapterPosition = $adapterPosition")
                        CommonInfo.AddressInfo.removeAt(adapterPosition)
                        data.removeAt(adapterPosition)
                        (context as MainActivity).writeAddAddressFile()
                        notifyDataSetChanged()
                    }
                }.show()
            }
        }
    }

}




