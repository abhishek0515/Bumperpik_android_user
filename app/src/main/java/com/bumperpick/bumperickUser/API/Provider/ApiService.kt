package com.bumperpick.bumperpickvendor.API.Provider

import com.bumperpick.bumperickUser.API.New_model.CustomerOffer
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.LoginModel
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.deletemodel
import com.bumperpick.bumperickUser.API.New_model.profile_model
import com.bumperpick.bumperpickvendor.API.Model.Category_Model
import com.bumperpick.bumperpickvendor.API.Model.Subscription
import com.bumperpick.bumperpickvendor.API.Model.Vendor_Register_Model
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.bumperpick.bumperpickvendor.API.Model.vendor_register_confirm
import com.bumperpick.bumperpickvendor.API.Model.vendor_subscription
import com.bumperpick.bumperpickvendor.API.Model.verify_otp
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("api/customer/send-otp")
    suspend  fun cust_send_otp(@Field("phone_number") mobile_number: String): Response<success_model>

    @FormUrlEncoded
    @POST("api/customer/resend-otp")
    suspend fun cust_re_send_otp(@Field("phone_number") mobile_number: String): Response<success_model>

    @FormUrlEncoded
    @POST("api/customer/verify-otp")
    suspend fun cust_verify_otp(@Field("phone_number") mobile_number: String,@Field("otp") otp: String): Response<verify_otp>

    @Multipart
   @POST("api/vendor/register")
   suspend fun register_vendor(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part gst_certificate: MultipartBody.Part
   ):Response<Vendor_Register_Model>


   @GET("api/categories")
   suspend fun getCategory():Response<Category_Model>

   @GET("api/subscriptions")
   suspend fun getSubcsription():Response<Subscription>

   @FormUrlEncoded
   @POST("api/vendor/select-subscription")
   suspend fun select_subscription(@Field("subscription_id")subscription_id:String,@Field("token")token:String):Response<vendor_subscription>

   @FormUrlEncoded
   @POST("api/vendor/registration-confirm")
   suspend fun registration_confirm(@FieldMap map: Map<String, String>):Response<vendor_register_confirm>
   @FormUrlEncoded
   @POST("api/customer/auth-google")
   suspend fun auth_google(@Field("email") email:String):Response<LoginModel>

   @FormUrlEncoded
   @POST("api/customer/offers")
   suspend fun customer_offer(@Field("token")token: String,@Field("sub_category_id")sub_cat_id:String="",@Field("category_id")cat_id:String=""):Response<CustomerOffer>

   @GET("api/customer/offers-details/{id}")
   suspend fun offer_details(@Path("id")id:String,@Query("token")token: String):Response<CustomerOfferDetail>
    @FormUrlEncoded
   @POST("api/customer/cart-offers/create")
   suspend fun cart_add(@Field("token")token: String,@Field("offer_id")offer_id:String):Response<CustomerOfferDetail>

   @GET("api/customer/cart-offers")
   suspend fun cart_data(@Query("token")token: String):Response<cartDetails>

   @GET("api/customer/cart-offers/delete/{id}")
   suspend fun deleteCart(@Path("id")id:String,@Query("token")token: String):Response<deletemodel>


   @GET("api/customer/profile")
   suspend fun getProfile(@Query("token")token: String):Response<profile_model>
   @Multipart
   @POST("api/customer/profile/update")
   suspend fun update_profile(  @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
                                @Part image: MultipartBody.Part?,):Response<profile_model>
}