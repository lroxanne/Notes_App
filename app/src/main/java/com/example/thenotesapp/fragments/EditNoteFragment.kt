package com.example.thenotesapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.FragmentEditNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel
import com.example.thenotesapp.viewmodel.PermissionViewModel
import com.example.thenotesapp.viewmodel.PermissionViewModel.Companion.STORAGE_PERM_REQ_CODE_BELOW_13
import com.example.thenotesapp.viewmodel.PermissionViewModel.Companion.STORAGE_PERM_REQ_CODE_ON_13
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.drive.Drive
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    private var shouldGoToStorageSettings = false
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var driveService: com.google.api.services.drive.Drive
    private lateinit var savedGoogleSignInAccount: GoogleSignInAccount
    private lateinit var sharedPreferences: SharedPreferences
    private val permissionViewModel: PermissionViewModel by activityViewModels()
    private var openImageLauncher: ActivityResultLauncher<Intent>? = null
    private var selectedImageUri: Uri? = null

    private val imageLoaderOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching to prevent loading old images
        .skipMemoryCache(true)

    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(
            "NoteAppPrefs",
            Context.MODE_PRIVATE
        )

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.editNoteTitle.setText(currentNote.noteTitle)
        binding.editNoteDesc.setText(currentNote.noteDesc)
        Glide.with(this)
            .load(currentNote.imageUri)
            .apply(imageLoaderOptions)
            .into(binding.editNoteImg)
        binding.editNoteImg.visibility = View.VISIBLE

        binding.editNoteFab.setOnClickListener {
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            val noteDesc = binding.editNoteDesc.text.toString().trim()

            if (noteTitle.isNotEmpty()) {
                val note =
                    Note(currentNote.id, noteTitle, noteDesc, false, selectedImageUri?.toString())
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, " Please enter note title", Toast.LENGTH_SHORT).show()
            }
        }

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

        binding.addFontBtn.setOnClickListener {
            showFontSelectionDialog(
                requireContext(),
                binding.editNoteTitle,
                binding.editNoteHeading
            )
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
                    }
                }
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

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()

        if (currentNote.isDeleted) {
            menuInflater.inflate(R.menu.menu_edit_delete_note, menu)
        } else {
            menuInflater.inflate(R.menu.menu_edit_note, menu)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {

            R.id.deleteMenu -> {
                deleteNote()
                true
            }

            R.id.delete_note -> {
                thoroughDeleteNote()
                true
            }

            R.id.recover -> {
                recoverNote()
                true
            }

            R.id.uploadMenu -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // requestStoragePermissionForUpload()

                    setupGoogleSignIn()
                } else {
                    setupGoogleSignIn()
                }
                true
            }

            R.id.CalMenu -> {
                calculateAndShowWordCount()
                true
            }

            else -> false
        }
    }

    private fun calculateAndShowWordCount() {
        val noteTitle = binding.editNoteTitle.text.toString().trim()
        val noteDesc = binding.editNoteDesc.text.toString().trim()
        val totalWords =
            (noteTitle.split("\\s+".toRegex()).size + noteDesc.split("\\s+".toRegex()).size).coerceAtLeast(
                0
            )
        StyleableToast.Builder(binding.root.context).textSize(16f)
            .text("Total word count: $totalWords")
            .backgroundColor(ContextCompat.getColor(binding.root.context, R.color.red))
            .solidBackground()
            .textColor(Color.WHITE).cornerRadius(10).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN_UPLOAD) {
            // Handle Google sign-in result
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null && result.isSuccess) {
                val account = result.signInAccount
                if (account != null) {
                    saveGoogleSignInAccount(account)
                    initializeDriveService(account)
                    createAndUploadPdf()
                }
            } else {
                Log.e("EditNoteFragment", "Sign-in failed")
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setupGoogleSignIn()
            } else {
                // Storage permission not granted, show a message or handle accordingly
                Toast.makeText(
                    requireContext(),
                    "Storage permission not granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun thoroughDeleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.thoroughDeleteNote(currentNote)
                Toast.makeText(context, " Note Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.trashNoteFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    private fun recoverNote() {
        notesViewModel.recoverNote(currentNote)
        Toast.makeText(context, " Note Recover", Toast.LENGTH_SHORT).show()
        view?.findNavController()?.popBackStack(R.id.trashNoteFragment, false)
    }

    private fun saveGoogleSignInAccount(account: GoogleSignInAccount) {
        val editor = sharedPreferences.edit()
        val accountJson = Gson().toJson(account)
        editor.putString("GoogleSignInAccount", accountJson)
        editor.apply()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Drive.SCOPE_FILE)
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        checkSavedGoogleSignInAccount()
    }

    private fun checkSavedGoogleSignInAccount() {
        val savedAccountJson = sharedPreferences.getString("GoogleSignInAccount", null)
        savedAccountJson?.let {
            savedGoogleSignInAccount = Gson().fromJson(it, GoogleSignInAccount::class.java)
            // Initialize Drive service and proceed with upload
            initializeDriveService(savedGoogleSignInAccount)
            createAndUploadPdf()
        } ?: signIn() // If no saved account, initiate sign-in process
    }

    private fun signIn() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN_UPLOAD)
    }

    private fun initializeDriveService(account: GoogleSignInAccount) {
        val credential =
            GoogleAccountCredential.usingOAuth2(requireContext(), setOf(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = account.account

        val httpTransport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        driveService =
            com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("NoteApp")
                .build()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun createAndUploadPdf() {
        val noteTitle = binding.editNoteTitle.text.toString().trim()
        val noteDesc = binding.editNoteDesc.text.toString().trim()

        if (noteTitle.isNotEmpty()) {
            val pdfFileName =
                "${currentNote.id}_$noteTitle.pdf" // Use note ID and title for filename
            val pdfFilePath = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                pdfFileName
            )

            GlobalScope.launch(Dispatchers.IO) {
                val success = createPdf(pdfFilePath, noteTitle, noteDesc)
                if (success) {
                    uploadToDrive(pdfFilePath.absolutePath, pdfFileName)
                }
            }
        } else {
            Toast.makeText(context, "Please enter note title", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadToDrive(filePath: String, fileName: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, "uploadToDrive called", Toast.LENGTH_LONG).show()
        }
        if (filePath.isEmpty() || fileName.isEmpty()) {
            activity?.runOnUiThread {
                Toast.makeText(context, "File path or name must not be empty", Toast.LENGTH_LONG)
                    .show()
            }
            return
        }

        try {
            Log.d("UploadToDrive", "Attempting to upload file: $fileName")
            val fileContent = File(filePath)
            if (!fileContent.exists()) {
                Log.e("UploadToDrive", "File does not exist at path: $filePath")
                return
            }
            val mediaContent = ByteArrayContent("application/pdf", fileContent.readBytes())
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = fileName
                mimeType = "application/pdf"
            }

            // Ensure driveService is initialized
            if (!::driveService.isInitialized) {
                Log.e("UploadToDrive", "Drive service is not initialized.")
                return
            }

            // Create the file on Google Drive
            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()

            val fileId = file.id
            Log.d("UploadToDrive", "File uploaded successfully with ID: $fileId")

            // Display a Toast message
            activity?.runOnUiThread {
                Toast.makeText(
                    context,
                    "File uploaded successfully with ID: $fileId",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("UploadToDrive", "Error uploading file: ${e.localizedMessage}")
            e.printStackTrace()
            // Display a Toast message for the error
            activity?.runOnUiThread {
                Toast.makeText(
                    context,
                    "Error uploading file: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun createPdf(pdfFile: File, title: String, description: String): Boolean {
        return try {
            val outputStream = FileOutputStream(pdfFile)
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.A4)
            document.add(Paragraph(title))
            document.add(Paragraph(description))



            document.close()
            outputStream.close()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERM_REQ_CODE_ON_13, STORAGE_PERM_REQ_CODE_BELOW_13 -> {
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

    private fun requestStoragePermissionForUpload() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 100
        private const val RC_SIGN_IN_UPLOAD = 1002
    }
}
