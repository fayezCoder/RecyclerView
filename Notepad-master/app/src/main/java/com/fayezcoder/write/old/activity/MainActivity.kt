package com.fayezcoder.write.old.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadActivity
import com.fayezcoder.write.old.fragment.NoteEditFragment
import com.fayezcoder.write.old.fragment.NoteListFragment
import com.fayezcoder.write.old.fragment.NoteViewFragment
import com.fayezcoder.write.old.fragment.WelcomeFragment
import com.fayezcoder.write.old.fragment.dialog.BackButtonDialogFragment
import com.fayezcoder.write.old.fragment.dialog.DeleteDialogFragment
import com.fayezcoder.write.old.fragment.dialog.FirstRunDialogFragment
import com.fayezcoder.write.old.fragment.dialog.SaveButtonDialogFragment
import com.fayezcoder.write.old.util.WebViewInitState
import org.apache.commons.lang3.StringUtils
import us.feras.mdv.MarkdownView
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : NotepadBaseActivity(), BackButtonDialogFragment.Listener, DeleteDialogFragment.Listener, SaveButtonDialogFragment.Listener, FirstRunDialogFragment.Listener, NoteListFragment.Listener, NoteEditFragment.Listener, NoteViewFragment.Listener {
    var filesToExport: Array<Any>? = null
    var filesToDelete: Array<Any>? = null
    var fileBeingExported = 0
    var successful = true
    private var cab = ArrayList<String>()
    private var inCabMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (packageManager.getComponentEnabledSetting(
                        ComponentName(this, NotepadActivity::class.java)
                ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            val intent = Intent(this, NotepadActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set action bar elevation
            supportActionBar!!.elevation = resources.getDimensionPixelSize(R.dimen.action_bar_elevation).toFloat()
        }

        // Show dialog if this is the user's first time running Notepad
        val prefMain = getPreferences(Context.MODE_PRIVATE)
        if (prefMain.getInt("first-run", 0) == 0) {
            // Show welcome dialog
            if (supportFragmentManager.findFragmentByTag("firstrunfragment") == null) {
                val firstRun: DialogFragment = FirstRunDialogFragment()
                firstRun.show(supportFragmentManager, "firstrunfragment")
            }
        } else {
            // The following code is only present to support existing users of Notepad on Google Play
            // and can be removed if using this source code for a different app

            // Convert old preferences to new ones
            val pref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
            if (prefMain.getInt("sort-by", -1) == 0) {
                val editor = pref.edit()
                val editorMain = prefMain.edit()
                editor.putString("sort_by", "date")
                editorMain.putInt("sort-by", -1)
                editor.apply()
                editorMain.apply()
            } else if (prefMain.getInt("sort-by", -1) == 1) {
                val editor = pref.edit()
                val editorMain = prefMain.edit()
                editor.putString("sort_by", "name")
                editorMain.putInt("sort-by", -1)
                editor.apply()
                editorMain.apply()
            }
            if (pref.getString("font_size", "null") == "null") {
                val editor = pref.edit()
                editor.putString("font_size", "large")
                editor.apply()
            }


            // Rename any saved drafts from 1.3.x
            val oldDraft = File(filesDir.toString() + File.separator + "draft")
            val newDraft = File(filesDir.toString() + File.separator + System.currentTimeMillis().toString())
            if (oldDraft.exists()) oldDraft.renameTo(newDraft)
        }
        val transaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentById(R.id.noteList) !is NoteListFragment) transaction.replace(R.id.noteList, NoteListFragment(), "NoteListFragment")
        if (!(supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteEditFragment
                        || supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteViewFragment)) {
            if ((supportFragmentManager.findFragmentById(R.id.noteViewEdit) == null
                            && findViewById<View>(R.id.layoutMain).tag == "main-layout-large")
                    || (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteListFragment
                            && findViewById<View>(R.id.layoutMain).tag == "main-layout-large")) transaction.replace(R.id.noteViewEdit, WelcomeFragment(), "NoteListFragment") else if (findViewById<View>(R.id.layoutMain).tag == "main-layout-normal") transaction.replace(R.id.noteViewEdit, NoteListFragment(), "NoteListFragment")
        }

        // Commit fragment transaction
        transaction.commit()
        if (savedInstanceState != null) {
            val filesToExportList = savedInstanceState.getStringArrayList("files_to_export")
            if (filesToExportList != null) filesToExport = filesToExportList.toTypedArray()
            val filesToDeleteList = savedInstanceState.getStringArrayList("files_to_delete")
            if (filesToDeleteList != null) filesToDelete = filesToDeleteList.toTypedArray()
            val savedCab = savedInstanceState.getStringArrayList("cab")
            if (savedCab != null) {
                inCabMode = true
                cab = savedCab
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val wvState = WebViewInitState.getInstance()
        wvState.initialize(this)
    }

    override fun onPause() {
        super.onPause()
        if (!inCabMode) cab.clear()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    // Keyboard shortcuts
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyShortcutEvent(event: KeyEvent): Boolean {
        super.dispatchKeyShortcutEvent(event)
        if (event.action == KeyEvent.ACTION_DOWN && event.isCtrlPressed) {
            if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteListFragment) {
                val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as NoteListFragment?
                fragment!!.dispatchKeyShortcutEvent(event.keyCode)
            } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteViewFragment) {
                val fragment = supportFragmentManager.findFragmentByTag("NoteViewFragment") as NoteViewFragment?
                fragment!!.dispatchKeyShortcutEvent(event.keyCode)
            } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteEditFragment) {
                val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
                fragment!!.dispatchKeyShortcutEvent(event.keyCode)
            } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is WelcomeFragment) {
                val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as WelcomeFragment?
                fragment!!.dispatchKeyShortcutEvent(event.keyCode)
            }
            return true
        }
        return super.dispatchKeyShortcutEvent(event)
    }

    override fun onDeleteDialogPositiveClick() {
        if (filesToDelete != null) {
            reallyDeleteNotes()
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteViewFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteViewFragment") as NoteViewFragment?
            fragment!!.onDeleteDialogPositiveClick()
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteEditFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
            fragment!!.onDeleteDialogPositiveClick()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteListFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as NoteListFragment?
            fragment!!.onBackPressed()
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteViewFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteViewFragment") as NoteViewFragment?
            fragment!!.onBackPressed()
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteEditFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
            fragment!!.onBackPressed(null)
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is WelcomeFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as WelcomeFragment?
            fragment!!.onBackPressed()
        }
    }

    override fun viewNote(filename: String) {
        viewEditNote(filename, false)
    }

    override fun editNote(filename: String) {
        viewEditNote(filename, true)
    }

    // Method used by selecting a existing note from the ListView in NoteViewFragment or NoteEditFragment
    // We need this method in MainActivity because sometimes getSupportFragmentManager() is null
    fun viewEditNote(filename: String, isEdit: Boolean) {
        val currentFilename: String
        currentFilename = if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteEditFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
            fragment!!.filename
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteViewFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteViewFragment") as NoteViewFragment?
            fragment!!.filename
        } else ""
        if (currentFilename != filename) {
            if (findViewById<View>(R.id.layoutMain).tag == "main-layout-normal") cab.clear()
            if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteEditFragment) {
                val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
                fragment!!.switchNotes(filename)
            } else {
                val bundle = Bundle()
                bundle.putString("filename", filename)
                val fragment: Fragment
                val tag: String
                if (isEdit) {
                    fragment = NoteEditFragment()
                    tag = "NoteEditFragment"
                } else {
                    fragment = NoteViewFragment()
                    tag = "NoteViewFragment"
                }
                fragment.arguments = bundle

                // Add NoteViewFragment or NoteEditFragment
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.noteViewEdit, fragment, tag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit()
            }
        }
    }

    override fun onBackDialogNegativeClick(filename: String) {
        val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
        fragment!!.onBackDialogNegativeClick(filename)
    }

    override fun onBackDialogPositiveClick(filename: String) {
        val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
        fragment!!.onBackDialogPositiveClick(filename)
    }

    override fun onSaveDialogNegativeClick() {
        val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
        fragment!!.onSaveDialogNegativeClick()
    }

    override fun onSaveDialogPositiveClick() {
        val fragment = supportFragmentManager.findFragmentByTag("NoteEditFragment") as NoteEditFragment?
        fragment!!.onSaveDialogPositiveClick()
    }

    override fun showBackButtonDialog(filename: String) {
        val bundle = Bundle()
        bundle.putString("filename", filename)
        val backFragment: DialogFragment = BackButtonDialogFragment()
        backFragment.arguments = bundle
        backFragment.show(supportFragmentManager, "back")
    }

    override fun showDeleteDialog() {
        showDeleteDialog(true)
    }

    private fun showDeleteDialog(clearFilesToDelete: Boolean) {
        if (clearFilesToDelete) filesToDelete = null
        val bundle = Bundle()
        bundle.putInt("dialog_title",
                if (filesToDelete == null || filesToDelete!!.size == 1) R.string.dialog_delete_button_title else R.string.dialog_delete_button_title_plural)
        val deleteFragment: DialogFragment = DeleteDialogFragment()
        deleteFragment.arguments = bundle
        deleteFragment.show(supportFragmentManager, "delete")
    }

    override fun showSaveButtonDialog() {
        val saveFragment: DialogFragment = SaveButtonDialogFragment()
        saveFragment.show(supportFragmentManager, "save")
    }

    override fun isShareIntent(): Boolean {
        return false
    }

    override fun getCabString(size: Int): String {
        return if (size == 1) resources.getString(R.string.cab_note_selected) else resources.getString(R.string.cab_notes_selected)
    }

    override fun deleteNotes() {
        filesToDelete = cab.toTypedArray()
        cab.clear()
        showDeleteDialog(false)
    }

    private fun reallyDeleteNotes() {
        // Build the pathname to delete each file, them perform delete operation
        for (file in filesToDelete!!) {
            val fileToDelete = File(filesDir.toString() + File.separator + file)
            fileToDelete.delete()
        }
        val filesToDelete2 = arrayOfNulls<String>(filesToDelete!!.size)
        Arrays.asList(filesToDelete).toArray<String>(filesToDelete2)

        // Send broadcasts to update UI
        val deleteIntent = Intent()
        deleteIntent.action = "com.fayezcoder.write.old.DELETE_NOTES"
        deleteIntent.putExtra("files", filesToDelete2)
        LocalBroadcastManager.getInstance(this).sendBroadcast(deleteIntent)
        val listIntent = Intent()
        listIntent.action = "com.fayezcoder.write.old.LIST_NOTES"
        LocalBroadcastManager.getInstance(this).sendBroadcast(listIntent)

        // Show toast notification
        if (filesToDelete!!.size == 1) showToast(R.string.note_deleted) else showToast(R.string.notes_deleted)
        filesToDelete = null
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun exportNotes() {
        filesToExport = cab.toTypedArray()
        cab.clear()
        if (filesToExport!!.size == 1 || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fileBeingExported = 0
            reallyExportNotes()
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            try {
                startActivityForResult(intent, EXPORT_TREE)
            } catch (e: ActivityNotFoundException) {
                showToast(R.string.error_exporting_notes)
            }
        }
    }

    override fun exportNote(filename: String) {
        filesToExport = arrayOf(filename)
        fileBeingExported = 0
        reallyExportNotes()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun reallyExportNotes() {
        var filename = ""
        var fileSuffix = ""
        try {
            filename = loadNoteTitle(filesToExport!![fileBeingExported].toString())
        } catch (e: IOException) { /* Gracefully fail */
        }
        fileSuffix = try {
            getNoteTimestamp(filesToExport!![fileBeingExported].toString())
        } catch (e: NumberFormatException) {
            //For draft notes, get current time
            getNoteTimestamp(System.currentTimeMillis().toString())
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TITLE, generateFilename(filename, fileSuffix))
        try {
            startActivityForResult(intent, EXPORT)
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.error_exporting_notes)
        }
    }

    private fun generateFilename(filename: String, lastModified: String): String {
        // Remove any invalid characters
        var filename = filename
        val characters = arrayOf("<", ">", ":", "\"", "/", "\\\\", "\\|", "\\?", "\\*")
        for (character in characters) {
            filename = filename.replace(character.toRegex(), "")
        }
        val pref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        val fileNameType = pref.getString("export_filename", "text-only")

        // To ensure that the generated filename fits within filesystem limitations,
        // truncate the filename to ~245 characters.
        if (fileNameType == "text-only") {
            val maxLength = 245
            if (filename.length > maxLength) filename = filename.substring(0, maxLength)
        } else {
            val maxLength = 245 - (lastModified.length + 1)
            if (filename.length > maxLength) filename = filename.substring(0, maxLength)
            filename = if (fileNameType == "text-timestamp") {
                //Add timestamp as suffix
                filename + "_" + lastModified
            } else {
                //Add timestamp as prefix
                lastModified + "_" + filename
            }
        }
        return "$filename.txt"
    }

    // Methods used to generate toast notifications
    private fun showToast(message: Int) {
        val toast = Toast.makeText(this, resources.getString(message), Toast.LENGTH_SHORT)
        toast.show()
    }

    // Loads note from /data/data/com.fayezcoder.write.old/files
    @kotlin.jvm.Throws(IOException::class)
    override fun loadNote(filename: String): String {

        // Initialize StringBuilder which will contain note
        val note = StringBuilder()

        // Open the file on disk
        val input = openFileInput(filename)
        val reader = InputStreamReader(input)
        val buffer = BufferedReader(reader)

        // Load the file
        var line = buffer.readLine()
        while (line != null) {
            note.append(line)
            line = buffer.readLine()
            if (line != null) note.append("\n")
        }

        // Close file on disk
        reader.close()
        return note.toString()
    }

    // Loads first line of a note for display in the ListView
    @kotlin.jvm.Throws(IOException::class)
    override fun loadNoteTitle(filename: String): String {
        // Open the file on disk
        val input = openFileInput(filename)
        val reader = InputStreamReader(input)
        val buffer = BufferedReader(reader)

        // Load the file
        val line = buffer.readLine()

        // Close file on disk
        reader.close()
        return line
    }

    // Calculates last modified date/time of a note for display in the ListView
    override fun loadNoteDate(filename: String): String {
        val lastModified = Date(filename.toLong())
        return DateFormat
                .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(lastModified)
    }

    // Calculates last modified date/time of a note for exporting
    private fun getNoteTimestamp(filename: String): String {
        //Get the current locale
        val locale: Locale
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm", locale)
        return dateFormat.format(Date(filename.toLong()))
    }

    override fun showFab() {
        inCabMode = false
        if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteListFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as NoteListFragment?
            fragment!!.showFab()
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is WelcomeFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as WelcomeFragment?
            fragment!!.showFab()
        }
    }

    override fun hideFab() {
        inCabMode = true
        if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is NoteListFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as NoteListFragment?
            fragment!!.hideFab()
        } else if (supportFragmentManager.findFragmentById(R.id.noteViewEdit) is WelcomeFragment) {
            val fragment = supportFragmentManager.findFragmentByTag("NoteListFragment") as WelcomeFragment?
            fragment!!.hideFab()
        }
    }

    override fun onFirstRunDialogPositiveClick() {
        // Set some initial preferences
        val prefMain = getPreferences(Context.MODE_PRIVATE)
        val editor = prefMain.edit()
        editor.putInt("first-run", 1)
        editor.apply()
        val pref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
        val editor2 = pref.edit()
        editor2.putBoolean("show_dialogs", false)
        editor2.apply()
    }

    @SuppressLint("MissingSuperCall")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (resultCode == Activity.RESULT_OK && resultData != null) {
            successful = true
            if (requestCode == IMPORT) {
                val uri = resultData.data
                val clipData = resultData.clipData
                if (uri != null) successful = importNote(uri) else if (clipData != null) for (i in 0 until clipData.itemCount) {
                    successful = importNote(clipData.getItemAt(i).uri)
                }

                // Show toast notification
                showToast(if (successful) if (uri == null) R.string.notes_imported_successfully else R.string.note_imported_successfully else R.string.error_importing_notes)

                // Send broadcast to NoteListFragment to refresh list of notes
                val listNotesIntent = Intent()
                listNotesIntent.action = "com.fayezcoder.write.old.LIST_NOTES"
                LocalBroadcastManager.getInstance(this).sendBroadcast(listNotesIntent)
            } else if (requestCode == EXPORT) {
                try {
                    saveExportedNote(loadNote(filesToExport!![fileBeingExported].toString()), resultData.data)
                } catch (e: IOException) {
                    successful = false
                }
                fileBeingExported++
                if (fileBeingExported < filesToExport!!.size) reallyExportNotes() else showToast(if (successful) if (fileBeingExported == 1) R.string.note_exported_to else R.string.notes_exported_to else R.string.error_exporting_notes)
                val fileToDelete = File(filesDir.toString() + File.separator + "exported_note")
                fileToDelete.delete()
            } else if (requestCode == EXPORT_TREE) {
                val tree = DocumentFile.fromTreeUri(this, resultData.data!!)
                for (exportFilename in filesToExport!!) {
                    try {
                        val file = tree!!.createFile(
                                "text/plain",
                                generateFilename(
                                        loadNoteTitle(exportFilename.toString()),
                                        getNoteTimestamp(exportFilename.toString())))
                        if (file != null) saveExportedNote(loadNote(exportFilename.toString()), file.uri) else successful = false
                    } catch (e: IOException) {
                        successful = false
                    }
                }
                showToast(if (successful) R.string.notes_exported_to else R.string.error_exporting_notes)
            }
        }
    }

    @kotlin.jvm.Throws(IOException::class)
    private fun saveExportedNote(note: String, uri: Uri?) {
        // Convert line separators to Windows format
        var note = note
        note = note.replace("\r\n".toRegex(), "\n")
        note = note.replace("\n".toRegex(), "\r\n")

        // Write file to external storage
        val os = contentResolver.openOutputStream(uri!!)
        if (os != null) {
            os.write(note.toByteArray())
        }
        os!!.close()
    }

    private fun importNote(uri: Uri): Boolean {
        return try {
            var importedFile = File(filesDir, System.currentTimeMillis().toString())
            var suffix: Long = 0

            // Handle cases where a note may have a duplicate title
            while (importedFile.exists()) {
                suffix++
                importedFile = File(filesDir, (System.currentTimeMillis() + suffix).toString())
            }
            val `is` = contentResolver.openInputStream(uri)
            val data = ByteArray(`is`!!.available())
            if (data.size > 0) {
                val os: OutputStream = FileOutputStream(importedFile)
                `is`.read(data)
                os.write(data)
                `is`.close()
                os.close()
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun printNote(contentToPrint: String) {
        val pref = getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)

        // Create a WebView object specifically for printing
        val generateHtml = !(pref.getBoolean("markdown", false)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        val webView = if (generateHtml) WebView(this) else MarkdownView(this)

        // Apply theme
        val theme = pref.getString("theme", "light-sans")
        var textSize = -1
        var fontFamily: String? = null
        if (theme != null) {
            if (theme.contains("sans")) {
                fontFamily = "sans-serif"
            }
        }
        if (theme != null) {
            if (theme.contains("serif")) {
                fontFamily = "serif"
            }
        }
        if (theme != null) {
            if (theme.contains("monospace")) {
                fontFamily = "monospace"
            }
        }
        when (pref.getString("font_size", "normal")) {
            "smallest" -> textSize = 12
            "small" -> textSize = 14
            "normal" -> textSize = 16
            "large" -> textSize = 18
            "largest" -> textSize = 20
        }
        val topBottom = " " + (resources.getDimension(R.dimen.padding_top_bottom_print) / resources.displayMetrics.density).toString() + "px"
        val leftRight = " " + (resources.getDimension(R.dimen.padding_left_right_print) / resources.displayMetrics.density).toString() + "px"
        val fontSize = " " + textSize.toString() + "px"
        val css = "body { " +
                "margin:" + topBottom + topBottom + leftRight + leftRight + "; " +
                "font-family:" + fontFamily + "; " +
                "font-size:" + fontSize + "; " +
                "}"
        webView.settings.javaScriptEnabled = false
        webView.settings.loadsImagesAutomatically = false
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                createWebPrintJob(view)
            }
        }

        // Load content into WebView
        if (generateHtml) {
            webView.loadDataWithBaseURL(null,
                    "<link rel='stylesheet' type='text/css' href='data:text/css;base64,"
                            + Base64.encodeToString(css.toByteArray(), Base64.DEFAULT)
                            + "' /><html><body><p>"
                            + StringUtils.replace(contentToPrint, "\n", "<br>")
                            + "</p></body></html>",
                    "text/HTML", "UTF-8", null)
        } else (webView as MarkdownView).loadMarkdown(contentToPrint,
                "data:text/css;base64," + Base64.encodeToString(css.toByteArray(), Base64.DEFAULT))
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun createWebPrintJob(webView: WebView) {
        // Get a PrintManager instance
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager

        // Get a print adapter instance
        val printAdapter = webView.createPrintDocumentAdapter()

        // Create a print job with name and adapter instance
        val jobName = getString(R.string.document, getString(R.string.app_name))
        printManager.print(jobName, printAdapter,
                PrintAttributes.Builder().build())
    }

    override fun startMultiSelect() {
        var fragment: NoteListFragment? = null
        if (findViewById<View>(R.id.layoutMain).tag == "main-layout-normal") fragment = supportFragmentManager.findFragmentById(R.id.noteViewEdit) as NoteListFragment?
        if (findViewById<View>(R.id.layoutMain).tag == "main-layout-large") fragment = supportFragmentManager.findFragmentById(R.id.noteList) as NoteListFragment?
        fragment?.startMultiSelect()
    }

    override fun getPreferences(mode: Int): SharedPreferences {
        return getSharedPreferences("MainActivity", mode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (filesToExport != null && filesToExport!!.size > 0) {
            val filesToExportList = ArrayList<String>()
            for (file in filesToExport!!) {
                filesToExportList.add(file.toString())
            }
            outState.putStringArrayList("files_to_export", filesToExportList)
        }
        if (filesToDelete != null && filesToDelete!!.size > 0) {
            val filesToDeleteList = ArrayList<String>()
            for (file in filesToDelete!!) {
                filesToDeleteList.add(file.toString())
            }
            outState.putStringArrayList("files_to_delete", filesToDeleteList)
        }
        if (inCabMode && cab.size > 0) outState.putStringArrayList("cab", cab)
        super.onSaveInstanceState(outState)
    }

    override fun getCabArray(): ArrayList<String> {
        return cab
    }

    companion object {
        const val IMPORT = 42
        const val EXPORT = 43
        const val EXPORT_TREE = 44
    }
}

private fun <T> Any.toArray(filesToDelete2: Array<T?>) {

}
