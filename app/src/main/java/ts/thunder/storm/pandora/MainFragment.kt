package ts.thunder.storm.pandora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ts.thunder.storm.pandora.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    lateinit var binding : FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

}