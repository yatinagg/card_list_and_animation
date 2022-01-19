package com.yatinagg.animationmultiplecards

import android.graphics.drawable.GradientDrawable

data class Card(val text: String, val color: Int, var gradientDrawable: GradientDrawable? = null)