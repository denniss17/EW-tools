package nl.dennisschroer.ewtools.tables.v6_1

import org.jetbrains.exposed.dao.IntIdTable

object SongWords : IntIdTable("word", columnName = "rowid") {
    val databaseFile = "SongWords.db"

    val song = reference("song_id", Songs)
    val words = text("words")
}