package nl.dennisschroer.ewtools.tables.v6_1

import org.jetbrains.exposed.dao.IntIdTable

object Collections : IntIdTable("collection", columnName = "rowid") {
    val databaseFile = "Collections.db"

    val name = text("name")
}