package uk.ac.tees.mad.d3574618.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.d3574618.data.ReusableItemExchangeRepository
import uk.ac.tees.mad.d3574618.data.ReusableItemExchangeRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): ReusableItemExchangeRepository =
        ReusableItemExchangeRepositoryImpl(firebaseAuth, firebaseFirestore)


}