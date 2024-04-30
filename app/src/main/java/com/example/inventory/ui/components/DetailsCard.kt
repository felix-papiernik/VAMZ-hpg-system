package com.example.inventory.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.inventory.R

@Composable
fun DetailsCard(
    labelValuePairArray: Array<Pair<Int, String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        for (labelValuePair in labelValuePairArray) {
            DetailsRow(labelValuePair.first, labelValuePair.second, modifier = Modifier.padding(16.dp))
        }
    }
}

/**
 * A row in the details card that displays a label and a value separated by space.
 */
@Composable
fun DetailsRow(
    labelResId: Int,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResId))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable fun DetailsCardPreview() {
    val labelValuePairArray = arrayOf(
        Pair(R.string.label1, "Value1"),
        Pair(R.string.label2, "Value2"),
        Pair(R.string.label3, "Value3"),
    )
    DetailsCard(labelValuePairArray = labelValuePairArray)
}