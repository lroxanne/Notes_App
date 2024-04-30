package com.example.thenotesapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.thenotesapp.R
import com.example.thenotesapp.adapter.NoteAdapter
import com.example.thenotesapp.databinding.FragmentDateNoteBinding
import com.example.thenotesapp.utils.DateUtil
import com.example.thenotesapp.viewmodel.DateNoteViewModel
import java.util.Calendar

class DateNoteFragment : Fragment(), MenuProvider, DatePickerDialog.OnDateSetListener {

    private val mBinding: FragmentDateNoteBinding by lazy { FragmentDateNoteBinding.inflate(layoutInflater) }

    private val viewModel: DateNoteViewModel by viewModels()
    private val noteAdapter by lazy { NoteAdapter(true) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvContent.adapter = noteAdapter

            val appCompatActivity = activity
            if (appCompatActivity is AppCompatActivity) {
                appCompatActivity.supportActionBar?.setTitle(R.string.date_note)
                appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

            addNoteFab.setOnClickListener {
                root.findNavController().navigate(R.id.action_dateNoteFragment_to_addNoteFragment)
            }

            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(this@DateNoteFragment, viewLifecycleOwner, Lifecycle.State.RESUMED)
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            updatePage(year, month, day)
        }


    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_date_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.today -> {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                updatePage(year, month, day)
                true
            }

            R.id.switch_date -> {
                showDateDialog()
                true
            }

            android.R.id.home -> {
                mBinding.root.findNavController().navigate(R.id.action_dateNoteFragment_to_homeFragment)
                true
            }

            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, dayOfMonth)
        datePickerDialog.setTitle(R.string.hint_date)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        updatePage(year, month, dayOfMonth)
    }

    private fun updatePage(year: Int, month: Int, day: Int) {
        mBinding.apply {
            tvDay.text = "${day}"
            tvMonth.setText(DateUtil.getMonth(month))
            tvWeek.setText(DateUtil.getWeek(year, month, day))
            viewModel.queryCurrentDateNote(year, month, day) {
                noteAdapter.differ.submitList(it)
            }
        }

    }
}