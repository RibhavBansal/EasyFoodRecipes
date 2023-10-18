package com.example.easyfoodrecipes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.easyfoodrecipes.pojo.Meal

@Database(entities = [Meal::class], version = 1)
@TypeConverters(MealTypeConverter::class)
abstract class MealDatabase: RoomDatabase(){
    abstract fun mealDao(): MealDao

    companion object{
        @Volatile
        var instance: MealDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MealDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(
                    context,
                    MealDatabase::class.java,
                    "meal.db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance as MealDatabase
        }
    }
}