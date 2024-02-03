package com.borja.android.firestorage.data

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageService @Inject constructor(private val storage: FirebaseStorage) {

    fun basicExample() {
        val a = Firebase.storage

        //ruta/subruta/imagen.png
        val reference = Firebase.storage.reference.child("ejemplo/simpsons-frog-prince.gif")
        reference.name //simpsons-frog-prince.gif
        reference.path //ejemplo/simpsons-frog-prince.gif
        reference.bucket //gs://firestorage-6a759.appspot.com

        val reference2 = Firebase.storage.reference.child("ejemplo/ejemplo2/ejemplo3/imagen.png")
        val reference3 =
            Firebase.storage.reference.child("ejemplo/").child("ejemplo2/").child("ejemplo3/")
                .child("imagen.png")
    }

    fun uploadBasicImage(uri: Uri) {
        val reference = storage.reference.child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }

    suspend fun downloadBasicImage(): Uri {
        //    val reference = storage.reference.child("$userId/profile.png")
        val reference = storage.reference.child("ejemplo/simpsons-frog-prince.gif")

        reference.downloadUrl.addOnFailureListener { Log.i("borchx", "Faile") }
            .addOnSuccessListener { Log.i("borchx", "sucess") }

        return reference.downloadUrl.await()
    }

    suspend fun uploadAndDownImage(uri: Uri): Uri {
        /* readMetadaBasic()*/
        readMetaDataAdvance()
        return Uri.EMPTY

        /* return suspendCancellableCoroutine<Uri> { cancellableContinuation ->
             val reference = storage.reference.child("download/${uri.lastPathSegment}")
             reference.putFile(uri, createMetaData()).addOnSuccessListener {
                 downloadImage(it, cancellableContinuation)
             }.addOnFailureListener {
                 cancellableContinuation.resumeWithException(it)
             }
         }*/
    }

    private fun downloadImage(
        uploadTask: UploadTask.TaskSnapshot,
        cancellableContinuation: CancellableContinuation<Uri>
    ) {
        uploadTask.storage.downloadUrl
            .addOnSuccessListener { uri: Uri -> cancellableContinuation.resume(uri) }
            .addOnFailureListener { cancellableContinuation.resumeWithException(it) }
    }

    private suspend fun readMetadaBasic() {
        val reference = storage.reference.child("download/metadata6001156338751479957.jpg")
        val response = reference.metadata.await()
        val metainfo = response.getCustomMetadata("borchx")
        Log.i("Borchx MetaInfo", metainfo.orEmpty())
    }

    private suspend fun readMetaDataAdvance() {
        val reference = storage.reference.child("download/metadata6001156338751479957.jpg")
        val response = reference.metadata.await()
        response.customMetadataKeys.forEach { key ->
            response.getCustomMetadata(key)?.let { value ->
                Log.i("Borchx MetaInfo", "para la key $key el valor es $value")
            }

        }
    }

    private fun removeImage(): Boolean {
        val reference = storage.reference.child("download/metadata6001156338751479957.jpg")
        return reference.delete().isSuccessful
    }

    private fun createMetaData(): StorageMetadata {
        val metadata = storageMetadata {
            contentType = "image/jpeg"
            setCustomMetadata("date", "25/10/1989")
            setCustomMetadata("borchx", "super developer")
        }
        return metadata
    }

    private fun uploadImageWithProgress(uri: Uri) {
        val reference = storage.reference.child("ejemplo/imagen.png")
        reference.putFile(uri).addOnProgressListener { uploadTask ->
            val progress = (100.0 * uploadTask.bytesTransferred / uploadTask.totalByteCount)
            Log.i("transfer", progress.toString())
        }
    }

    suspend fun getAllImages(): List<Uri> {
        val reference = storage.reference.child("download/")
        /*reference.listAll().addOnSuccessListener { result ->
            result.items.forEach {
                Log.i("todas las imagenes", it.name)
            }
        }*/
        return reference.listAll().await().items.map {
            it.downloadUrl.await()
        }
    }

}