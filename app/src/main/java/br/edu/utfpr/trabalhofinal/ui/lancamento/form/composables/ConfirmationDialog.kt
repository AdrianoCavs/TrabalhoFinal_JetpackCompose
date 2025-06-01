package br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.ui.theme.TrabalhoFinalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissButtonText: String? = null,
    confirmButtonText: String? = null,
    icon: ImageVector = Icons.Outlined.Info//ImageVector.vectorResource(R.drawable.caution_icon)
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        title = title?.let { { Text(text = it, style = MaterialTheme.typography.titleMedium); } },
        icon = { Icon(icon, contentDescription = "Atenção!") },
        text = { Text(text, textAlign = TextAlign.Center); },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton( onClick = onConfirm ) {
                Text(confirmButtonText ?: stringResource(R.string.confirmar));
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) {
                Text(dismissButtonText ?: stringResource(R.string.cancelar));
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationDialogPreview() {
    TrabalhoFinalTheme {
        ConfirmationDialog(
            title = "Excluir lançamento",
            text = "Essa ação não poderá ser desfeita",
            onDismiss = {},
            onConfirm = {}
        )
    }
}