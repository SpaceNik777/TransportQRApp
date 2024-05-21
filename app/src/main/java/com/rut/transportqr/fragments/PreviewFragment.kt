package com.rut.transportqr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rut.transportqr.R
import com.rut.transportqr.databinding.FragmentPreviewBinding
import com.rut.transportqr.tensorflowClassificator.ImageClassificator
import com.rut.transportqr.viewModel.ComplaintViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PreviewFragment : Fragment() {
    private lateinit var viewBinding: FragmentPreviewBinding
    private var photoFile: File = File("")
    private lateinit var labels_list: List<String>
    private val viewModel: ComplaintViewModel by activityViewModels<ComplaintViewModel>()
    private val imageClassificator = ImageClassificator()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentPreviewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = findNavController()
        //Обработка пути к файлу фотографии
        val complaint = viewModel.complaintModel.value
        val photoFilePath = complaint?.getPhotoFilePath()
        photoFile = File(photoFilePath)
        Glide.with(requireContext())
            .load(photoFile)
            .into(viewBinding.photoImageView)
        //Display the output text
        //Set up the listener for retake button
        viewBinding.retakeButton.setOnClickListener {
            controller.navigate(R.id.photoFragment)
        }
        //Set up the listener for save button
        viewBinding.saveButton.setOnClickListener {
            //Process image with TensorFlow Lite model
            viewLifecycleOwner.lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(photoFile)
                        .submit()
                        .get()
                }
                withContext(Dispatchers.Main) {
                    val outputText =
                        imageClassificator.processImageWithTensorFlow(bitmap, requireContext())
                    if (complaint != null) {
                        complaint.setImageClass(outputText[0])
                        complaint.setProbability(outputText[1] + "%")
                    }
                    if (complaint != null) {
                        viewModel.setComplaint(complaint)
                        controller.navigate(R.id.action_previewFragment_to_menuFragment)
                    }
                }
            }
        }
    }
}
