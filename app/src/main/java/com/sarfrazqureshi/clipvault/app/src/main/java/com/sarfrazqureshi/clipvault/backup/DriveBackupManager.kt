package com.sarfrazqureshi.clipvault.backup

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import java.io.ByteArrayOutputStream
import java.util.Collections

object DriveBackupManager {

    private const val BACKUP_FILE_NAME = "clipvault_backup.json"

    fun getSignInClient(context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    fun getLastSignedInAccount(context: Context): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    private fun getDriveService(context: Context, account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("ClipVault")
            .build()
    }

    fun backup(context: Context, account: GoogleSignInAccount, jsonData: String): Boolean {
        return try {
            val drive = getDriveService(context, account)
            val existingId = findBackupFileId(drive)
            val content = ByteArrayContent("application/json", jsonData.toByteArray())
            if (existingId != null) {
                drive.files().update(existingId, null, content).execute()
            } else {
                val metadata = File()
                metadata.name = BACKUP_FILE_NAME
                metadata.parents = Collections.singletonList("appDataFolder")
                drive.files().create(metadata, content).execute()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun restore(context: Context, account: GoogleSignInAccount): String? {
        return try {
            val drive = getDriveService(context, account)
            val fileId = findBackupFileId(drive) ?: return null
            val outputStream = ByteArrayOutputStream()
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream)
            outputStream.toString("UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun findBackupFileId(drive: Drive): String? {
        val result = drive.files().list()
            .setSpaces("appDataFolder")
            .setQ("name = '$BACKUP_FILE_NAME'")
            .setFields("files(id, name)")
            .execute()
        return result.files?.firstOrNull()?.id
    }
}
