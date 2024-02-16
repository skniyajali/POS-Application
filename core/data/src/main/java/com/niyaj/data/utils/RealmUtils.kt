package com.niyaj.data.utils

import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.types.BaseRealmObject
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

// Extension function to simplify collecting and processing results
suspend fun <T : BaseRealmObject, R> Flow<ResultsChange<T>>.collectAndSend(
    transform: (T) -> R,
    send: suspend (List<R>) -> Unit
) {
    collectLatest { result ->
        val mappedList = result.list.map { transform(it) }
        send(mappedList)
    }
}


// Extension function to simplify collecting, transforming, and sending results with search filtering
suspend fun <T : RealmObject, R> Flow<ResultsChange<T>>.collectWithSearch(
    transform: (T) -> R,
    searchFilter: (List<R>) -> List<R>,
    send: suspend (List<R>) -> Unit,
) {
    collectLatest { result ->
        when (result) {
            is InitialResults -> {
                val mappedList = result.list.map { transform(it) }
                val filteredList = searchFilter(mappedList)
                send(filteredList)
            }

            is UpdatedResults -> {
                val mappedList = result.list.map { transform(it) }
                val filteredList = searchFilter(mappedList)
                send(filteredList)
            }
        }
    }
}


// Extension function to simplify collecting, transforming, and sending results with search filtering
fun <T : RealmObject, R> ResultsChange<T>.collectWithSearch(
    transform: (T) -> R,
    searchFilter: (List<R>) -> List<R>,
): List<R> {
    return when (this) {
        is InitialResults -> {
            searchFilter(this.list.map { transform(it) })
        }

        is UpdatedResults -> {
            searchFilter(this.list.map { transform(it) })
        }
    }
}