package com.example.plantencyclopedia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.ui.theme.CardSurface
import com.example.plantencyclopedia.ui.theme.SageGreen
import com.example.plantencyclopedia.ui.theme.SageGreenDark
import com.example.plantencyclopedia.ui.theme.TextSecondary

@Composable
fun AddPlantDialog(
    onDismiss: () -> Unit,
    onAdd: (
        name: String,
        english: String,
        scientific: String,
        family: String,
        usage: String,
        chemicals: List<String>,
        note: String,
        image: String,
        habitat: String,
        partsUsed: String,
        preparation: String,
        precautions: String,
        growthForm: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var english by remember { mutableStateOf("") }
    var scientific by remember { mutableStateOf("") }
    var family by remember { mutableStateOf("الشفوية") }
    var usage by remember { mutableStateOf("علاجية") }
    var chemicalsText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var habitat by remember { mutableStateOf("") }
    var partsUsed by remember { mutableStateOf("") }
    var preparation by remember { mutableStateOf("") }
    var precautions by remember { mutableStateOf("") }
    var growthForm by remember { mutableStateOf("عشب معمر") }
    var image by remember { mutableStateOf("https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة نبات جديد إلى الموسوعة",
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
                    label = { Text("الاسم العربي (مثال: البابونج)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_name_input")
                )

                OutlinedTextField(
                    value = english,
                    onValueChange = { english = it },
                    label = { Text("الاسم بالإنجليزية (مثال: Chamomile)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_english_input")
                )

                OutlinedTextField(
                    value = scientific,
                    onValueChange = { scientific = it },
                    label = { Text("الاسم العلمي (مثال: Matricaria chamomilla)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_scientific_input")
                )

                OutlinedTextField(
                    value = family,
                    onValueChange = { family = it },
                    label = { Text("الفصيلة النباتية (مثال: النجمية)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_family_input")
                )

                OutlinedTextField(
                    value = usage,
                    onValueChange = { usage = it },
                    label = { Text("الاستخدام (علاجية / غذائية / عطرية / تجميلية)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_usage_input")
                )

                OutlinedTextField(
                    value = chemicalsText,
                    onValueChange = { chemicalsText = it },
                    label = { Text("المواد الفعالة (مفصولة بفواصل)") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("add_chemicals_input")
                )

                OutlinedTextField(
                    value = habitat,
                    onValueChange = { habitat = it },
                    label = { Text("الموطن والبيئة الطبيعية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_habitat_input")
                )

                OutlinedTextField(
                    value = partsUsed,
                    onValueChange = { partsUsed = it },
                    label = { Text("الأجزاء المستعملة (الأوراق / الأزهار...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_parts_used_input")
                )

                OutlinedTextField(
                    value = preparation,
                    onValueChange = { preparation = it },
                    label = { Text("طريقة التحضير والاستعمال") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("add_preparation_input")
                )

                OutlinedTextField(
                    value = precautions,
                    onValueChange = { precautions = it },
                    label = { Text("محاذير وتنبيهات الاستخدام") },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("add_precautions_input")
                )

                OutlinedTextField(
                    value = growthForm,
                    onValueChange = { growthForm = it },
                    label = { Text("طبيعة النمو (شجيرة / عشب معمر...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_growth_form_input")
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("الوصف والملاحظات الشاملة") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("add_note_input")
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
                    onAdd(
                        name, english, scientific, family, usage, chemList, note, image,
                        habitat, partsUsed, preparation, precautions, growthForm
                    )
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                modifier = Modifier.testTag("submit_add_plant_button")
            ) {
                Text("إضافة النبات")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_plant_button")
            ) {
                Text("إلغاء", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = CardSurface
    )
}

