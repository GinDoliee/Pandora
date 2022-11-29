package ts.thunder.storm.pandora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ts.thunder.storm.pandora.databinding.FragmentThirdBinding


class ThirdFragment : Fragment() {

    lateinit var mainActivity: MainActivity

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

        for(i in 0 until CommonInfo.AddressInfo.size){
            stakeData.add(Stake(CommonInfo.AddressInfo.get(i).FCT_Address,CommonInfo.AddressInfo.get(i).FCT_Name))
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = ThirdListAdapter(mainActivity, stakeData)

        binding.btnAdd.setOnClickListener{
            showListDialog()
        }
    }

    fun addItem(stake: Stake){
        stakeData.add(stake)
        binding.recyclerView.adapter?.notifyDataSetChanged()
        CommonInfo.AddressInfo.add(stake)
        mainActivity.writeAddAddressFile()
    }

    private fun showListDialog() {
        AddressDialog(requireContext()) {
            addItem(it)
        }.show()
    }


}

