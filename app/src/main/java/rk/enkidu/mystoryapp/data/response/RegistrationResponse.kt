package rk.enkidu.mystoryapp.data.response

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
