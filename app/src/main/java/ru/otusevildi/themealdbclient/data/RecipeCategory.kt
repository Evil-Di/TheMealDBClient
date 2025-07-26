package ru.otusevildi.themealdbclient.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class RecipeCategory(
    @SerialName("idCategory") val id: String?,
    @SerialName("strCategory") val name: String?,
    @SerialName("strCategoryThumb") val tLink: String?,
    @SerialName("strCategoryDescription") val description: String?
)

@Serializable data class RecipeCategories(@SerialName("categories") val list: List<RecipeCategory>?)
