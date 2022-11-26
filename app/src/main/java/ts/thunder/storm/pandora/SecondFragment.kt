package ts.thunder.storm.pandora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ts.thunder.storm.pandora.databinding.FragmentMainBinding
import ts.thunder.storm.pandora.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {

    lateinit var binding : FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(inflater,container,false)
        return binding.root
    }


}