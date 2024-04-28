package uk.ac.tees.mad.d3574618.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.d3574618.data.database.ItemDatabase
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepository
import uk.ac.tees.mad.d3574618.data.repository.FirestoreRepositoryImpl
import uk.ac.tees.mad.d3574618.data.repository.ReusableItemExchangeRepository
import uk.ac.tees.mad.d3574618.data.repository.ReusableItemExchangeRepositoryImpl
import uk.ac.tees.mad.d3574618.data.repository.DatabaseRepository
import uk.ac.tees.mad.d3574618.data.repository.DatabaseRepositoryImpl
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
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun providesRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        firebaseStorage: FirebaseStorage
    ): ReusableItemExchangeRepository =
        ReusableItemExchangeRepositoryImpl(firebaseAuth, firebaseFirestore, firebaseStorage)


    @Provides
    @Singleton
    fun providesFirestoreRepo(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        firebaseStorage: FirebaseStorage
    ): FirestoreRepository =
        FirestoreRepositoryImpl(
            firestore = firestore,
            firebaseAuth = firebaseAuth,
            firebaseStorage = firebaseStorage
        )

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app,
            ItemDatabase::class.java,
            "reusable_item_db"
        ).build()

    @Provides
    @Singleton
    fun providesDatabaseRepo(
        db: ItemDatabase
    ): DatabaseRepository =
        DatabaseRepositoryImpl(db.getDao())

}