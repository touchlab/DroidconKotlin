package co.touchlab.sessionize.platform

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import co.touchlab.droidcon.db.QueryWrapper
import com.squareup.sqldelight.android.AndroidSqlDatabase

class DroidconOpenHelper: SupportSQLiteOpenHelper.Callback(QueryWrapper.Schema.version) {

    override fun onCreate(db: SupportSQLiteDatabase) {
        QueryWrapper.Schema.create(AndroidSqlDatabase(db).getConnection())
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onUpgrade(
            db: SupportSQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
    ) {
        QueryWrapper.Schema.migrate(AndroidSqlDatabase(db).getConnection(), oldVersion, newVersion)
    }

}