package uk.ac.aber.dcs.cs39440.mealbay.model
data class Recipe(

    val category: String? = "",
    val difficulty: String? = "",
    val ingredients: List<String>,
    val photo: String? = "",
    val preparation: List<String>,
    val rating: String? = "",
    val title: String? = "",
    val total_time: String? = "",
    var id: String? = null,
) {
    // No-argument constructor
    constructor() : this("", "", emptyList(), "", emptyList(), "", "", "", "")
}
