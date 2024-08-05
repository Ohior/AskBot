package ohior.app.askbox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T> MyPopupMenu(
    modifier: Modifier = Modifier,
    popupItemList: List<T>,
    onItemClick: (T) -> Unit,
    menuItem: @Composable (RowScope.() -> Unit),
    itemCompose: @Composable (RowScope.() -> Unit)
) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedButton (onClick = {
        expanded = !expanded
    }, content = menuItem)
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = !expanded }) {
            popupItemList.forEach { t ->
                DropdownMenuItem(onClick = { onItemClick(t) }, content = itemCompose)
            }
        }
    }
}