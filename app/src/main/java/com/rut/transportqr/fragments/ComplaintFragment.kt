package com.rut.transportqr.fragments

import android.os.Build
import com.rut.transportqr.viewModel.ComplaintViewModel
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.rut.transportqr.R
import com.rut.transportqr.databinding.FragmentComplaintBinding
import com.rut.transportqr.imageCompressor.ImageCompressor
import com.rut.transportqr.model.PostComplaintModel
import java.io.File

class ComplaintFragment : Fragment() {
    private val viewModel: ComplaintViewModel by activityViewModels()
    private lateinit var binding: FragmentComplaintBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sendButton = view.findViewById<Button>(R.id.sendButton)
        val imgName = view.findViewById<TextView>(R.id.imgNameTextView)
        val complaint = viewModel.complaintModel.value
        val controller = findNavController()
        if (complaint != null) {
            imgName.text = complaint.getPhotoFilePath()?.let { it1 -> File(it1).name }
        }
        val transportCodeView = view.findViewById<MaterialTextView>(R.id.transportCodeTextView)
        if (complaint != null) {
            transportCodeView.text = "Код транспортного средства: " + complaint.getTransportCode()
        }
        sendButton.setOnClickListener {
            if (complaint != null) {
                complaint.setHeader(binding.titleComplaintField.text.toString())
                complaint.setText(binding.messageText.text.toString())
                complaint.setSenderEmail(binding.emailComplaintField.text.toString())
                if (complaint.getSenderEmail()
                        ?.let { it1 -> Patterns.EMAIL_ADDRESS.matcher(it1).matches() } == true
                ) {
                    complaint.getHeader()?.let { it1 ->
                        viewModel.setComplaint(complaint)
                        binding.sendButton.visibility = View.GONE
                        viewModel.sendComplaint()
                        viewModel.myResponse.observe(viewLifecycleOwner, Observer { response ->
                            if (response.isSuccessful) {
                                val complaintResponse = response.body()
                                if (complaintResponse != null) {
                                    Log.d("Response", complaintResponse.toString())
                                    controller.navigate(R.id.action_complaintFragment_to_menuFragment)
                                    Toast.makeText(
                                        requireContext(),
                                        "Обращение успешно отправлено!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Log.d("Response", response.errorBody().toString())
                                binding.sendButton.visibility = View.VISIBLE
                            }
                        })

                    }
                } else {
                    Toast.makeText(requireContext(), "Incorrect Email", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
