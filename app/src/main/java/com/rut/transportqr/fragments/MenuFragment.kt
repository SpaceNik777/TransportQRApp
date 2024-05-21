package com.rut.transportqr.fragments

import com.rut.transportqr.viewModel.ComplaintViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.rut.transportqr.R
import com.rut.transportqr.barcodeScannerOptions.ScanOptionsHelper
import com.rut.transportqr.model.ComplaintModel

class MenuFragment : Fragment() {
    private val viewModel: ComplaintViewModel by activityViewModels()
    private lateinit var codeTRTextView: TextView
    private val scanOptionsHelper = ScanOptionsHelper()
    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            val complaint = viewModel.complaintModel.value
            Toast.makeText(requireContext(), "Scanned: ${result.contents}", Toast.LENGTH_LONG)
                .show()
            if (complaint != null) {
                complaint.setTransportCode(result.contents)
                viewModel.setComplaint(complaint)
            } else {
                val complaintModel =
                    ComplaintModel(result.contents, null, null, null, null, null, null, null)
                viewModel.setComplaint(complaintModel)
            }
            updateUI()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeTRTextView = view.findViewById(R.id.transporCodeTextView)
        val scanQRButton = view.findViewById<Button>(R.id.toScanQRButton)
        val toPhotoButton = view.findViewById<Button>(R.id.toPhotoFragButton)
        val toEnterTRCodeFragmentButton = view.findViewById<Button>(R.id.toEnterTrCodeButton)
        val toComplaintFragmentButton = view.findViewById<Button>(R.id.toComplaintFragButton)
        val controller = findNavController()
        updateUI()
        scanQRButton.setOnClickListener {
            val options = scanOptionsHelper.createOptions()
            barcodeLauncher.launch(options)
        }
        toEnterTRCodeFragmentButton.setOnClickListener {
            controller.navigate(R.id.action_menuFragment_to_enterTransportCodeFragment)
        }
        toPhotoButton.setOnClickListener {
            controller.navigate(R.id.action_menuFragment_to_photoFragment)
        }
        toComplaintFragmentButton.setOnClickListener {
            val complaint = viewModel.complaintModel.value
            if (complaint != null) {
                if (complaint.getTransportCode() != null && complaint.getTransportCode() != "00000") {
                    if (complaint.getPhotoFilePath() != null) {
                        controller.navigate(R.id.action_menuFragment_to_complaintFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Вы не сделали фото происшествия!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Вы не ввели транспортный номер!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Вы не сфотографировали причину обращения и не ввели номер транспортного средства!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUI() {
        viewModel.complaintModel.observe(viewLifecycleOwner) { complaint ->
            complaint?.let {
                codeTRTextView.text = it.getTransportCode()
            }
        }
    }
}
