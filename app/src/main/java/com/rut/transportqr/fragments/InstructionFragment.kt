package com.rut.transportqr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rut.transportqr.R

class InstructionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_instruction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toMenuButton = view.findViewById<Button>(R.id.toMenuButton)
        val controller = findNavController()
        (activity as AppCompatActivity).supportActionBar?.title = "Меню"
        toMenuButton.setOnClickListener {
            controller.navigate(R.id.action_instructionFragment_to_menuFragment)
        }
    }
}
