package uk.ac.aber.dcs.cs39440.mealbay.model
data class Recipe(
    val category: String? = "",
    val difficulty: String? = "",
    val ingredients: List<String>,
    val isVegan: Boolean = false,
    val isVegetarian: Boolean = false,
    val photo: String? = "",
    val preparation: List<String>,
    val rating: String? = "",
    val title: String? = "",
    val total_time: String? = "",
) {
    // No-argument constructor
    constructor() : this("", "", emptyList(), false, false, "", emptyList(), "", "", "")
}
