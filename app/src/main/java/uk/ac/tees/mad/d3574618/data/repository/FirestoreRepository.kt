package uk.ac.tees.mad.d3574618.data.repository

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.d3574618.data.domain.AddItemUiState
import uk.ac.tees.mad.d3574618.data.domain.FirestoreItemResponse
import uk.ac.tees.mad.d3574618.data.domain.ItemCategory
import uk.ac.tees.mad.d3574618.data.domain.ItemCondition
import uk.ac.tees.mad.d3574618.data.domain.Resource
import uk.ac.tees.mad.d3574618.data.domain.UserResponse
import java.util.UUID
import javax.inject.Inject

interface FirestoreRepository {
    fun getCurrentUser(): Flow<Resource<UserResponse>>
    fun submitItem(item: AddItemUiState): Flow<Resource<String>>
    fun getAllItems(): Flow<Resource<List<FirestoreItemResponse>>>
    fun getItemsByKeyList(keyList: List<String>): Flow<Resource<List<FirestoreItemResponse>>>
    suspend fun getMyListedItems(): Flow<Resource<List<FirestoreItemResponse>>>
    fun getItemById(itemId: String): Flow<Resource<FirestoreItemResponse>>
    fun deleteItem(itemId: String): Flow<Resource<String>>
    fun requestForSwap(itemId: String, swapWithItemId: String): Flow<Resource<String>>
    fun acceptSwapRequest(itemId: String, swapWithItemId: String): Flow<Resource<String>>
    fun rejectSwapRequest(itemId: String, swapWithItemId: String): Flow<Resource<String>>
}

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) : FirestoreRepository {

    override fun getCurrentUser(): Flow<Resource<UserResponse>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid != null) {
            firestore.collection("users").document(currentUserUid).get()
                .addOnSuccessListener { mySnapshot ->
                    if (mySnapshot.exists()) {
                        val data = mySnapshot.data

                        if (data != null) {
                            val userResponse = UserResponse(
                                key = currentUserUid,
                                item = UserResponse.CurrentUser(
                                    name = data["username"] as String? ?: "",
                                    email = data["email"] as String? ?: "",
                                    phone = data["phone"] as String? ?: "",
                                    profileImage = data["images"] as String? ?: "",
                                    listedItems = data["listedItems"] as List<String>?
                                        ?: emptyList(),
                                    location = data["location"] as String? ?: ""
                                )
                            )

                            trySend(Resource.Success(userResponse))
                        } else {
                            trySend(Resource.Error(message = "No data found in Database"))

                            println("No data found in Database")
                        }
                    } else {
                        trySend(Resource.Error(message = "No data found in Database"))
                        println("No data found in Database")
                    }
                }.addOnFailureListener { e ->
                    Log.d("ERRor", e.toString())
                    trySend(Resource.Error(message = e.toString()))
                }
        } else {
            trySend(Resource.Error(message = "User not signed up"))
        }
        awaitClose {
            close()
        }
    }


    override fun submitItem(item: AddItemUiState): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            val storageRef = firebaseStorage.reference
            val uuid = UUID.randomUUID()
            val imagesRef = storageRef.child("images/$uuid")

            val uploadTasks = item.images.map {
                imagesRef.putBytes(it)
            }

            Tasks.whenAllSuccess<UploadTask.TaskSnapshot>(uploadTasks).addOnSuccessListener {
                val downloadUrls = it.map { snapshot ->
                    snapshot.storage.downloadUrl
                }
                Tasks.whenAllSuccess<Uri>(downloadUrls).addOnSuccessListener { uris ->
                    val data = hashMapOf(
                        "name" to item.name,
                        "description" to item.description,
                        "keywords" to item.keywords,
                        "listedDate" to item.listedDate,
                        "image" to uris.map { uri -> uri.toString() },
                        "category" to item.category,
                        "condition" to item.condition,
                        "listedByKey" to item.listedByKey,
                        "itemSwapStatus" to "Pending"
                    )

                    firestore.collection("items")
                        .add(data)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                "Listing Success",
                                "DocumentSnapshot added with ID: ${documentReference.id}"
                            )
                            trySend(Resource.Success(documentReference.id))

                            // Add the document ID to the user's listedItems
                            val userId =
                                firebaseAuth.currentUser?.uid
                            val userDocRef = firestore.collection("users").document(userId!!)
                            firestore.runTransaction { transaction ->
                                val user = transaction.get(userDocRef)
                                val listedItems = user.get("listedItems") as MutableList<String>?
                                    ?: mutableListOf()
                                listedItems.add(documentReference.id)
                                transaction.update(userDocRef, "listedItems", listedItems)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Error Listing", "Error adding document", e)
                            trySend(Resource.Error("Error adding document"))
                        }
                }
            }
            awaitClose {
                close()
            }
        }

    override fun getAllItems(): Flow<Resource<List<FirestoreItemResponse>>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val documents = firestore.collection("items").get().await()
            val items = documents.mapNotNull { document ->
                val item = document.data
                val id = document.id
                val name = item["name"] as String
                val description = item["description"] as String
                val image = item["image"] as List<String>
                val dateListed = (item["listedDate"] as com.google.firebase.Timestamp).toDate()
                val keywords = item["keywords"] as String
                val category = ItemCategory.valueOf(item["category"] as String)
                val condition = ItemCondition.valueOf(item["condition"] as String)
                val itemSwapStatus = item["itemSwapStatus"] as String

                FirestoreItemResponse(
                    FirestoreItemResponse.FirestoreItem(
                        id = id,
                        name = name,
                        description = description,
                        image = image,
                        dateListed = dateListed,
                        keywords = keywords,
                        category = category,
                        condition = condition,
                        itemSwapStatus = itemSwapStatus
                    ),
                    id
                )
            }
            trySend(Resource.Success(items))
        } catch (e: Exception) {
            Log.w("Error Fetching", "Error fetching documents", e)
            trySend(Resource.Error("Error fetching documents"))
        }
        awaitClose {
            close()
        }
    }


    override fun getItemsByKeyList(keyList: List<String>): Flow<Resource<List<FirestoreItemResponse>>> =
        callbackFlow {
            trySend(Resource.Loading())
            try {
                val items = mutableListOf<FirestoreItemResponse>()
                for (key in keyList) {
                    val document = firestore.collection("items").document(key).get().await()
                    if (document.exists()) {
                        val item = document.data
                        val id = document.id
                        val name = item?.get("name") as String
                        val description = item["description"] as String
                        val image = item["image"] as List<String>
                        val dateListed =
                            (item["listedDate"] as com.google.firebase.Timestamp).toDate()
                        val keywords = item["keywords"] as String
                        val category = ItemCategory.valueOf(item["category"] as String)
                        val condition = ItemCondition.valueOf(item["condition"] as String)
                        val itemSwapStatus = item["itemSwapStatus"] as String

                        items.add(
                            FirestoreItemResponse(
                                FirestoreItemResponse.FirestoreItem(
                                    id = id,
                                    name = name,
                                    description = description,
                                    image = image,
                                    dateListed = dateListed,
                                    keywords = keywords,
                                    category = category,
                                    condition = condition,
                                    itemSwapStatus = itemSwapStatus
                                ),
                                id
                            )
                        )
                    }
                }
                trySend(Resource.Success(items))
            } catch (e: Exception) {
                Log.w("Error Fetching", "Error fetching documents", e)
                trySend(Resource.Error("Error fetching documents"))
            }
            awaitClose {
                close()
            }
        }

    override suspend fun getMyListedItems(): Flow<Resource<List<FirestoreItemResponse>>> =
        callbackFlow {
            trySend(Resource.Loading())
            val userId = firebaseAuth.currentUser?.uid
            val userDocRef = firestore.collection("users").document(userId!!)
            userDocRef.get().addOnSuccessListener { userSnapshot ->
                val user = userSnapshot.data
                val listedItemsByUser =
                    user?.get("listedItems") as MutableList<String>? ?: mutableListOf()

                if (listedItemsByUser.isNotEmpty()) {
                    launch {
                        val itemsList = withContext(Dispatchers.IO) {
                            listedItemsByUser.map { itemId ->
                                val itemRef = firestore.collection("items").document(itemId)
                                val itemSnapshot = Tasks.await(itemRef.get())
                                val item = itemSnapshot
                                val swapRequests =
                                    item.get("swapRequests") as MutableList<Map<String, Any>>?
                                        ?: mutableListOf()
                                val swapRequestItems = if (swapRequests.isNotEmpty()) {
                                    swapRequests.mapNotNull { swapRequest ->
                                        val swapWithItemId = swapRequest["swapRequests"] as String
                                        val status = swapRequest["status"] as String
                                        val swapItemRef =
                                            firestore.collection("items").document(swapWithItemId)
                                        val swapItemSnapshot = Tasks.await(swapItemRef.get())
                                        if (swapItemSnapshot != null) {
                                            FirestoreItemResponse.FirestoreItem(
                                                id = swapItemSnapshot.id,
                                                name = swapItemSnapshot.getString("name")!!,
                                                description = swapItemSnapshot.getString("description")!!,
                                                image = swapItemSnapshot.get("image") as List<String>,
                                                dateListed = (swapItemSnapshot.getTimestamp("listedDate")
                                                    ?.toDate())!!,
                                                keywords = swapItemSnapshot.getString("keywords")!!,
                                                category = ItemCategory.valueOf(
                                                    swapItemSnapshot.getString(
                                                        "category"
                                                    )!!
                                                ),
                                                condition = ItemCondition.valueOf(
                                                    swapItemSnapshot.getString(
                                                        "condition"
                                                    )!!
                                                ),
                                                swapStatus = status,
                                                itemSwapStatus = swapItemSnapshot.getString("itemSwapStatus")!!
                                            )
                                        } else {
                                            null
                                        }
                                    }.filterNotNull().toMutableList()
                                } else {
                                    mutableListOf()
                                }

                                FirestoreItemResponse(
                                    FirestoreItemResponse.FirestoreItem(
                                        id = itemRef.id,
                                        name = item.getString("name") ?: "",
                                        description = item.getString("description") ?: "",
                                        image = item.get("image") as List<String>,
                                        dateListed = (item.getTimestamp("listedDate")?.toDate())!!,
                                        keywords = item.getString("keywords")!!,
                                        category = ItemCategory.valueOf(item.getString("category")!!),
                                        condition = ItemCondition.valueOf(item.getString("condition")!!),
                                        swapRequests = swapRequestItems,
                                        itemSwapStatus = item.getString("itemSwapStatus")!!
                                    ),
                                    itemRef.id
                                )
                            }
                        }
                        trySend(Resource.Success(itemsList))
                    }
                } else {
                    Log.d("LISTED ITEM", listedItemsByUser.toString())
                    trySend(Resource.Success(emptyList()))
                }
            }.addOnFailureListener { e ->
                trySend(Resource.Error(message = e.message ?: "Read item failed"))
            }
            awaitClose { close() }
        }

    override fun getItemById(itemId: String): Flow<Resource<FirestoreItemResponse>> = callbackFlow {
        trySend(Resource.Loading())
        try {

            val mySnapshot = firestore.collection("items").document(itemId)
                .get()
                .await()


            if (mySnapshot.exists()) {
                val data = mySnapshot.data

                if (data != null) {
                    val listedBy = (data["listedByKey"] as String)
                    val listedByUserDoc =
                        firestore.collection("users").document(listedBy).get().await()
                    val listedByUser = if (listedByUserDoc.exists()) {
                        val user = listedByUserDoc.data
                        UserResponse.CurrentUser(
                            name = user?.get("username") as String,
                            email = user["email"] as String,
                            phone = user["phone"] as String,
                            profileImage = user["images"] as String,
                            location = user["location"] as String
                        )
                    } else null

                    val itemResponse = FirestoreItemResponse(
                        key = itemId,
                        item = FirestoreItemResponse.FirestoreItem(
                            id = itemId,
                            name = data["name"] as String,
                            description = data["description"] as String,
                            dateListed = mySnapshot.getDate("listedDate"),
                            image = data["image"] as List<String>,
                            category = ItemCategory.valueOf(data["category"] as String),
                            condition = ItemCondition.valueOf(data["condition"] as String),
                            listedBy = listedByUser,
                            keywords = data["keywords"] as String,
                            itemSwapStatus = data["itemSwapStatus"] as String
                        )
                    )

                    trySend(Resource.Success(itemResponse))
                } else {
                    println("No data found in Database")
                }
            } else {
                println("No data found in Database")
            }

        } catch (e: Exception) {
            Log.w("Error Fetching", "Error fetching documents", e)
            trySend(Resource.Error("Error fetching document"))
        }

        awaitClose {
            close()
        }
    }

    override fun deleteItem(itemId: String): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val userId = firebaseAuth.currentUser?.uid
        firestore.runTransaction { transaction ->
            val itemRef = firestore.collection("items").document(itemId)
            val userRef = firestore.collection("users").document(userId!!)
            val user = transaction.get(userRef)

            // Remove the item from the user's listedItems
            val listedItems = user.get("listedItems") as MutableList<String>?
                ?: mutableListOf()
            listedItems.remove(itemId)
            transaction.update(userRef, "listedItems", listedItems)

            // Delete the item from the items collection
            transaction.delete(itemRef)
        }.addOnSuccessListener {
            trySend(Resource.Success("Item deleted successfully..."))
        }.addOnFailureListener { e ->
            Log.w("Error deleting", "Error deleting item", e)
            trySend(Resource.Error("Error deleting item: ${e.message}"))
        }
        awaitClose { close() }
    }

    override fun requestForSwap(itemId: String, swapWithItemId: String): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            val userId = firebaseAuth.currentUser?.uid
            firestore.runTransaction { transaction ->
                val itemRef = firestore.collection("items").document(itemId)
                val userRef = firestore.collection("users").document(userId!!)
                val item = transaction.get(itemRef)
                val user = transaction.get(userRef)

                val swapRequestInItem =
                    item.get("swapRequests") as MutableList<Map<String, Any>>? ?: mutableListOf()
                val swapRequestInUser = user.get("swapRequests") as MutableList<Map<String, Any>>?
                    ?: mutableListOf()

                val swapData = hashMapOf(
                    "swapRequests" to swapWithItemId,
                    "status" to "Pending"
                )
                swapRequestInItem.add(swapData)

                val swapRequest = hashMapOf(
                    "itemId" to itemId,
                    "swapWithItemId" to swapWithItemId,
                    "status" to "Pending"
                )

                swapRequestInUser.add(swapRequest)

                transaction.update(itemRef, "swapRequests", swapRequestInItem)
                transaction.update(userRef, "swapRequests", swapRequestInUser)
            }.addOnSuccessListener {
                trySend(Resource.Success("Item swap request sent..."))
            }.addOnFailureListener { e ->
                Log.w("Error swapping", "Error Swapping item", e)
                trySend(Resource.Error("Error Swapping item: ${e.message}"))
            }
            awaitClose { close() }
        }

    override fun acceptSwapRequest(itemId: String, swapWithItemId: String): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            val userId = firebaseAuth.currentUser?.uid
            firestore.runTransaction { transaction ->
                val itemRef = firestore.collection("items").document(itemId)
                val swapWithItemRef = firestore.collection("items").document(swapWithItemId)
                val userRef = firestore.collection("users").document(userId!!)
                val item = transaction.get(itemRef)
                val user = transaction.get(userRef)

                val swapRequestInItem =
                    item.get("swapRequests") as MutableList<HashMap<String, Any>>?
                        ?: mutableListOf()

                val swapRequestInUser =
                    user.get("swapRequests") as MutableList<HashMap<String, Any>>?
                        ?: mutableListOf()

                swapRequestInItem.find { it["swapRequests"] == swapWithItemId }?.let {
                    it["status"] = "Swapped"
                }
                swapRequestInUser.find { it["itemId"] == itemId && it["swapWithItemId"] == swapWithItemId }
                    ?.put("status", "Swapped")

                transaction.update(itemRef, "swapRequests", swapRequestInItem)
                transaction.update(itemRef, "itemSwapStatus", "Swapped")
                transaction.update(swapWithItemRef, "itemSwapStatus", "Swapped")
                transaction.update(userRef, "swapRequests", swapRequestInUser)
            }.addOnSuccessListener {
                trySend(Resource.Success("Swap request accepted..."))
            }.addOnFailureListener { e ->
                Log.w("Error accepting swap", "Error accepting swap request", e)
                trySend(Resource.Error("Error accepting swap request: ${e.message}"))
            }
            awaitClose { close() }
        }

    override fun rejectSwapRequest(itemId: String, swapWithItemId: String): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            val userId = firebaseAuth.currentUser?.uid
            firestore.runTransaction { transaction ->
                val itemRef = firestore.collection("items").document(itemId)
                val swapWithItemRef = firestore.collection("items").document(swapWithItemId)
                val userRef = firestore.collection("users").document(userId!!)
                val item = transaction.get(itemRef)
                val user = transaction.get(userRef)

                val swapRequestInItem =
                    item.get("swapRequests") as MutableList<HashMap<String, Any>>?
                        ?: mutableListOf()
                val swapRequestInUser =
                    user.get("swapRequests") as MutableList<HashMap<String, Any>>?
                        ?: mutableListOf()

                swapRequestInItem.find { it["swapRequests"] == swapWithItemId }?.let {
                    it["status"] = "Rejected"
                }
                swapRequestInUser.find { it["itemId"] == itemId && it["swapWithItemId"] == swapWithItemId }
                    ?.put("status", "Rejected")

                transaction.update(itemRef, "swapRequests", swapRequestInItem)
                transaction.update(itemRef, "itemSwapStatus", "Rejected")
                transaction.update(swapWithItemRef, "itemSwapStatus", "Rejected")
                transaction.update(userRef, "swapRequests", swapRequestInUser)
            }.addOnSuccessListener {
                trySend(Resource.Success("Swap request rejected..."))
            }.addOnFailureListener { e ->
                Log.w("Error rejecting swap", "Error rejecting swap request", e)
                trySend(Resource.Error("Error rejecting swap request: ${e.message}"))
            }
            awaitClose { close() }
        }
}