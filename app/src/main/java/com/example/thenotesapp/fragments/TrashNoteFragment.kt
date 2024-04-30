package com.example.thenotesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thenotesapp.MainActivity
import com.example.thenotesapp.R
import com.example.thenotesapp.adapter.NoteAdapter
import com.example.thenotesapp.databinding.FragmentTrashNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodel.NoteViewModel

class TrashNoteFragment : Fragment(), MenuProvider {

    private val mBinding by lazy { FragmentTrashNoteBinding.inflate(layoutInflater) }

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appCompatActivity = activity
        if (appCompatActivity is AppCompatActivity) {
            appCompatActivity.supportActionBar?.setTitle(R.string.trash_note)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        setupHomeRecyclerView()
    }

    private fun setupHomeRecyclerView() {
        noteAdapter = NoteAdapter()
        mBinding.trashRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            notesViewModel.getDeleteNotes().observe(viewLifecycleOwner) { note ->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }

    private fun updateUI(note: List<Note>?) {
        if (note != null) {
            if (note.isNotEmpty()) {
                mBinding.emptyNotesImage.visibility = View.GONE
                mBinding.trashRecyclerView.visibility = View.VISIBLE
            } else {
                mBinding.emptyNotesImage.visibility = View.VISIBLE
                mBinding.trashRecyclerView.visibility = View.GONE
            }
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.trash_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.notes -> {
                Navigation.findNavController(mBinding.root).navigateUp()
                true
            }

            android.R.id.home -> {
                mBinding.root.findNavController().popBackStack()
                true
            }

            else -> false
        }
    }

}