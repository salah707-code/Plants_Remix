package com.example.plantencyclopedia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.ui.theme.CardBorder
import com.example.plantencyclopedia.ui.theme.CardSurface
import com.example.plantencyclopedia.ui.theme.SageGreen
import com.example.plantencyclopedia.ui.theme.SageGreenDark
import com.example.plantencyclopedia.ui.theme.TextMuted
import com.example.plantencyclopedia.ui.theme.TextSecondary

@Composable
fun EditPlantDialog(
    plant: Plant,
    onDismiss: () -> Unit,
    onSave: (name: String, english: String, scientific: String, family: String, usage: String, chemicals: List<String>, note: String) -> Unit
) {
    var name by remember { mutableStateOf(plant.name) }
    var english by remember { mutableStateOf(plant.english) }
    var scientific by remember { mutableStateOf(plant.scientific) }
    var family by remember { mutableStateOf(plant.family) }
    var usage by remember { mutableStateOf(plant.usage) }
    var chemicalsText by remember { mutableStateOf(plant.chemicals.joinToString("، ")) }
    var note by remember { mutableStateOf(plant.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تعديل سجل النبات",
                color = SageGreenDark,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("الاسم العربي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                )

                OutlinedTextField(
                    value = english,
                    onValueChange = { english = it },
                    label = { Text("الاسم بالإنجليزية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_english_input")
                )

                OutlinedTextField(
                    value = scientific,
                    onValueChange = { scientific = it },
                    label = { Text("الاسم العلمي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_scientific_input")
                )

                OutlinedTextField(
                    value = family,
                    onValueChange = { family = it },
                    label = { Text("الفصيلة النباتية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_family_input")
                )

                OutlinedTextField(
                    value = usage,
                    onValueChange = { usage = it },
                    label = { Text("الاستخدام (علاجية / غذائية / عطرية / تجميلية)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_usage_input")
                )

                OutlinedTextField(
                    value = chemicalsText,
                    onValueChange = { chemicalsText = it },
                    label = { Text("المواد الكيميائية الفعالة (مفصولة بفواصل)") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("edit_chemicals_input")
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("الوصف والملاحظات") },
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("edit_note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val chemList = chemicalsText
                        .split("،", ",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    onSave(name, english, scientific, family, usage, chemList, note)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                modifier = Modifier.testTag("save_edit_button")
            ) {
                Text("حفظ التعديلات")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_edit_button")
            ) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = CardSurface
    )
}
