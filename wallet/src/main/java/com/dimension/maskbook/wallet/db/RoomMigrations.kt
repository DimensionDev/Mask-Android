package com.dimension.maskbook.wallet.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object RoomMigrations {
    val MIGRATION_6_7 get() = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE DbSendHistory ADD COLUMN `name` TEXT DEFAULT '' NOT NULL")
        }
    }
}
