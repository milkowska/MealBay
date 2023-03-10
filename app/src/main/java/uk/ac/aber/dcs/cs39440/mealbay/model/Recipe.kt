package uk.ac.aber.dcs.cs39440.mealbay.model

data class Recipe(
    val category: String = "",
    val difficulty: String = "",
    val ingredients: Array<String>,
    val isVegan: Boolean,
    val isVegetarian: Boolean,
    val photo: String = "",
    val preparation: Array<String>,
    val rating: String = "",
    val title: String = "",
    val total_time: String = "",
)
