package com.example.inventory.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.inventory.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DateInput(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes labelResId: Int,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(stringResource(id = labelResId)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        isError = value.isNotEmpty() && !isValidDate(value),
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.dd_mm_yyyy)) }

    )
}

/**
 * Converts a date string in dd.mm.yyyy format to a long value.
 */
fun DateToLong(date: String): Long {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return dateFormat.parse(date).time
}
/**
 * Converts a long value to a date string in dd.mm.yyyy format.
 */
fun LongToDateString(date: Long): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return dateFormat.format(date)
}

fun isValidDate(date: String): Boolean {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    dateFormat.isLenient = false
    return try {
        dateFormat.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}

fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}