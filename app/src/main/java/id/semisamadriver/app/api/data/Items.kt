package id.semisamadriver.app.api.data

import id.semisamadriver.app.utilily.formatToRupiah

data class Boarding(
    var title: String,
    var desc: String,
    var image: Int
)

data class Banner(
    var image: String
)

data class Category(
    var name: String,
    var id: String,
    var image: String
)

data class Product(
    var image: String?,
    var name: String?,
    var description: String?,
    var nutritionBenefit: String?,
    var storageMethod: String?,
    var stockQuantity: Int?,
    var price: Long?,
    var unit: Unit?,
    var categories: Category?,
    var unitSellQuantity: Int?,
    var id: String?
){
    fun getPriceFormat(): String{
        return price?.formatToRupiah()!!
    }

    fun getUnitDescription(): String{
        return "per $unitSellQuantity ${unit?.name}"
    }
}

data class Unit(
    var name: String,
    var id: String
)

data class ProfileMenu(
    var title: String,
    var isRequired: Boolean,
    var icon: Int
)
