package ts.thunder.storm.pandora

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ts.thunder.storm.pandora.databinding.FragmentSecondBinding
import ts.thunder.storm.pandora.databinding.FragmentThirdBinding


class ThirdFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    lateinit var binding : FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainActivity = context as MainActivity

        binding.textName1.text =CommonInfo.AddressNameArray.get(0)
        binding.textAddress1.text = CommonInfo.AddressArray.get(0)

        binding.textName2.text =CommonInfo.AddressNameArray.get(1)
        binding.textAddress2.text = CommonInfo.AddressArray.get(1)

        binding.textName3.text =CommonInfo.AddressNameArray.get(2)
        binding.textAddress3.text = CommonInfo.AddressArray.get(2)



        binding.buttonAdd1.setOnClickListener(){
            showAddressDialog(1)
        }

        binding.buttonDelete1.setOnClickListener(){
            showDeleteAddressDialog(1)

        }

        binding.buttonAdd2.setOnClickListener(){
            showAddressDialog(2)
        }

        binding.buttonDelete2.setOnClickListener(){
            showDeleteAddressDialog(2)
        }

        binding.buttonAdd3.setOnClickListener(){
            showAddressDialog(3)
        }


        binding.buttonDelete3.setOnClickListener(){
            showDeleteAddressDialog(3)
        }
    }

    private fun showAddressDialog(index :Int) {
        AddressDialog(requireContext()) {
            when(index){
                1->{
                    binding.textName1.text = it.FCT_Name
                    binding.textAddress1.text = it.FCT_Address
                }
                2->{
                    binding.textName2.text = it.FCT_Name
                    binding.textAddress2.text = it.FCT_Address
                }
                3->{
                    binding.textName3.text = it.FCT_Name
                    binding.textAddress3.text = it.FCT_Address
                }
            }
            Log.d("Hey","dialog : $it")
        }.show()
    }

    private fun showDeleteAddressDialog(index:Int){
        DeleteAddressDialog(requireContext()){
            when(index){
                1->{
                    binding.textName1.text = ""
                    binding.textAddress1.text = ""
                }
                2->{
                    binding.textName2.text = ""
                    binding.textAddress2.text = ""
                }
                3->{
                    binding.textName3.text = ""
                    binding.textAddress3.text = ""
                }
            }
        }.show()
    }

}
