package nl.dennisschroer.ewtools.tables.v6_1

import org.jetbrains.exposed.dao.IntIdTable

object Songs : IntIdTable("song", columnName = "rowid") {
    val databaseFile = "Songs.db"

    val title = text("title")
    val author= text("author")
    val description = text("description")
}