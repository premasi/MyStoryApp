package rk.enkidu.mystoryapp.data.response

import com.google.gson.annotations.SerializedName

data class GetStoryWithLocationResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListMapItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListMapItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)
