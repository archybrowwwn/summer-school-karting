@file:OptIn(ExperimentalMaterial3Api::class)

package com.apexkarting.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ApexBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetPadding = ApexTheme.tokens.spacing.md
    val sheetShape = RoundedCornerShape(
        topStart = ApexTheme.tokens.spacing.xl,
        topEnd = ApexTheme.tokens.spacing.xl,
    )
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RectangleShape,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = ApexTheme.tokens.sizing.screenMaxWidth)
                    .background(color = MaterialTheme.colorScheme.surface, shape = sheetShape)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = sheetPadding,
                        end = sheetPadding,
                        top = sheetPadding,
                        bottom = ApexTheme.tokens.spacing.lg,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(sheetPadding),
            ) {
                ApexSheetDragHandle()
                content()
            }
        }
    }
}

@Composable
internal fun ApexSheetDragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                ),
        )
    }
}

@Composable
internal fun ApexSheetHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.tabHeaderHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (actionText != null && onActionClick != null) {
            ApexTextLink(
                text = actionText,
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

@Composable
internal fun ApexSheetContent(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

@Composable
internal fun ApexFormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl),
            )
            .padding(ApexTheme.tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        content()
    }
}