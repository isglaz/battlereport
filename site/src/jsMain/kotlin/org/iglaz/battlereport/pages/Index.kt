package org.iglaz.battlereport.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.dom.Text

@Page
@Composable
fun HomePage() {
    var response by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        val result = window.fetch("http://localhost:8080/").await()
        val text = result.text().await()
        response = text
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(response)
    }
}
