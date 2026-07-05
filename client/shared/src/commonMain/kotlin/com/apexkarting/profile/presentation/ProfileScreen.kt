package com.apexkarting.profile.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.config.AppConfig
import com.apexkarting.core.logging.AppLogger
import com.apexkarting.core.phone.formatPhoneNumber
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.uikit.ApexShapes
import com.apexkarting.uikit.apexClickable
import com.apexkarting.core.ui.ActionStatus
import com.apexkarting.core.ui.Loadable
import com.apexkarting.core.ui.ApexBottomSheet
import com.apexkarting.core.ui.ApexSheetContent
import com.apexkarting.core.ui.TabScreenLayout
import com.apexkarting.core.ui.contentWidthModifier
import com.apexkarting.core.ui.tabScreenContentPadding
import com.apexkarting.core.ui.PhoneNumberVisualTransformation
import com.apexkarting.uikit.icons.ArrowRight
import com.apexkarting.uikit.icons.Edit
import com.apexkarting.uikit.icons.Icons
import com.apexkarting.uikit.icons.ApexIcon

@Composable
fun ProfileScreen(
    state: ProfileState,
    appConfig: AppConfig,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    val openExternalUrl: (String) -> Unit = { url ->
        runCatching { uriHandler.openUri(url) }
            .onFailure { failure -> AppLogger.e(failure, "Failed to open external URL: $url") }
    }

    LaunchedEffect(Unit) {
        onIntent(ProfileIntent.Load)
    }

    LaunchedEffect(state.message) {
        val message = state.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onIntent(ProfileIntent.MessageShown)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TabScreenLayout(title = "Профиль") {
            when (val profile = state.profile) {
                Loadable.Initial,
                Loadable.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
                is Loadable.Content -> ProfileContent(
                    state = state,
                    appConfig = appConfig,
                    clientName = profile.value.name.orEmpty(),
                    phone = profile.value.phone.value,
                    onOpenExternalUrl = openExternalUrl,
                    onIntent = onIntent,
                )
                is Loadable.Error -> ProfileError(onRetry = { onIntent(ProfileIntent.Load) })
                is Loadable.Empty -> ProfileError(onRetry = { onIntent(ProfileIntent.Load) })
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(ApexTheme.tokens.spacing.md),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    if (state.logoutConfirmVisible) {
        LogoutConfirmDialog(
            onConfirm = { onIntent(ProfileIntent.LogoutConfirmed) },
            onDismiss = { onIntent(ProfileIntent.LogoutDismissed) },
        )
    }
    if (state.deleteConfirmVisible) {
        DeleteAccountConfirmDialog(
            onConfirm = { onIntent(ProfileIntent.DeleteConfirmed) },
            onDismiss = { onIntent(ProfileIntent.DeleteDismissed) },
        )
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState,
    appConfig: AppConfig,
    clientName: String,
    phone: String,
    onOpenExternalUrl: (String) -> Unit,
    onIntent: (ProfileIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .contentWidthModifier()
            .verticalScroll(rememberScrollState())
            .padding(tabScreenContentPadding()),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    ) {
        when (state.mode) {
            ProfileMode.View -> ProfileViewContent(
                state = state,
                appConfig = appConfig,
                clientName = clientName,
                phone = phone,
                onOpenExternalUrl = onOpenExternalUrl,
                onIntent = onIntent,
            )
            ProfileMode.Edit -> ProfileEditContent(
                state = state,
                onIntent = onIntent,
            )
            ProfileMode.ConfirmPhone -> ProfilePhoneConfirmContent(
                state = state,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun ProfileViewContent(
    state: ProfileState,
    appConfig: AppConfig,
    clientName: String,
    phone: String,
    onOpenExternalUrl: (String) -> Unit,
    onIntent: (ProfileIntent) -> Unit,
) {
    ProfileInfoRow(
        label = null,
        value = clientName.ifBlank { "Имя" },
        placeholder = clientName.isBlank(),
        onClick = { onIntent(ProfileIntent.EditClicked) },
    )
    ProfileInfoRow(
        label = "Телефон",
        value = formatPhoneNumber(phone),
        onClick = { onIntent(ProfileIntent.EditClicked) },
    )
    Spacer(Modifier.height(ApexTheme.tokens.spacing.md))
    ProfileLinks(
        appConfig = appConfig,
        onOpenExternalUrl = onOpenExternalUrl,
    )
    ProfileLogoutButton(state = state, onIntent = onIntent)
}

@Composable
private fun ProfileEditContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
) {
    ProfileTextField(
        value = state.nameInput,
        onValueChange = { onIntent(ProfileIntent.NameChanged(it)) },
        label = "Имя",
        enabled = !state.isSubmitting,
    )
    ProfileTextField(
        value = state.phoneInput,
        onValueChange = { onIntent(ProfileIntent.PhoneChanged(it)) },
        label = "Телефон",
        enabled = !state.isSubmitting,
        keyboardType = KeyboardType.Phone,
        visualTransformation = PhoneNumberVisualTransformation(),
    )
    state.fieldError?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    Button(
        onClick = { onIntent(ProfileIntent.SaveClicked) },
        enabled = state.canSave,
        shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.buttonHeight),
    ) {
        Text(if (state.isSubmitting) "Сохраняем..." else "Сохранить", fontWeight = FontWeight.Bold)
    }
    OutlinedButton(
        onClick = { onIntent(ProfileIntent.EditCancelled) },
        enabled = !state.isSubmitting,
        shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.buttonHeight),
    ) {
        Text("Отменить")
    }
}

@Composable
private fun ProfilePhoneConfirmContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Text(
        text = "Подтвердите новый номер кодом из SMS",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Text(
        text = formatPhoneNumber(state.pendingPhone ?: state.phoneInput),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    ProfileTextField(
        value = state.codeInput,
        onValueChange = { onIntent(ProfileIntent.CodeChanged(it)) },
        label = "Код из SMS",
        enabled = !state.isSubmitting,
    )
    state.fieldError?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    Button(
        onClick = { onIntent(ProfileIntent.ConfirmPhoneClicked) },
        enabled = state.canConfirmPhone,
        shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.buttonHeight),
    ) {
        Text(if (state.isSubmitting) "Проверяем..." else "Подтвердить", fontWeight = FontWeight.Bold)
    }
    TextButton(
        onClick = { onIntent(ProfileIntent.ResendPhoneCode) },
        enabled = state.canResendCode,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            if (state.resendSecondsRemaining > 0) {
                "Отправить код повторно (00:${state.resendSecondsRemaining.toString().padStart(2, '0')})"
            } else {
                "Отправить код повторно"
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    OutlinedButton(
        onClick = { onIntent(ProfileIntent.BackToEdit) },
        enabled = !state.isSubmitting,
        shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.buttonHeight),
    ) {
        Text("Назад к редактированию")
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
    )
}

@Composable
private fun ProfileInfoRow(
    label: String?,
    value: String,
    placeholder: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val rowShape = ApexShapes.control()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.buttonHeight)
            .then(
                if (onClick != null) {
                    Modifier.apexClickable(rowShape, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = rowShape,
            )
            .padding(horizontal = ApexTheme.tokens.spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = if (placeholder) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
        }
        ApexIcon(
            imageVector = Icons.Edit,
            contentDescription = "Редактировать",
            tint = MaterialTheme.colorScheme.onSurface,
            size = ApexTheme.tokens.spacing.lg,
        )
    }
}

@Composable
private fun ProfileLinks(
    appConfig: AppConfig,
    onOpenExternalUrl: (String) -> Unit,
) {
    // SCR-007 / AC-009: links are opened only when provided by app configuration.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(top = ApexTheme.tokens.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        InfoLine(
            label = "Правила картодрома",
            value = if (appConfig.rulesUrl != null) null else "не настроено",
            onClick = appConfig.rulesUrl?.let { url -> { onOpenExternalUrl(url) } },
        )
        InfoLine(
            label = "Поддержка",
            value = if (appConfig.supportUrl != null) null else "не настроено",
            onClick = appConfig.supportUrl?.let { url -> { onOpenExternalUrl(url) } },
        )
        InfoLine("Версия приложения", appConfig.appVersion)
    }
}

@Composable
private fun ProfileLogoutButton(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Button(
        onClick = { onIntent(ProfileIntent.LogoutClicked) },
        enabled = state.actionStatus == ActionStatus.Idle,
        shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.buttonHeight),
    ) {
        Text(if (state.isSubmitting) "Выходим..." else "Выйти", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InfoLine(
    label: String,
    value: String?,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            ApexIcon(
                imageVector = Icons.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                size = 16.dp,
            )
        }
    }
}

@Composable
private fun ProfileError(
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .contentWidthModifier()
            .padding(top = ApexTheme.tokens.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Не удалось загрузить профиль",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Повторить")
        }
    }
}

@Composable
private fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ProfileConfirmSheet(
        title = "Выйти из аккаунта?",
        message = "После выхода для записи на заезд нужно будет снова ввести телефон и код.",
        confirmText = "Выйти",
        dismissText = "Не выходить",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
private fun DeleteAccountConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    ProfileConfirmSheet(
        title = "Удалить аккаунт?",
        message = "Профиль будет удалён. Для новых записей потребуется зарегистрироваться снова.",
        confirmText = "Удалить",
        dismissText = "Отменить",
        confirmError = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileConfirmSheet(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmError: Boolean = false,
) {
    ApexBottomSheet(onDismissRequest = onDismiss) {
        ApexSheetContent {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = if (confirmError) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                },
                border = if (confirmError) null else BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(dismissText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
