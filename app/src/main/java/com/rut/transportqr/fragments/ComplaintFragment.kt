package com.rut.transportqr.fragments

import com.rut.transportqr.viewModel.ComplaintViewModel
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.rut.transportqr.R
import com.rut.transportqr.databinding.FragmentComplaintBinding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sendButton = view.findViewById<Button>(R.id.sendButton)
        val imgName = view.findViewById<TextView>(R.id.imgNameTextView)
        val complaint = viewModel.complaintModel.value
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
                        viewModel.startSendingComplaint()
                        viewModel.isComplaintSent.observe(viewLifecycleOwner) { isComplaintSent ->
                            if (isComplaintSent) {
                                // Перейти на MenuFragment
                                findNavController().navigate(R.id.menuFragment)
                                // Сбросить флаг isComplaintSent
                                viewModel.setIsComplaintSent(false)
                                Toast.makeText(
                                    requireContext(),
                                    "Обращение успешно отправлено!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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
