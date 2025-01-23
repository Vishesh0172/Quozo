package com.example.quozo.presentation.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quozo.models.LoadingState
import com.example.quozo.presentation.components.BackgroundAnimation

@Composable
fun NameScreen(modifier: Modifier = Modifier, state: NameState, onEvent:(NameEvent) -> Unit, navigateToMain:() -> Unit) {

    navigateToMain()

    LaunchedEffect(state.userCreated) {
        if (state.userCreated)
            navigateToMain()
    }

    if (state.loadingState == LoadingState.Success)


        Box(modifier = Modifier.fillMaxSize()){

            BackgroundAnimation(Modifier)
            Column (modifier = modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.Start){
                Spacer(Modifier.height(12.dp))
                Text(text = "Let's Get Started", style = MaterialTheme.typography.displayLarge)



                TextField(
                    colors = TextFieldDefaults.colors(
                        errorIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 120.dp),
                    shape = RoundedCornerShape(15.dp),
                    placeholder = {Text(text = "Your Name", style = MaterialTheme.typography.bodySmall)},
                    value = state.name,
                    onValueChange = { onEvent(NameEvent.TypeName(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onEvent(NameEvent.SubmitName)
                    }
                ) {
                    Text(text = "Go", style = MaterialTheme.typography.bodyMedium)
                }

            }

        }



}