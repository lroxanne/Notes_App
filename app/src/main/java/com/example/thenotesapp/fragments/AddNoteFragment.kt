package com.example.thenotesapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.thenotesapp.API.ApiService
import com.example.thenotesapp.API.GrammarResponse

import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.FragmentAddNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel
import com.example.thenotesapp.viewmodel.PermissionViewModel



class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding: FragmentAddNoteBinding? = null
    private val binding get() = addNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView: View
    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private var openImageLauncher: ActivityResultLauncher<Intent>? = null
    private var shouldGoToStorageSettings = false
    private var selectedImageUri: Uri? = null

    private val imageLoaderOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching to prevent loading old images
        .skipMemoryCache(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        addNoteView = view
        binding.addImgBtn.setOnClickListener {
            if (permissionViewModel.checkImagePermission()) {
                try {
                    openImageLauncher?.launch(Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                permissionViewModel.askForImagePermission(this)
            }
        }

        openImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    imageUri?.let {
                        selectedImageUri = it // Store the selected image URI
                        Glide.with(this)
                            .load(imageUri)
                            .apply(imageLoaderOptions)
                            .into(binding.editNoteImg)
                        binding.editNoteImg.visibility = View.VISIBLE
                    }
                }
            }

        binding.addFontBtn.setOnClickListener {
            showFontSelectionDialog(requireContext(),binding.addNoteTitle,binding.addNoteHeading)
        }
    }


    fun showFontSelectionDialog(context: Context, title: TextView, heading:TextView) {
        val fontOptions = arrayOf("Jersey", "Roboto")
        val fontFamilies = arrayOf("jersey.ttf", "roboto.ttf")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Font")

        builder.setItems(fontOptions) { _, which ->
            try {
                val fontPath = "fonts/${fontFamilies.getOrElse(which) { "" }}"
                val typeface = Typeface.createFromAsset(context.assets, fontPath)
                title.typeface = typeface
                heading.typeface = typeface
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        builder.show()
    }

    private fun saveNote(view: View) {
        val noteTitle = binding.addNoteTitle.text.toString().trim()
        val noteDesc = binding.addNoteDesc.text.toString().trim()

        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteDesc, false, selectedImageUri?.toString())
            notesViewModel.addNote(note)

            Toast.makeText(addNoteView.context, "Note Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(addNoteView.context, "Please enter note title", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.saveMenu -> {
                checkGrammarAndSaveNote()

                true
            }

            else -> false
        }
    }
    private fun checkGrammarAndSaveNote() {
        val noteDesc = binding.addNoteDesc.text.toString().trim()
        ApiService.grammarApi.checkGrammar(noteDesc, "en").enqueue(object : Callback<GrammarResponse> {
            override fun onResponse(call: Call<GrammarResponse>, response: Response<GrammarResponse>) {
                if (response.isSuccessful) {
                    val grammarErrors = StringBuilder()
                    response.body()?.matches?.forEach { match ->
                        val error = match.message
                        val suggestions = match.replacements.joinToString { it.value }
                        grammarErrors.append("Error: $error\nSuggestions: $suggestions\n\n")
                    }

                    if (grammarErrors.isNotEmpty()) {
                        Toast.makeText(context, grammarErrors.toString(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "No grammar errors found.", Toast.LENGTH_SHORT).show()
                    }

                    saveNote()
                } else{
                    Toast.makeText(context, "Error checking grammar: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }                }
            override fun onFailure(call: Call<GrammarResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to check grammar: ${t.message}", Toast.LENGTH_SHORT).show()
                saveNote()
            }
        })
    }

    private fun saveNote() {
        val noteTitle = binding.addNoteTitle.text.toString().trim()
        val noteDesc = binding.addNoteDesc.text.toString().trim()
        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteDesc, false, selectedImageUri?.toString())
            notesViewModel.addNote(note)
            Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
            view?.findNavController()?.popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionViewModel.STORAGE_PERM_REQ_CODE_ON_13, PermissionViewModel.STORAGE_PERM_REQ_CODE_BELOW_13 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        openImageLauncher?.launch(
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    if (shouldGoToStorageSettings) {
                        try {
                            startActivity(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", requireActivity().packageName, null)
                            })
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    shouldGoToStorageSettings = true
                }
            }
        }
    }
}