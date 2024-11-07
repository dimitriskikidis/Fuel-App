package com.dimitriskikidis.admin.fuelapp.presentation.addeditbrand

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dimitriskikidis.admin.fuelapp.R
import com.dimitriskikidis.admin.fuelapp.databinding.FragmentAddEditBrandBinding
import com.dimitriskikidis.admin.fuelapp.presentation.BrandsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditBrandFragment : Fragment() {

    private val BRAND_ICON_SIZE_PX = 90

    private var _binding: FragmentAddEditBrandBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditBrandViewModel by viewModels()
    private val brandsViewModel: BrandsViewModel by hiltNavGraphViewModels(R.id.brandListFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBrandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (viewModel.currentBrand == null) {
                brandsViewModel.currentBrand?.let { brand ->
                    viewModel.onEvent(AddEditBrandEvent.OnInitBrand(brand))
                }
            }

            viewModel.brandName?.let {
                tietBrandName.setText(it)
            }

            viewModel.brandIcon?.let {
                ivBrandIcon.setImageBitmap(it)
            }

            if (viewModel.currentBrand == null) {
                (activity as AppCompatActivity).supportActionBar?.title = "Add Brand"
                btnAddSave.text = "Add"
            } else {

                (activity as AppCompatActivity).supportActionBar?.title = "Edit Brand"
                btnAddSave.text = "Save"
            }

            tietBrandName.doAfterTextChanged {
                val name = it.toString()
                viewModel.onEvent(AddEditBrandEvent.OnNameChange(name))
            }

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            val resultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    it.data?.data?.let { uri ->
                        val source =
                            ImageDecoder.createSource(requireContext().contentResolver, uri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        val width = bitmap.width
                        val height = bitmap.height
                        val croppedBitmap =
                            if (width < height) {
                                val x = 0
                                val y = (height - width) / 2
                                Bitmap.createBitmap(bitmap, x, y, width, width)
                            } else if (height < width) {
                                val x = (height - width) / 2
                                val y = 0
                                Bitmap.createBitmap(bitmap, x, y, height, height)
                            } else {
                                bitmap
                            }
                        val brandIcon = Bitmap.createScaledBitmap(
                            croppedBitmap,
                            BRAND_ICON_SIZE_PX,
                            BRAND_ICON_SIZE_PX,
                            true
                        )
                        ivBrandIcon.setImageBitmap(bitmap)
                        viewModel.onEvent(AddEditBrandEvent.OnIconChange(brandIcon))
                    }
                }

            btnSelectBrandIcon.setOnClickListener {
                resultLauncher.launch(intent)
            }

            btnAddSave.setOnClickListener {
                viewModel.onEvent(AddEditBrandEvent.OnAddSave(brandsViewModel.brandNames))
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collectLatest { state ->
                        tilBrandName.apply {
                            error = state.nameError
                            isErrorEnabled = state.nameError != null
                        }

                        tvBrandIconError.apply {
                            text = state.iconError
                            isVisible = state.iconError != null
                        }

                        groupMain.isVisible = !state.isLoading
                        progressBar.isVisible = state.isLoading
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is AddEditBrandViewModel.UiEvent.ShowMessageAndNavigateBack -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle(event.title)
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .setOnDismissListener {
                                        setFragmentResult(
                                            "AddEditBrandFragment",
                                            bundleOf()
                                        )
                                        findNavController().popBackStack()
                                    }
                                    .create()
                                    .show()
                            }
                            is AddEditBrandViewModel.UiEvent.ShowMessageAndSignOut -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setMessage(event.message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .setOnDismissListener {
                                        findNavController().navigate(
                                            R.id.action_global_signInFragment
                                        )
                                    }
                                    .create()
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}