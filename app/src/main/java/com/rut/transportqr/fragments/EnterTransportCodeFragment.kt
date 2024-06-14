package com.rut.transportqr.fragments

import com.rut.transportqr.viewModel.ComplaintViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.rut.transportqr.R
import com.rut.transportqr.model.ComplaintModel

class EnterTransportCodeFragment : Fragment() {
    private val viewModel: ComplaintViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_enter_transport_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val enterButton = view.findViewById<Button>(R.id.toMenuButton)
        val codeTextView = view.findViewById<TextInputEditText>(R.id.transportCodeField)
        val controller = findNavController()
        val complaint = viewModel.complaintModel.value
        enterButton.setOnClickListener {
            val code = codeTextView.text.toString()
            if (code.isNotEmpty() && code.all { it.isDigit() }) {
                if (complaint != null) {
                    complaint.setTransportCode(code)
                    viewModel.setComplaint(complaint)
                    controller.navigate(R.id.action_enterTransportCodeFragment_to_menuFragment)
                } else {
                    val complaintModel = ComplaintModel(
                        codeTextView.text.toString(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                    viewModel.setComplaint(complaintModel)
                    controller.navigate(R.id.action_enterTransportCodeFragment_to_menuFragment)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Заполните поле транспортного кода, в нем должны быть только цифры!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
