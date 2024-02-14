package com.niyaj.model

data class AddOnItem(
    val addOnItemId: String = "",

    val itemName: String = "",

    val itemPrice: Int = 0,

    val isApplicable: Boolean = true,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)

fun List<AddOnItem>.searchAddOnItem(searchText: String): List<AddOnItem> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemName.contains(searchText, true) ||
                    it.itemPrice.toString().contains(searchText, true) ||
                    it.createdAt.contains(searchText, true) ||
                    it.updatedAt?.contains(searchText, true) == true
        }
    }else this
}