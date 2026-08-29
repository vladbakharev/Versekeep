package com.vladbakharev.versekeep.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoemDatabaseHelper
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE poems (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    year INTEGER,
                    content TEXT NOT NULL,
                    favorite INTEGER NOT NULL DEFAULT 0,
                    favorited_at INTEGER,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
                """.trimIndent(),
            )
        }

        override fun onUpgrade(
            db: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int,
        ) {
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE poems ADD COLUMN favorited_at INTEGER")
                db.execSQL("UPDATE poems SET favorited_at = updated_at WHERE favorite = 1")
            }
        }

        private companion object {
            const val DATABASE_NAME = "versekeep.db"
            const val DATABASE_VERSION = 2
        }
    }
