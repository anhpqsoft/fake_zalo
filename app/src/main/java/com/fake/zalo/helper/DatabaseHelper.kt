package com.fake.zalo.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fake.zalo.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MESSAGES = "messages"
        private const val COLUMN_MESSAGES_ID = "message_id"
        private const val COLUMN_MESSAGES_SENDER_ID = "sender_id"
        private const val COLUMN_MESSAGES_CONTENT = "content"
        private const val COLUMN_MESSAGES_TYPE = "type"
        private const val COLUMN_MESSAGES_TIMESTAMP = "timestamp"

        private const val TABLE_IMAGES = "images"
        private const val COLUMN_IMAGES_ID = "id"
        private const val COLUMN_IMAGES_MSG_ID_FK = "message_id"
        private const val COLUMN_IMAGES_CONTENT = "content"
        private const val COLUMN_IMAGES_ORDERS = "orders"

        private const val CREATE_TABLE_MESSAGES = """
            CREATE TABLE $TABLE_MESSAGES (
                $COLUMN_MESSAGES_ID TEXT PRIMARY KEY,
                $COLUMN_MESSAGES_SENDER_ID TEXT,
                $COLUMN_MESSAGES_CONTENT TEXT,
                $COLUMN_MESSAGES_TYPE TEXT,
                $COLUMN_MESSAGES_TIMESTAMP TEXT
            )
        """

        private const val CREATE_TABLE_IMAGES = """
            CREATE TABLE $TABLE_IMAGES (
                $COLUMN_IMAGES_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_IMAGES_MSG_ID_FK TEXT,
                $COLUMN_IMAGES_CONTENT TEXT,
                $COLUMN_IMAGES_ORDERS INTEGER,
                FOREIGN KEY($COLUMN_IMAGES_MSG_ID_FK) REFERENCES $TABLE_MESSAGES($COLUMN_MESSAGES_ID) ON DELETE CASCADE
            )
        """

        private const val PRAGMA_FOREIGN_KEY = "PRAGMA foreign_keys = ON;"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(PRAGMA_FOREIGN_KEY)
        db?.execSQL(CREATE_TABLE_MESSAGES)
        db?.execSQL(CREATE_TABLE_IMAGES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $CREATE_TABLE_MESSAGES")
        db?.execSQL("DROP TABLE IF EXISTS $CREATE_TABLE_IMAGES")
        onCreate(db)
    }

    suspend fun getLimitedMessages(limit: Int): List<Message> = withContext(Dispatchers.IO) {
        val messages = mutableListOf<Message>()
        val db = this@DatabaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MESSAGES ORDER BY $COLUMN_MESSAGES_TIMESTAMP ASC LIMIT ?",
            arrayOf(limit.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val messageId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGES_ID))
                val senderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGES_SENDER_ID))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGES_CONTENT))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGES_TYPE))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGES_TIMESTAMP))

                val images = arrayListOf<String>()
                if (type != null && type == "image") {
                    val cursorImages = db.rawQuery(
                        "SELECT * FROM $TABLE_IMAGES WHERE $COLUMN_IMAGES_MSG_ID_FK = ? ORDER BY $COLUMN_IMAGES_ORDERS ASC",
                        arrayOf(messageId)
                    )
                    if (cursorImages.moveToFirst()) {
                        do {
                            images.add(cursorImages.getString(cursorImages.getColumnIndexOrThrow(COLUMN_IMAGES_CONTENT)))
                        } while (cursorImages.moveToNext())
                    }
                    cursorImages.close()
                }
                messages.add(Message(messageId, senderId, content, images.toList(), type, timestamp.toLong()))
            } while (cursor.moveToNext())
        }
        cursor.close()
        messages
    }

    suspend fun insertMessage(message: Message): Long = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MESSAGES_ID, message.messageId)
            put(COLUMN_MESSAGES_SENDER_ID, message.senderId)
            put(COLUMN_MESSAGES_CONTENT, message.content)
            put(COLUMN_MESSAGES_TYPE, message.type)
            put(COLUMN_MESSAGES_TIMESTAMP, message.timestamp)
        }
        val result = db.insert(TABLE_MESSAGES, null, values)

        if (message.type == "image") {
            var index = 0
            message.images.forEach {
                val vls = ContentValues().apply {
                    put(COLUMN_IMAGES_MSG_ID_FK, message.messageId)
                    put(COLUMN_IMAGES_CONTENT, it)
                    put(COLUMN_IMAGES_ORDERS, index)
                    index++
                }
                db.insert(TABLE_IMAGES, null, vls)
            }
        }
        result
    }
}