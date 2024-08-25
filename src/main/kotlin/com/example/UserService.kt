package com.example.plugins

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

class UserService(private val connection: Connection) {
    init {
        createTable()
    }

    private fun createTable() {
        connection.createStatement().use { statement ->
            statement.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    id UUID PRIMARY KEY,
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(50) NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL
                )
            """)
        }
    }

    private fun resultSetToUser(rs: ResultSet): User =
        User(
            id = rs.getObject("id", UUID::class.java),
            username = rs.getString("username"),
            password = rs.getString("password"),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )

    fun findById(id: UUID): User? {
        val sql = "SELECT * FROM Users WHERE id = ?"
        connection.prepareStatement(sql).use { statement ->
            statement.setObject(1, id)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) resultSetToUser(resultSet) else null
        }
    }

    fun findAll(): List<User> {
        val users = mutableListOf<User>()
        val sql = "SELECT * FROM Users"
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                users.add(resultSetToUser(resultSet))
            }
        }
        return users
    }

    fun save(user: User) {
        val sql = """
            INSERT INTO Users (id, username, password, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
        """
        connection.prepareStatement(sql).use { statement ->
            statement.setObject(1, user.id)
            statement.setString(2, user.username)
            statement.setString(3, user.password)
            statement.setTimestamp(4, Timestamp.valueOf(user.createdAt))
            statement.setTimestamp(5, Timestamp.valueOf(user.updatedAt))
            statement.executeUpdate()
        }
    }

    fun update(user: User) {
        val sql = """
            UPDATE Users SET username = ?, password = ?, updated_at = ?
            WHERE id = ?
        """
        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, user.username)
            statement.setString(2, user.password)
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()))
            statement.setObject(4, user.id)
            statement.executeUpdate()
        }
    }

    fun remove(id: UUID) {
        val sql = "DELETE FROM Users WHERE id = ?"
        connection.prepareStatement(sql).use { statement ->
            statement.setObject(1, id)
            statement.executeUpdate()
        }
    }
}
