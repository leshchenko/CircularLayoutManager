package com.leshchenko.circularlayoutmanagerlib

import android.graphics.PointF
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CircularRecyclerLayoutManager(
        private val itemsPerCircle: Int = 6,
        private var anglePerItem: Double = Double.NaN,
        private var firstCircleRadius: Double = Double.NaN,
        private var angleStepForCircles: Double = Double.NaN,
        private val canScrollHorizontally: Boolean = false
) : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams() =
            RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT
            )

    override fun canScrollHorizontally() = canScrollHorizontally

    override fun canScrollVertically() = true

    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        child.measure(
                View.MeasureSpec.makeMeasureSpec(widthUsed, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(heightUsed, View.MeasureSpec.EXACTLY)
        )
    }

    private lateinit var centerPoint: PointF

    private var itemWidth = 0

    private val viewCalculation = SparseArray<ItemData>(itemCount)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        calculateConstants()
        for (position in 0 until itemCount) {
            fillAndLayoutItem(position, recycler)
        }
    }

    private fun fillAndLayoutItem(position: Int, recycler: RecyclerView.Recycler?) {

        val circleOrderPosition = position / itemsPerCircle

        val circleRadius = firstCircleRadius + (itemWidth * 1.5 * circleOrderPosition)

        val angleStep = if (angleStepForCircles.isNaN()) anglePerItem.div(2) else angleStepForCircles

        val angle = (anglePerItem * position) + if (circleOrderPosition.isDivideByTwo()) 0.0 else angleStep

        val positionData = calculatePosition(circleRadius, angle)

        viewCalculation.put(position, ItemData(circleRadius, circleRadius, angle))

        if (isViewVisible(positionData)) {
            recycler?.getViewForPosition(position)?.let { viewForPosition ->
                addView(viewForPosition)
                measureChildWithMargins(viewForPosition, itemWidth, itemWidth)
                layoutDecoratedWithMargins(viewForPosition, positionData)
            }
        }
    }

    private fun calculatePosition(radius: Double, angle: Double): PositionData {
        val xCoordinate = (radius * sinAngle(angle)) + centerPoint.x
        val yCoordinate = (radius * cosAngle(angle)) + centerPoint.y
        val top = yCoordinate - (itemWidth / 2)
        val left = xCoordinate - (itemWidth / 2)
        val right = left + itemWidth
        val bottom = top + itemWidth
        return PositionData(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }


    private fun isViewVisible(positionData: PositionData): Boolean {
        return when {
            positionData.left <= (-itemWidth) -> false
            positionData.right >= width + itemWidth -> false
            positionData.top <= (-itemWidth) -> false
            positionData.bottom >= height + itemWidth -> false
            else -> true
        }
    }

    override fun scrollVerticallyBy(
            dy: Int,
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
    ): Int {
        updateCalculation(dy)
        updateViews(recycler)
        return dy
    }

    private fun updateViews(recycler: RecyclerView.Recycler?) {
        val updatedPositions = mutableListOf<Int>()
        val viewsForDetaching = mutableListOf<View>()

        updateAllChild(viewsForDetaching, updatedPositions)

        for (position in 0 until itemCount) {
            if (updatedPositions.contains(position)) continue
            val data = viewCalculation[position]
            val positionData = calculatePosition(data.currentRadius, data.angle)
            layoutItemIfNeeded(positionData, data, recycler, position)
        }

        recycler?.let { viewsForDetaching.forEach { detachAndScrapView(it, recycler) } }
        viewsForDetaching.clear()
    }

    private fun layoutItemIfNeeded(
            positionData: PositionData,
            data: ItemData,
            recycler: RecyclerView.Recycler?,
            position: Int
    ) {
        if (isViewVisible(positionData) && data.currentRadius > 0.0) {
            recycler?.getViewForPosition(position)?.let { viewForPosition ->
                addView(viewForPosition)
                viewForPosition.updateViewSize(data.currentRadius / firstCircleRadius)
                measureChildWithMargins(viewForPosition, itemWidth, itemWidth)
                layoutDecoratedWithMargins(viewForPosition, positionData)
            }
        }
    }

    private fun updateAllChild(
            viewsForDetaching: MutableList<View>,
            updatedPositions: MutableList<Int>
    ) {
        for (position in 0 until childCount) {
            getChildAt(position)?.let { childAt ->
                val childPosition = getPosition(childAt)
                val data = viewCalculation[childPosition]
                val positionData = calculatePosition(data.currentRadius, data.angle)
                childAt.updateViewSize(data.currentRadius / firstCircleRadius)

                if (isViewVisible(positionData).not() || data.currentRadius == 0.0) {
                    viewsForDetaching.add(childAt)
                } else {
                    childAt.updatePosition(positionData)
                }
                updatedPositions.add(childPosition)
            }
        }
    }

    private fun updateCalculation(dy: Int) {
        for (position in 0 until viewCalculation.size()) {
            if (shouldItemMove(position).not()) {
                continue
            }
            val data = viewCalculation.get(position)
            data?.currentRadius = data.currentRadius + dy * 0.2
            if (data.currentRadius < 0) data.currentRadius = 0.0
            if (data.currentRadius > data.initialRadius) data.currentRadius = data.initialRadius
        }
    }

    private fun shouldItemMove(index: Int): Boolean {
        val itemCurrentRadius = viewCalculation.get(index)?.currentRadius
        val circleNumber = index / itemsPerCircle
        for (position in 0 until itemCount) {
            val currentRadius = viewCalculation.get(position)?.currentRadius ?: 0.0
            val isNextCircle = position / itemsPerCircle > circleNumber
            val isRadiusBigEnough = currentRadius <= (firstCircleRadius + itemWidth / 2)

            if (isNextCircle && isRadiusBigEnough && itemCurrentRadius == 0.0) {
                return false
            }
        }
        return true
    }

    private fun calculateConstants() {
        centerPoint = PointF(width / 2f, height / 2f)
        if (firstCircleRadius.isNaN()) {
            firstCircleRadius = width / 4.0
        }
        val firstCircleLength = 2 * Math.PI * firstCircleRadius
        itemWidth = ((firstCircleLength / (itemsPerCircle)) * 0.4).toInt()
        if (anglePerItem.isNaN()) {
            anglePerItem = 360 / if (itemCount > itemsPerCircle) itemsPerCircle.toDouble() else itemCount.toDouble()
        }
    }

    override fun scrollHorizontallyBy(
            dx: Int,
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
    ): Int {
        for (position in 0 until viewCalculation.size()) {
            if (shouldItemMove(position).not()) {
                continue
            }
            viewCalculation.get(position).angle += dx * 0.1
        }
        updateViews(recycler)
        return dx
    }

    inner class ItemData(val initialRadius: Double, var currentRadius: Double, var angle: Double)
    inner class PositionData(val left: Int, val top: Int, val right: Int, val bottom: Int)
}