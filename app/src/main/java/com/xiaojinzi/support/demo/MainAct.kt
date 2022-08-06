package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.architecture.mvvm1.UseCaseCheck
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.ActivityFlag
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

@ActivityFlag(
    value = ["test", "test1"],
)
class MainAct : BaseAct<MainViewModel>() {

    private val mainScope = MainScope()

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
        }

    }

}

@Composable
fun testContent() {
    Box(
        modifier = Modifier
            .padding(horizontal = 50.dp, vertical = 50.dp)
            .width(200.dp)
            .height(200.dp)
            .background(Color.LightGray)
            .nothing(),
        contentAlignment = Alignment.CenterEnd,
    ) {

        var parentSize by remember {
            mutableStateOf(
                IntSize(0, 0)
            )
        }
        var selfSize by remember {
            mutableStateOf(
                IntSize(0, 0)
            )
        }
        var inParentOffset by remember {
            mutableStateOf(
                Offset.Zero
            )
        }
        var isDragging by remember {
            mutableStateOf(
                false
            )
        }

        var offsetX by remember { mutableStateOf(0) }
        var offsetY by remember { mutableStateOf(0) }

        val offsetAnimX by animateIntAsState(targetValue = offsetX)
        val offsetAnimY by animateIntAsState(targetValue = offsetY)

        println("test ----------------------")

        println("test parentSize = $parentSize")
        println("test selfSize = $selfSize")
        println("test inParentOffset = $inParentOffset")
        println("test offsetX = $offsetX")
        println("test offsetY = $offsetY")

        LaunchedEffect(key1 = isDragging) {
            delay(400)
            if (!isDragging) {
                offsetX += if (inParentOffset.x + selfSize.width / 2f > parentSize.width / 2f) {
                    (parentSize.width - selfSize.width - inParentOffset.x)
                } else {
                    -inParentOffset.x
                }.roundToInt()
            }
        }

        Box(
            Modifier
                .size(100.dp)
                .offset {
                    IntOffset(
                        if (isDragging) offsetX else offsetAnimX,
                        if (isDragging) offsetY else offsetAnimY,
                    )
                }
                .onGloballyPositioned { coordinates ->
                    selfSize = coordinates.size
                    parentSize = coordinates.parentLayoutCoordinates?.size ?: IntSize(0, 0)
                    inParentOffset = coordinates.positionInParent()
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                    ) { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX += dragAmount.x
                            .coerceIn(
                                minimumValue = -inParentOffset.x,
                                maximumValue = (parentSize.width - selfSize.width - inParentOffset.x),
                            )
                            .roundToInt()
                        offsetY += dragAmount.y
                            .coerceIn(
                                minimumValue = -inParentOffset.y,
                                maximumValue = (parentSize.height - selfSize.height - inParentOffset.y),
                            )
                            .roundToInt()
                    }
                }
                .background(Color.DarkGray)
        )
    }
}

@Composable
fun testContent2() {
    Button(onClick = {
        UseCaseCheck.printUseCase()
    }) {
        Text("test")
    }
}