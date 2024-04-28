package uk.ac.tees.mad.d3574618.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.d3574618.auth.MoreDetailsUiState
import uk.ac.tees.mad.d3574618.data.domain.Resource
import java.util.UUID
import javax.inject.Inject

interface ReusableItemExchangeRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(
        email: String,
        password: String,
        username: String
    ): Flow<Resource<AuthResult>>

    fun forgotPassword(email: String): Flow<Resource<Boolean>>
    suspend fun saveUser(email: String?, username: String?, userId: String?)
    fun updateCurrentUser(
        item: MoreDetailsUiState
    ): Flow<Resource<String>>
}

class ReusableItemExchangeRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : ReusableItemExchangeRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(
        email: String,
        password: String,
        username: String
    ): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // Add user to Firestore with username
            val userId = authResult.user?.uid
            saveUser(userId = userId, email = email, username = username)
            emit(Resource.Success(authResult))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun forgotPassword(email: String): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            firebaseAuth.sendPasswordResetEmail(email).await()

            emit(Resource.Success(true))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override suspend fun saveUser(
        email: String?,
        username: String?,
        userId: String?
    ) {
        if (userId != null) {
            val userMap = hashMapOf(
                "email" to email,
                "username" to username,
            )
            firebaseFirestore.collection("users").document(userId).set(userMap).await()
        }
    }


    override fun updateCurrentUser(item: MoreDetailsUiState): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            val storageRef = firebaseStorage.reference
            val uuid = UUID.randomUUID()
            val imagesRef = storageRef.child("images/$uuid")
            val currentUserUid = firebaseAuth.currentUser?.uid

            val uploadTask =
                item.images?.let {
                    imagesRef.putBytes(it)
                }

            uploadTask?.addOnSuccessListener {
                imagesRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["images"] = uri.toString()
                        map["phone"] = item.phone
                        map["location"] = item.location
                        if (currentUserUid != null) {
                            firebaseFirestore.collection("users")
                                .document(currentUserUid)
                                .set(map, SetOptions.merge())
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        trySend(Resource.Success("Updated Successfully.."))
                                    }
                                }
                                .addOnFailureListener { e ->
                                    trySend(Resource.Error(message = e.message))
                                }
                        } else {
                            trySend(Resource.Error(message = "User not logged in"))
                        }
                    }
                    .addOnFailureListener {
                        trySend(Resource.Error(message = "Updating user failed Successfully: $it"))
                    }
            }?.addOnFailureListener {
                trySend(Resource.Error(message = "Image upload failed Successfully: $it"))
            }
            awaitClose { close() }
        }


}