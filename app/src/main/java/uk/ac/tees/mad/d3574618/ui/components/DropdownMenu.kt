package uk.ac.tees.mad.d3574618.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenu(
    list: List<T>,
    category: (T) -> Unit,
    focusManager: FocusManager,
    displayText: (T) -> String,
    placeholder: String = "Select item"
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val options = list.map { displayText(it) to it }
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(placeholder) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selectedOptionText,
                readOnly = true,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed() { index, selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.first) },
                        onClick = {
                            selectedOptionText = selectionOption.first
                            category(options[index].second)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()

                    )
                }
            }
        }
    }
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DropdownMenu(
//    list: List<ItemCategory>,
//    category: (ItemCategory) -> Unit,
//    focusManager: FocusManager
//) {
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        val options = list.map { it.name to it }
//        var expanded by remember { mutableStateOf(false) }
//        var selectedOptionText by remember { mutableStateOf("Category") }
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = { expanded = !expanded },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            OutlinedTextField(
//                modifier = Modifier
//                    .menuAnchor()
//                    .fillMaxWidth(),
//                value = selectedOptionText,
//                readOnly = true,
//                onValueChange = {},
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                shape = MaterialTheme.shapes.small,
//                keyboardOptions = KeyboardOptions(
//                    imeAction = ImeAction.Next
//                ),
//                keyboardActions = KeyboardActions(onNext = {
//                    focusManager.moveFocus(FocusDirection.Down)
//                })
//            )
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                options.forEachIndexed() { index, selectionOption ->
//                    DropdownMenuItem(
//                        text = { Text(selectionOption.first) },
//                        onClick = {
//                            selectedOptionText = selectionOption.first
//                            category(options[index].second)
//                            expanded = false
//                        },
//                        modifier = Modifier.fillMaxWidth()
//
//                    )
//                }
//            }
//        }
//    }
//}