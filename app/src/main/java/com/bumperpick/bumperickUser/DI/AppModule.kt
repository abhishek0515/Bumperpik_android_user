package com.bumperpick.bumperickUser.DI

// AppModule.kt
import DataStoreManager
import com.bumperpick.bumperickUser.Repository.AuthRepository
import com.bumperpick.bumperickUser.Repository.AuthRepositoryImpl
import com.bumperpick.bumperickUser.Repository.GoogleSignInRepository
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.OfferRepositoryImpl
import com.bumperpick.bumperickUser.Screens.Home.AccountViewmodel
import com.bumperpick.bumperickUser.Screens.Home.CategoryViewModel
import com.bumperpick.bumperickUser.Screens.Home.HomePageViewmodel
import com.bumperpick.bumperickUser.Screens.Login.GoogleSignInViewModel
import com.bumperpick.bumperickUser.Screens.Login.LoginViewmodel
import com.bumperpick.bumperickUser.Screens.OTP.OtpViewModel
import com.bumperpick.bumperickUser.Screens.Splash.SplashViewmodel
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("http://13.50.109.14/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }
    // DataStoreManager Singleton
    single { DataStoreManager(get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(),get()) }
    single { GoogleSignInRepository(get(),get()) }
    single <OfferRepository>{ OfferRepositoryImpl(get(),get()) }
    // ViewModel
    viewModel { SplashViewmodel(get()) }
    viewModel { LoginViewmodel(get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { GoogleSignInViewModel(get()) }
    viewModel { HomePageViewmodel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { AccountViewmodel(get(),get()) }


}
