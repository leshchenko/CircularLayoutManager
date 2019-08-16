package com.leshchenko.circularlayoutmanager

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


fun RecyclerView.LayoutManager.layoutDecoratedWithMargins(
    view: View,
    positionData: CircularRecyclerLayoutManager.PositionData
) = with(positionData) { layoutDecoratedWithMargins(view, left, top, right, bottom) }

fun View.updatePosition(positionData: CircularRecyclerLayoutManager.PositionData) {
    left = positionData.left
    top = positionData.top
    right = positionData.right
    bottom = positionData.bottom
}

fun View.updateViewSize(scale: Double) {
    alpha = scale.toFloat()
    scaleX = if (scale > 1) 1f else scale.toFloat()
    scaleY = if (scale > 1) 1f else scale.toFloat()
}

fun Int.isDivideByTwo() = this % 2 == 0

fun cosAngle(angle: Double): Double {
    return cos(angleToRadian(angle))
}

fun sinAngle(angle: Double): Double {
    return sin(angleToRadian(angle))
}

fun angleToRadian(angle: Double) = angle * PI / 180