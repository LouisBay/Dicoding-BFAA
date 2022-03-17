package com.louis.mynotesapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.louis.mynotesapp.db.DatabaseContract.NoteColums

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${NoteColums.TABLE_NAME}")
        onCreate(db)
    }

    companion object {

        private const val DATABASE_NAME = "dbnoteapp"

        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE ${NoteColums.TABLE_NAME}" +
                " (${NoteColums._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${NoteColums.TITLE} TEXT NOT NULL," +
                " ${NoteColums.DESCRIPTION} TEXT NOT NULL," +
                " ${NoteColums.DATE} TEXT NOT NULL)"
    }
}