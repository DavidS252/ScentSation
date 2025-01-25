package com.example.scentsation.data.brand

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.scentsation.data.fragrance.Fragrance
import java.io.Serializable


@Entity
data class Brand (
    @PrimaryKey val id: String = "",
    val brandName: String = "",
    val fragrances: List<Fragrance> = emptyList()
) : Serializable {

    companion object {
        const val ID_KEY = "id"
        const val BRAND_NAME_KEY = "brandName"
        const val FRAGRANCES_KEY = "fragrances"

        fun fromJSON(json: Map<String, Any>): Brand {
            val id = json[ID_KEY] as? String ?: ""
            val brandName = json[BRAND_NAME_KEY] as? String ?: ""
            val fragrances = json[FRAGRANCES_KEY] as? List<Fragrance> ?: emptyList()

            val Brand = Brand(id, brandName, fragrances)
            return Brand
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                BRAND_NAME_KEY to brandName,
                FRAGRANCES_KEY to fragrances
            )
        }
}