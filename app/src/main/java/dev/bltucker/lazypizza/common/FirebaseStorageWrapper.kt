package dev.bltucker.lazypizza.common

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageWrapper @Inject constructor(private val firebaseStorage: FirebaseStorage) {

    suspend fun getDrinkImage(imageName: String): String {
        Log.d("FirebaseStorageWrapper", "Fetching image for Drink: $imageName")

        return firebaseStorage.reference.child("drink/$imageName").downloadUrl.await().toString()
    }

    suspend fun getIceCreamImage(imageName: String): String {
        Log.d("FirebaseStorageWrapper", "Fetching image for IceCream: $imageName")

        return firebaseStorage.reference.child("ice_cream/$imageName").downloadUrl.await().toString()
    }

    suspend fun getPizzaImage(imageName: String): String {
        Log.d("FirebaseStorageWrapper", "Fetching image for Pizza: $imageName")
        return firebaseStorage.reference.child("pizza").child(imageName).downloadUrl.await().toString()
    }

    suspend fun getSauceImage(imageName: String): String {
        Log.d("FirebaseStorageWrapper", "Fetching image for Sauce: $imageName")

        return firebaseStorage.reference.child("sauce/$imageName").downloadUrl.await().toString()
    }

    suspend fun getToppingsImage(imageName: String): String {
        Log.d("FirebaseStorageWrapper", "Fetching image for Toppings: $imageName")

        return firebaseStorage.reference.child("toppings/$imageName").downloadUrl.await().toString()
    }

}