package com.ecm.mychatapp


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

import java.io.File


class SettingsActivity : AppCompatActivity() {
    var mDataBase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var GALLERY_ID = 1
    private val CROP_IMAGE_ID = 2
    var mStorageRef: StorageReference? = null
    private lateinit var storage: FirebaseStorage
    // new vars
//    private lateinit var database: FirebaseDatabase
//    private lateinit var storage: FirebaseStorage
//    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        storage = FirebaseStorage.getInstance()
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        var userId = mCurrentUser!!.uid
        mDataBase = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        loadProfileImage()
        mDataBase!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

               var displayName = snapshot.child("display_name").value
                var image = snapshot.child("image").value
                var userStatus = snapshot.child("status").value
                var thumbNail = snapshot.child("thumb_image").value
                var settingStatus = findViewById<TextView>(R.id.settingsStatusText)
                var settingsDisplayName = findViewById<TextView>(R.id.settingsDisplayName)
               settingStatus.text = userStatus.toString()
                settingsDisplayName.text = displayName.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        var settingChangeStatus = findViewById<Button>(R.id.settingsChangeStatus)
        settingChangeStatus.setOnClickListener {
            var settingStatus = findViewById<TextView>(R.id.settingsStatusText)
            var intent = Intent(this, StatusActivity::class.java)
            intent.putExtra("status", settingStatus.text.toString().trim() )
            startActivity(intent)

        }
        var changeImageButton = findViewById<Button>(R.id.settingsChangeImgBtn)
        changeImageButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 25)

//            var galleryIntent = Intent()
//            galleryIntent.type = "image/*"
//            galleryIntent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_ID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 25 && data != null){
            var profileImage = findViewById<CircleImageView>(R.id.settingsProfileId)
            var image: Uri? = data!!.data
            if (image != null){
                profileImage.setImageURI(image)
                var userId = mCurrentUser!!.uid
                var storageRef = storage.reference.child("profile_pic").child(userId)

                storageRef.putFile(image).addOnSuccessListener {
                    Toast.makeText(this, "Image loaded successfully", Toast.LENGTH_LONG).show()

                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        mDataBase!!.child("thumb_image").setValue(uri.toString()).addOnSuccessListener {
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
        }
    }

    private fun loadProfileImage() {
        val userRef = mDataBase
        val profilePicRef = userRef!!.child("thumb_image")

        profilePicRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var profileImage = findViewById<CircleImageView>(R.id.settingsProfileId)
                val imageUrl = dataSnapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get().load(imageUrl).placeholder(R.drawable.profile_img)
                        .into(profileImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "Failed to load profile image: ${databaseError.message}")
            }
        })

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var status = dataSnapshot.child("status").getValue(String::class.java)
                var userName = dataSnapshot.child("display_name").getValue(String::class.java)

                var displayName = findViewById<TextView>(R.id.settingsDisplayName)
                var profileStatus = findViewById<TextView>(R.id.settingsStatusText)

                displayName.text = userName
                profileStatus.text = status


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "Failed to load user data: ${databaseError.message}")
            }
        })
    }
    companion object {
        private const val TAG = "SettingsActivity"
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == GALLERY_ID && resultCode == RESULT_OK) {
//            val imageUri: Uri? = data?.data
//
//            imageUri?.let {
//                // Start image cropping activity
//                startCropActivity(it)
//            }
//        } else if (requestCode === CROP_IMAGE_ID && resultCode == RESULT_OK) {
//            val croppedImageUri: Uri? = data?.data
//
//            // Use the cropped image URI as needed
//            if (croppedImageUri != null) {
//                val resultUri = croppedImageUri
//                // Update the image in Firebase or perform any desired action
//                // Example: uploadImageToFirebase(croppedImageUri)
//                val thumbFile = File(resultUri.path)
//                val options = BitmapFactory.Options().apply {
//                    inJustDecodeBounds = true
//                }
//                BitmapFactory.decodeFile(thumbFile.absolutePath, options)
//
//                // Calculate inSampleSize to scale down the image
//                options.inSampleSize = calculateInSampleSize(options, 200, 200)
//
//                // Decode bitmap with inSampleSize set
//                options.inJustDecodeBounds = false
//                val thumbBitmap = BitmapFactory.decodeFile(thumbFile.absolutePath, options)
//
//                // Use the thumbBitmap as needed
//                // Example: displayThumbImage(thumbBitmap)
//                var byteArray = ByteArrayOutputStream()
//                var userId = mCurrentUser!!.uid
//                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
//                var thumbByteArray: ByteArray
//                thumbByteArray = byteArray.toByteArray()
//                var filePath = mStorageRef!!.child("chat_profile_images")
//                    .child(userId + ".jpg")
//
//                var thumbFilePath = mStorageRef!!.child("chat_profile_images")
//                    .child("thumbs").child(userId + ".jpg")
//
//                filePath.putFile(resultUri).addOnCompleteListener{
//                    task : Task<UploadTask.TaskSnapshot> ->
//                    if (task.isSuccessful){
//
//                        var downloadUrl = task.result.uploadSessionUri.toString()
//
//                        var uploadTask: UploadTask = thumbFilePath.putBytes(thumbByteArray)
//
//                        uploadTask.addOnCompleteListener{
//                            task : Task<UploadTask.TaskSnapshot> ->
//                            var thumbUrl = task.result.uploadSessionUri.toString()
//                            if (task.isSuccessful){
//                                var updateObj = HashMap<String, Any>()
//                                updateObj.put("image", downloadUrl)
//                                updateObj.put("thumb_image", thumbUrl )
//
//                                mDataBase!!.updateChildren(updateObj).addOnCompleteListener {
//                                    task : Task<Void> ->
//                                    if (task.isSuccessful){
//                                        Toast.makeText(this, "Profile image saved", Toast.LENGTH_LONG).show()
//
//                                    }else{
//
//                                    }
//                                }
//
//                            }else{
//
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun startCropActivity(imageUri: Uri) {
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(imageUri, "image/*")
        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 256)
        cropIntent.putExtra("outputY", 256)
        cropIntent.putExtra("scale", true)
        cropIntent.putExtra("return-data", true)

        val cropImageUri = getOutputUri()
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri)

        startActivityForResult(cropIntent, CROP_IMAGE_ID)
    }

    private fun getOutputUri(): Uri? {
        // Create a file to save the cropped image
        val outputFile = File(externalCacheDir, "cropped_image.jpg")

        return if (outputFile.exists()) {
            outputFile.delete()
            FileProvider.getUriForFile(this, applicationContext.packageName + ".fileprovider", outputFile)
        } else {
            null
        }
    }
}



