package ts.thunder.storm.pandora

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import ts.thunder.storm.pandora.databinding.FragmentSecondBinding
import ts.thunder.storm.pandora.databinding.FragmentThirdBinding


class ThirdFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var mainFragment: MainFragment

    lateinit var binding : FragmentThirdBinding

    var stakeData = mutableListOf<Stake>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivity = context as MainActivity

        for(i in 0 until CommonInfo.TotalAddressNumber){
            stakeData.add(Stake(CommonInfo.AddressArray.get(i).toString(),CommonInfo.AddressNameArray.get(i).toString()))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = ThirdListAdapter(stakeData)

        binding.btnAdd.setOnClickListener{
            showListDialog()
        }

    }

    fun addItem(stake: Stake){
        stakeData.add(Stake(stake.FCT_Address,stake.FCT_Name))
        binding.recyclerView.adapter?.notifyDataSetChanged()
        CommonInfo.AddressArray.set(CommonInfo.TotalAddressNumber,stake.FCT_Address)
        CommonInfo.AddressNameArray.set(CommonInfo.TotalAddressNumber,stake.FCT_Address)
        CommonInfo.TotalAddressNumber++
        mainActivity.writeAddAddressFile()

    }


    private fun showListDialog() {
        AddressDialog(requireContext()) {
            addItem(it)
            Log.d("Hey","dialog : $it")
        }.show()
    }


}

