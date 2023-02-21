package uk.ac.aber.dcs.cs39440.mealbay.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListDao
import uk.ac.aber.dcs.cs39440.mealbay.model.ShoppingListItem

/**
 * Implementation of the Room Database
 */
@Database(entities = [ShoppingListItem::class], version = 1)
abstract class MealBayRoomDatabase : RoomDatabase() {

        abstract fun shoppingListDao(): ShoppingListDao

        companion object {
            private var instance: MealBayRoomDatabase? = null

            @Synchronized
            fun getDatabase(context: Context): MealBayRoomDatabase? {
                if (instance == null) {
                    instance =
                        Room.databaseBuilder<MealBayRoomDatabase>(
                            context.applicationContext,
                            MealBayRoomDatabase::class.java,
                            "MealBay_database"
                        )
                            .build()
                }
                return instance
            }
        }
    }
