package com.bumperpick.bumperickUser.DI

// AppModule.kt
import DataStoreManager
import com.bumperpick.bumperickUser.Repository.AuthRepository
import com.bumperpick.bumperickUser.Repository.AuthRepositoryImpl
import com.bumperpick.bumperickUser.Repository.EventRepositoryImpl
import com.bumperpick.bumperickUser.Repository.Event_campaign_Repository
import com.bumperpick.bumperickUser.Repository.GoogleSignInRepository
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.OfferRepositoryImpl
import com.bumperpick.bumperickUser.Repository.SupportRepository
import com.bumperpick.bumperickUser.Repository.SupportRepositoryImpl
import com.bumperpick.bumperickUser.Screens.Campaign.EventScreenViewmodel
import com.bumperpick.bumperickUser.Screens.Event.EventViewmodel
import com.bumperpick.bumperickUser.Screens.Faq.FaqViewmodel
import com.bumperpick.bumperickUser.Screens.Home.AccountViewmodel
import com.bumperpick.bumperickUser.Screens.Home.CategoryViewModel
import com.bumperpick.bumperickUser.Screens.Home.HomePageViewmodel
import com.bumperpick.bumperickUser.Screens.Home.homeScreenViewmodel
import com.bumperpick.bumperickUser.Screens.Login.GoogleSignInViewModel
import com.bumperpick.bumperickUser.Screens.Login.LoginViewmodel
import com.bumperpick.bumperickUser.Screens.NotificationScreen.NotificationViewmodel
import com.bumperpick.bumperickUser.Screens.OTP.OtpViewModel
import com.bumperpick.bumperickUser.Screens.Splash.SplashViewmodel
import com.bumperpick.bumperickUser.Screens.Support.SupportViewModel
import com.bumperpick.bumperickUser.data.LocationViewModel

import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("http://13.200.242.189/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ApiService::class.java) }
    // DataStoreManager Singleton
    single { DataStoreManager(get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(),get(),get()) }
    single { GoogleSignInRepository(get(),get(),get()) }
    single <OfferRepository>{ OfferRepositoryImpl(get(),get(),get()) }
    single <Event_campaign_Repository>{ EventRepositoryImpl(get(),get(),get()) }
    single<SupportRepository> { SupportRepositoryImpl(dataStoreManager = get(), context = get(), apiService = get()) }
    // ViewModel
    viewModel { SplashViewmodel(get()) }
    viewModel { LoginViewmodel(get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { GoogleSignInViewModel(get()) }
    viewModel { HomePageViewmodel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { AccountViewmodel(get(),get()) }
    viewModel { EventScreenViewmodel(get()) }
    viewModel { EventViewmodel(get()) }
    viewModel { FaqViewmodel(get()) }
    viewModel { SupportViewModel(get()) }
    viewModel { homeScreenViewmodel() }
    viewModel { NotificationViewmodel(get()) }
    viewModel { LocationViewModel(get ()) }





}
