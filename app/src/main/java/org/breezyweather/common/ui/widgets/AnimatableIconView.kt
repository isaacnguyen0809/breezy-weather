package org.breezyweather.common.ui.widgets

import android.animation.Animator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Size
import androidx.appcompat.widget.AppCompatImageView
import org.breezyweather.R

class AnimatableIconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    @Size(3)
    private lateinit var mIconImageViews: Array<AppCompatImageView>

    @Size(3)
    private val mIconAnimators: Array<Animator?> = arrayOf(null, null, null)

    init {
        val attributes = context.theme
            .obtainStyledAttributes(attrs, R.styleable.AnimatableIconView, defStyleAttr, 0)
        initialize(attributes)
        attributes.recycle()
    }

    private fun initialize(attributes: TypedArray) {
        val innerMargin = attributes.getDimensionPixelSize(
            R.styleable.AnimatableIconView_inner_margins, 0
        )
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        params.setMargins(innerMargin, innerMargin, innerMargin, innerMargin)
        mIconImageViews = arrayOf(
            AppCompatImageView(context),
            AppCompatImageView(context),
            AppCompatImageView(context)
        )
        for (i in mIconImageViews.indices.reversed()) {
            addView(mIconImageViews[i], params)
        }
    }

    fun setAnimatableIcon(
        @Size(3) drawables: Array<Drawable?>,
        @Size(3) animators: Array<Animator?>
    ) {
        endAnimators()
        for (i in drawables.indices) {
            mIconImageViews[i].setImageDrawable(drawables[i])
            mIconImageViews[i].visibility = if (drawables[i] == null) GONE else VISIBLE
            mIconAnimators[i] = animators[i]
            mIconAnimators[i]?.setTarget(mIconImageViews[i])
        }
    }

    fun startAnimators() {
        for (a in mIconAnimators) {
            if (a != null && a.isStarted) {
                // animating.
                return
            }
        }
        for (i in mIconAnimators.indices) {
            if (mIconImageViews[i].visibility == VISIBLE) {
                mIconAnimators[i]?.start()
            }
        }
    }

    private fun endAnimators() {
        for (i in mIconImageViews.indices) {
            mIconAnimators[i]?.let {
                if (it.isStarted) {
                    mIconAnimators[i]?.cancel()
                }
            }
            resetView(mIconImageViews[i])
        }
    }

    private fun resetView(view: View) {
        view.alpha = 1f
        view.scaleX = 1f
        view.scaleY = 1f
        view.rotation = 0f
        view.rotationX = 0f
        view.rotationY = 0f
        view.translationX = 0f
        view.translationY = 0f
    }
}
