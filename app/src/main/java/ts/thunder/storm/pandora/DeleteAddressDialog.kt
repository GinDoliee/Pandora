package ts.thunder.storm.pandora

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import ts.thunder.storm.pandora.databinding.DialogDeleteAddressBinding

class DeleteAddressDialog (context: Context, private val okCallback: (Boolean) -> Unit) : Dialog(context) {

    private lateinit var binding: DialogDeleteAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDeleteAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() = with(binding) {

        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnOK.setOnClickListener() {
            okCallback(true)
            dismiss()
        }

        binding.btnCancel.setOnClickListener() {
            dismiss()
        }
    }

}