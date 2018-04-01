package nl.dennisschroer.ewtools.tables.v6_1

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.sql.Blob

object CollectionDetails : IntIdTable("detail", columnName = "rowid") {
    val databaseFile = "Collections.db"

    val collection = reference("collection_id", Collections)
    val inclusions = blob("inclusions") // Actual type is "int64a"
    val exclusions = blob("exclusions") // Actual type is "int64a"
}