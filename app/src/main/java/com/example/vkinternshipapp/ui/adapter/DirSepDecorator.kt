package com.example.vkinternshipapp.ui.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vkinternshipapp.R


class DirSepDecorator(
    context: Context,
    @DrawableRes resId: Int
) : RecyclerView.ItemDecoration() {
    private val divider: Drawable? = ContextCompat.getDrawable(context, resId)?.apply {
        setTint(context.getColor(R.color.dark_gray))
    }
    private var size: Int = 60

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        val top = (parent.height - size) / 2 + parent.paddingTop
        val bottom = parent.height - top - parent.paddingBottom

        val childCount = parent.childCount
        if (childCount == 1) return
        for (i in 1 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + size
            divider?.setBounds(left, top, right, bottom)
            divider?.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.right = size
        }
    }


}