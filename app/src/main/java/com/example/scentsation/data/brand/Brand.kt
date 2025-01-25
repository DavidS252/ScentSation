package com.example.scentsation.data.brand

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.scentsation.data.fragrance.Fragrance
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.Serializable

class FragranceConverters {

    @TypeConverter
    fun fromFragranceList(fragrances: List<Fragrance>?): String {
        val gson = Gson()
        return gson.toJson(fragrances)
    }

    @TypeConverter
    fun toFragranceList(data: String): List<Fragrance> {
        val gson = Gson()
        val listType = object : TypeToken<List<Fragrance>>() {}.type
        return gson.fromJson(data, listType)
    }
}

@Entity
data class Brand (
    @PrimaryKey var id: String = "",
    val brandName: String = "",
    val fragrances: List<String> = emptyList()
) : Serializable {

    companion object {
        const val ID_KEY = "id"
        const val BRAND_NAME_KEY = "brandName"
        const val FRAGRANCES_KEY = "fragrances"

        fun fromJSON(json: Map<String, Any>): Brand {
            val id = json[ID_KEY] as? String ?: ""
            val brandName = json[BRAND_NAME_KEY] as? String ?: ""
            val fragrances = json[FRAGRANCES_KEY] as? List<String> ?: emptyList()

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