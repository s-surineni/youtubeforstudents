package com.example.youtubeforstudents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    sectionDurationNumber: String,
    onSectionDurationNumberChange: (String) -> Unit,
    selectedTimeUnit: TimeUnit,
    onTimeUnitChange: (TimeUnit) -> Unit,
    showSectionControls: Boolean,
    onShowSectionControlsChange: (Boolean) -> Unit,
    sectionDurationSeconds: Int?,
    onBackClick: () -> Unit
) {
    // Helper function to convert seconds to readable duration
    fun secondsToReadableDuration(seconds: Int): String {
        return when {
            seconds < 60 -> "$seconds second${if (seconds != 1) "s" else ""}"
            seconds < 3600 -> {
                val minutes = seconds / 60
                "$minutes minute${if (minutes != 1) "s" else ""}"
            }
            else -> {
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                "$hours hour${if (hours != 1) "s" else ""} $minutes minute${if (minutes != 1) "s" else ""}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Empty space for alignment
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section Mode Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Section-Based Learning",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Break videos into manageable sections for focused learning",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Section Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Enable Section Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Switch(
                        checked = showSectionControls,
                        onCheckedChange = onShowSectionControlsChange
                    )
                }
                
                if (showSectionControls) {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Section Duration Settings
                    Text(
                        text = "Section Duration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Number input
                    OutlinedTextField(
                        value = sectionDurationNumber,
                        onValueChange = { 
                            // Only allow numbers
                            if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                                onSectionDurationNumberChange(it)
                            }
                        },
                        label = { Text("Duration") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Enter a number") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Radio buttons for time unit
                    Text(
                        text = "Time Unit:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTimeUnit == TimeUnit.SECONDS,
                                onClick = { onTimeUnitChange(TimeUnit.SECONDS) }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Seconds")
                        }
                        
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTimeUnit == TimeUnit.MINUTES,
                                onClick = { onTimeUnitChange(TimeUnit.MINUTES) }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Minutes")
                        }
                    }
                    
                    // Section Info
                    if (sectionDurationSeconds != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Section Configuration",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = "Each section will be ${secondsToReadableDuration(sectionDurationSeconds!!)} long",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // How It Works Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "How Section Mode Works",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val steps = listOf(
                    "1. Set your desired section duration above",
                    "2. Play any video in the main screen",
                    "3. Video automatically pauses after each section",
                    "4. Press 'Play Next Section' to continue",
                    "5. Use 'Restart' to begin from the start"
                )
                
                steps.forEach { step ->
                    Text(
                        text = step,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Learning Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Learning Tips",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val tips = listOf(
                    "• Use 30-60 seconds for quick concept reviews",
                    "• Try 2-5 minutes for focused study sessions",
                    "• Break long lectures into 10-minute sections",
                    "• Take notes between sections for better retention",
                    "• Review previous sections if needed"
                )
                
                tips.forEach { tip ->
                    Text(
                        text = tip,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
} 