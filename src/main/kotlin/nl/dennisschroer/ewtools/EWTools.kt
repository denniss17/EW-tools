package nl.dennisschroer.ewtools

import nl.dennisschroer.ewtools.tables.v6_1.SongWords
import nl.dennisschroer.ewtools.tables.v6_1.Songs
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.JDBC
import java.io.File
import java.sql.Connection
import javax.swing.text.rtf.RTFEditorKit

class EWTools(val path: String, val command: String) {
    /** Output directory of export command */
    val exportDir = "export"

    /** Path to databases relative to database root */
    val dataPath: String = "v6.1/Databases/Data"

    fun run() {
        validatePath()
        when (command) {
            "export" -> export()
        }
    }

    private fun export() {
        File(exportDir).mkdir()

        val songs = transaction(connect(Songs.databaseFile)) {
            Songs.selectAll().map { song ->
                Song(
                        song[Songs.id].value,
                        song[Songs.title],
                        transaction(connect(SongWords.databaseFile)) {
                            SongWords.select { SongWords.song eq song[Songs.id].value }.first()[SongWords.words]
                        }
                )
            }
        }

        songs.forEach {
            File("$exportDir/${fileNameForSong(it)}").writeText(fileContentForSong(it))
        }
    }

    private fun fileContentForSong(it: Song): String {
        // file content is RTF, but we want plain text
        // https://stackoverflow.com/a/5826234/3063682
        val rtfParser = RTFEditorKit()
        val document = rtfParser.createDefaultDocument()
        rtfParser.read(it.words.byteInputStream(), document, 0)
        return document.getText(0, document.length)
    }

    private fun fileNameForSong(song: Song): String = "${song.id}_${song.title.toLowerCase().replace(" ", "-").replace("[^a-z0-9-]".toRegex(), "")}.txt"

    private fun connect(databaseFileName: String): Database {
        val databaseFilePath = "$path/$dataPath/$databaseFileName"
        val connection = Database.connect("jdbc:sqlite:$databaseFilePath", driver = JDBC::class.java.name)

        // https://github.com/JetBrains/Exposed/wiki/FAQ#q-sqlite3-fails-with-the-error-transaction-attempt-0-failed-sqlite-supports-only-transaction_serializable-and-transaction_read_uncommitted
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        return connection
    }

    /** Validate if database exists in given path */
    private fun validatePath() {
        val databaseDirectory = File(path)
        if (!databaseDirectory.exists()) throw IllegalArgumentException("$databaseDirectory does not exist")
        if (!databaseDirectory.isDirectory) throw IllegalArgumentException("$databaseDirectory is not a directory")
        val dataDirectory = File("$path/$dataPath")
        if (!dataDirectory.exists()) throw IllegalArgumentException("$dataDirectory does not exist")
        if (!dataDirectory.isDirectory) throw IllegalArgumentException("$dataDirectory is not a directory")
    }
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: java -jar EW-tools.jar <databasepath> <command>")
        println()
        println("<databasepath>: path to root of EW database. Contains 'Resources' and 'v6.1'")
        println("Available commands:")
        println("  export - export songs from db to text files")
    } else {
        val path = args[0]
        val command = args[1]

        EWTools(path, command).run()
    }
}