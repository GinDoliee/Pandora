package ts.thunder.storm.pandora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ts.thunder.storm.pandora.databinding.FragmentSecondBinding
import ts.thunder.storm.pandora.databinding.FragmentThirdBinding


class ThirdFragment : Fragment() {

    lateinit var binding : FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater,container,false)
        return binding.root
    }

}