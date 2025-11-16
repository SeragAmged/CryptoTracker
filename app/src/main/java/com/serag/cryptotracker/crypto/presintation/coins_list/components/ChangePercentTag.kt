package com.serag.cryptotracker.crypto.presintation.coins_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serag.cryptotracker.crypto.presintation.models.DisplayableNumber
import com.serag.cryptotracker.ui.theme.CryptoTrackerTheme
import com.serag.cryptotracker.ui.theme.greenBackground

@Composable
fun ChangePercentTag(changePercent: DisplayableNumber) {
    val isPositive = changePercent.value > 0.0
    val backgroundColor =
        if (isPositive) greenBackground else MaterialTheme.colorScheme.errorContainer
    val contentColor =
        if (isPositive) Color.Green else MaterialTheme.colorScheme.onErrorContainer

    val icon = if (isPositive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(100f))
            .background(color = backgroundColor)
            .padding(horizontal = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = contentColor
        )
        Text(
            text = "${changePercent.formatted}%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

@PreviewLightDark
@Composable
fun ChangePercentTagPreview() {
    CryptoTrackerTheme {
        ChangePercentTag(
            previewCoin.changePercent24Hr
        )
    }
}