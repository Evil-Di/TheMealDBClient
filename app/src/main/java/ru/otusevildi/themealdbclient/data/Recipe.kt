package ru.otusevildi.themealdbclient.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class Recipe(
    @SerialName("idMeal") val id: String?,
    @SerialName("strMeal") val name: String?,
    @SerialName("strMealAlternate") val altName: String?,
    @SerialName("strCategory") val category: String?,
    @SerialName("strArea") val area: String?,
    @SerialName("strInstructions") val description: String?,
    @SerialName("strMealThumb") val tLink: String?,
    @SerialName("strTags") val tags: String?,
    @SerialName("strYoutube") val vLink: String?,
    @SerialName("strIngredient1") val i1: String?,
    @SerialName("strIngredient2") val i2: String?,
    @SerialName("strIngredient3") val i3: String?,
    @SerialName("strIngredient4") val i4: String?,
    @SerialName("strIngredient5") val i5: String?,
    @SerialName("strIngredient6") val i6: String?,
    @SerialName("strIngredient7") val i7: String?,
    @SerialName("strIngredient8") val i8: String?,
    @SerialName("strIngredient9") val i9: String?,
    @SerialName("strIngredient10") val i10: String?,
    @SerialName("strIngredient11") val i11: String?,
    @SerialName("strIngredient12") val i12: String?,
    @SerialName("strIngredient13") val i13: String?,
    @SerialName("strIngredient14") val i14: String?,
    @SerialName("strIngredient15") val i15: String?,
    @SerialName("strIngredient16") val i16: String?,
    @SerialName("strIngredient17") val i17: String?,
    @SerialName("strIngredient18") val i18: String?,
    @SerialName("strIngredient19") val i19: String?,
    @SerialName("strIngredient20") val i20: String?,
    @SerialName("strMeasure1") val m1: String?,
    @SerialName("strMeasure2") val m2: String?,
    @SerialName("strMeasure3") val m3: String?,
    @SerialName("strMeasure4") val m4: String?,
    @SerialName("strMeasure5") val m5: String?,
    @SerialName("strMeasure6") val m6: String?,
    @SerialName("strMeasure7") val m7: String?,
    @SerialName("strMeasure8") val m8: String?,
    @SerialName("strMeasure9") val m9: String?,
    @SerialName("strMeasure10") val m10: String?,
    @SerialName("strMeasure11") val m11: String?,
    @SerialName("strMeasure12") val m12: String?,
    @SerialName("strMeasure13") val m13: String?,
    @SerialName("strMeasure14") val m14: String?,
    @SerialName("strMeasure15") val m15: String?,
    @SerialName("strMeasure16") val m16: String?,
    @SerialName("strMeasure17") val m17: String?,
    @SerialName("strMeasure18") val m18: String?,
    @SerialName("strMeasure19") val m19: String?,
    @SerialName("strMeasure20") val m20: String?,
    @SerialName("strSource") val source: String?,
    @SerialName("strImageSource") val imageSource: String?,
    @SerialName("strCreativeCommonsConfirmed") val confirmed: String?,
    @SerialName("dateModified") val modified: String?
)

@Serializable data class Recipes(@SerialName("meals") val list: List<Recipe>?)


@Serializable data class RecipeShort(
    @SerialName("idMeal") val id: String?,
    @SerialName("strMeal") val name: String?,
    @SerialName("strMealThumb") val tLink: String?
)

@Serializable data class RecipesShort(@SerialName("meals") val list: List<RecipeShort>?)