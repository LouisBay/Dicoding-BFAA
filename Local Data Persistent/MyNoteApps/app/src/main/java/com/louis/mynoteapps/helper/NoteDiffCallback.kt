package com.louis.mynoteapps.helper

import androidx.recyclerview.widget.DiffUtil
import com.louis.mynoteapps.database.Note

class NoteDiffCallback(private val mOldNoteList: List<Note>, private val mNewNoteList: List<Note>) : DiffUtil.Callback() {

    override fun getOldListSize() = mOldNoteList.size

    override fun getNewListSize() = mNewNoteList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldNoteList[oldItemPosition].id == mNewNoteList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldNoteList[oldItemPosition]
        val newEmployee = mNewNoteList[newItemPosition]

        return oldEmployee.title == newEmployee.title && oldEmployee.description == newEmployee.description
    }


}