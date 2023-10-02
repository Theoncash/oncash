package `in`.oncash.oncash.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import `in`.oncash.oncash.databinding.FragmentContactBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [contactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class contactFragment : Fragment() {


    lateinit var binding :FragmentContactBinding
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(inflater , container , false )
        return  binding.root




    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment contactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @SuppressLint("QueryPermissionsNeeded")
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            contactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }


                binding.whatspp.setOnClickListener {
                    val url = "https://chat.whatsapp.com/IDEyUIpJLChCgrT6TbE108" // Replace with the URL you want to open

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)

                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "No web browser found", Toast.LENGTH_SHORT).show()
                    }
                }


                binding.gmail.setOnClickListener {
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data = Uri.parse("mailto:onbread.assist@gmail.com")

                        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivity(emailIntent)
                        } else {
                            Toast.makeText(requireContext(), "No email client found", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

    }
}