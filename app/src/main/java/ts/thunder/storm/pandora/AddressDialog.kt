package ts.thunder.storm.pandora

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import ts.thunder.storm.pandora.databinding.DialogAddressBinding

class AddressDialog (context: Context,private val okCallback: (Stake) -> Unit) : Dialog(context) {

    private lateinit var binding: DialogAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {

        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnSaveAddress.setOnClickListener(){

            if(binding.editNickName.text.isNullOrBlank()){
                Toast.makeText(context, "Put NickName", Toast.LENGTH_SHORT).show()
            }else if(binding.editAddress.text.isNullOrBlank()){
                Toast.makeText(context, "Put Address", Toast.LENGTH_SHORT).show()
            }else{
                if (binding.editAddress.text.length != CommonInfo.FCT2_ADD_LENGTH) {
                    Toast.makeText(context, "Put Correct Address", Toast.LENGTH_SHORT).show()
                }else {
                    val stake = Stake(binding.editAddress.text.toString(),binding.editNickName.text.toString())
                    okCallback(stake)
                    dismiss()
                }
            }
        }

        binding.btnCancel.setOnClickListener(){
            dismiss()
        }

    }
}