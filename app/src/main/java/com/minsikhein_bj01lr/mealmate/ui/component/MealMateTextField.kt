package com.minsikhein_bj01lr.mealmate.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed

import androidx.compose.material3.OutlinedTextField
import com.minsikhein_bj01lr.mealmate.ui.theme.SoftCreamyYellow

@Composable
fun MealMateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SoftCreamyYellow,
            unfocusedContainerColor = SoftCreamyYellow,
            disabledContainerColor = SoftCreamyYellow,
            focusedIndicatorColor = DeepRed,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            cursorColor = DeepRed
        )
    )
}