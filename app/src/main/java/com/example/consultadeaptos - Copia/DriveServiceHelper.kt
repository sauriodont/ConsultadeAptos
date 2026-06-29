package com.example.consultadeaptos

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

object DriveServiceHelper {

    private var driveService: Drive? = null

    fun initDriveService(context: Context, accountName: String) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
        )
        credential.selectedAccountName = accountName

        driveService = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("Consulta de Aptos")
            .build()
    }

    fun getDriveService(): Drive {
        if (driveService == null) {
            throw IllegalStateException("DriveService no inicializado. Llama a initDriveService primero.")
        }
        return driveService!!
    }
}
