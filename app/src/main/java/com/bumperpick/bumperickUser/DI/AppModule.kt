package com.bumperpick.bumperickUser.DI

// AppModule.kt
import DataStoreManager
import com.bumperpick.bumperickUser.Repository.AuthRepository
import com.bumperpick.bumperickUser.Repository.AuthRepositoryImpl
import com.bumperpick.bumperickUser.Screens.Login.LoginViewmodel
import com.bumperpick.bumperickUser.Screens.OTP.OtpViewModel
import com.bumperpick.bumperickUser.Screens.Splash.SplashViewmodel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    // DataStoreManager Singleton
    single { DataStoreManager(get()) }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // ViewModel
    viewModel { SplashViewmodel(get()) }
    viewModel { LoginViewmodel(get()) }
    viewModel { OtpViewModel(get()) }
}
