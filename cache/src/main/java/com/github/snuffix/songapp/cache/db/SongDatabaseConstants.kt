package com.github.snuffix.songapp.cache.db


object SongDatabaseConstants {
    const val TABLE_NAME = "songs"
    const val COLUMN_TRACK_NAME = "trackName"
    const val QUERY_SONGS = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_TRACK_NAME LIKE :query LIMIT :limit OFFSET :offset"
    const val QUERY_COUNT = "SELECT count(*) FROM $TABLE_NAME"
}