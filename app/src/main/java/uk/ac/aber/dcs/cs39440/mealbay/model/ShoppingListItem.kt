package uk.ac.aber.dcs.cs39440.mealbay.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull


@Entity(tableName = "shopping_list")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    var id: Int = 0,
    var item: String = "",
)

