package com.example.individualassignment_33

import android.content.res.XmlResourceParser
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.individualassignment_33.ui.theme.IndividualAssignment_33Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import java.util.Locale
import java.util.Vector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val wordParser = resources.getXml(R.xml.typingwords)
        setContent {
            IndividualAssignment_33Theme {
                MakeScreen(parseWords(wordParser))
            }
        }
    }
}

fun parseWords(parser: XmlResourceParser): Vector<String> {
    var words = Vector<String>()
    while (parser.eventType != XmlPullParser.END_DOCUMENT) {
        if (parser.eventType == XmlPullParser.START_TAG) {
            when(parser.name) {
                "word" -> words.add(parser.nextText())
            }
        }
        parser.next()
    }
    return words
}

fun getWordIndex(size: Int): Int {
    return (0..size-1).shuffled()[0]
}

fun makeWordList(size: Int): Vector<MutableState<Int>> {
    var wordIndices = Vector<MutableState<Int>>(0)
    for(i in 1..5){
        wordIndices.add(mutableStateOf(getWordIndex(size)))
    }
    return wordIndices
}

@Composable
fun MakeScreen(words: Vector<String>) {
    var totalWords by remember { mutableStateOf(0f) }

    var totalTime by remember { mutableStateOf(0.1f) }  //total time elapsed in seconds
    val timeToReset = 5 //the number of seconds before a word is swapped
    var currentTime by remember { mutableStateOf(0f) } //current time since reset in ms
    var currentText by remember { mutableStateOf("") }

//    var wordIndex by remember {
//        mutableStateOf(getWordIndex(words.size))
//    }

    var wordIndices = makeWordList(words.size)

    LaunchedEffect(Unit) {
        while(true){
            delay(100)
            currentTime += 100
            if(currentTime>=(timeToReset*1000f)){
                //Log.d("test", "now")
                totalTime += currentTime/1000
                currentTime = 0f
                //wordIndex = getWordIndex(words.size)
                wordIndices = makeWordList(words.size)
                currentText = ""
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val coroutineScope = rememberCoroutineScope()
            Text(
                text = "Speed Typing!",
                fontSize = 30.sp
            )
            val wpm = (totalWords/totalTime)*60
            Text(
                text = String.format(Locale.ENGLISH,"Words per minute: %.3f", wpm),
                fontSize = 20.sp
            )
            Spacer(Modifier.size(20.dp))
            Text(
                text = "type the following:",
                fontSize = 10.sp
            )
//            Text(
//                text = words[wordIndex],
//                fontSize = 30.sp
//            )
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(wordIndices.size) {i ->
                    val wordIndex = wordIndices[i]
                    Text(
                        text = words[wordIndex.value],
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(Modifier.size(20.dp))
            TextField(
                value = currentText,
                onValueChange = {
                    currentText = it
                    coroutineScope.launch(Dispatchers.Main) {
//                        if (currentText == words[wordIndex]) {
//                            totalTime += currentTime / 1000
//                            currentTime = 0f
//                            totalWords++
//                            wordIndex = getWordIndex(words.size)
//                            currentText = ""
//                        }
                        for(i in 0..(wordIndices.size-1)) {
                            if(currentText == words[wordIndices[i].value]) {
                                totalTime += currentTime / 1000
                                currentTime = 0f
                                totalWords++
                                wordIndices[i].value = getWordIndex(words.size)
                                currentText = ""
                            }
                        }
                    }
                }
            )
            Spacer(Modifier.size(200.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IndividualAssignment_33Theme {
    }
}