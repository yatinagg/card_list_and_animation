package com.yatinagg.animationmultiplecards

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*


const val MIN_SWIPE_DISTANCE = -500

class CustomLayout constructor(private val frameLayout: FrameLayout) : AppCompatActivity() {

    private var x: Float = 0F
    private var y: Float = 0F
    lateinit var cardList: MutableList<Card>
    lateinit var cardViewList: MutableList<CardView>
    private var currentCardIndex: Int = 0
    private var currentElevation = 100
    private var cardDiff = 20
    private lateinit var cardValueYList: MutableList<Float>
    private val top = 300

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(56), rnd.nextInt(256))
    }

    private fun getRandomGradient(): GradientDrawable {
        val colors = IntArray(5)
        colors[0] = getRandomColor()
        colors[1] = getRandomColor()
        colors[2] = getRandomColor()
        colors[3] = getRandomColor()
        colors[4] = getRandomColor()

        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, colors)

        gd.gradientType = GradientDrawable.LINEAR_GRADIENT
        return gd
    }

    fun setCardViews(cardList: MutableList<Card>) {
        this.currentCardIndex = 0
        this.cardList = cardList
        val frameLayout = frameLayout
        val padding = 40
        this.cardViewList = mutableListOf()
        cardDiff = 150 / (cardList.size)

//        frameLayout.background = ContextCompat.getDrawable(frameLayout.context, R.drawable.gradient)
        this.cardList.forEachIndexed { index, card ->
            val cardView = CardView(frameLayout.context)
            cardView.tag = index.toFloat()
            cardViewList.add(cardView)
            if (cardList[index].gradientDrawable == null)
                cardList[index].gradientDrawable = getRandomGradient()
            println("cardView ${cardView.id}")
            cardView.elevation = currentElevation.toFloat()
            currentElevation -= 1
            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            layoutParams.setMargins(50, top, 50, 150)
            cardView.layoutParams = layoutParams
            println("padding $padding")
            cardView.setPadding(padding, padding, padding, padding)
            cardView.setCardBackgroundColor(card.color)
            cardView.radius = 50f
            cardView.useCompatPadding = true
            frameLayout.addView(cardView)
            println("frame$frameLayout")
            cardView.y -= cardDiff * index


            val textView = TextView(cardView.context)

            textView.text = card.text
            val layoutParams1 = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            textView.textSize = 30F
            textView.gravity = Gravity.CENTER
            textView.layoutParams = layoutParams1
            textView.setTextColor(Color.WHITE)
            cardView.addView(textView)

            val displayMetrics = frameLayout.context.resources.displayMetrics
            val cardWidth = cardView.width
            val cardHeight = cardView.height
            x = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)
            y = (displayMetrics.heightPixels.toFloat() / 2) - (cardHeight / 2)
        }
        cardValueYList = mutableListOf()
        var curr = top.toFloat()
        println("curr $curr")
        for(i in 0 until cardList.size){
            cardValueYList.add(curr)
            curr -= cardDiff
        }
        frameLayout.background = cardList[0].gradientDrawable
        setListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setListener() {
        var currentCard = cardViewList[currentCardIndex]
        var rotation = 0F
        val translationCardLeft = ValueAnimator.ofFloat(0f, -1000f).apply {
            duration = 500
            addUpdateListener { updatedAnimation ->
                currentCard.translationX = updatedAnimation.animatedValue as Float
            }
            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        if (rotation != 0f && rotation != -90f)
                            currentCard.rotation = rotation
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        currentCard.visibility = View.GONE
                        currentCard.x = 200f
                        currentCard.y = 300f
                        currentCard.rotation = 290f
                    }
                }
            )
        }

        val rotationCardLeft = ValueAnimator.ofFloat(-90f, 0f).apply {
            duration = 500
            addUpdateListener { updatedAnimation ->
                cardViewList.forEachIndexed { index, cardView ->
                    if (index != currentCardIndex) {
                        cardView.y =
                            cardValueYList[(index - currentCardIndex + cardList.size) % cardList.size] + (((updatedAnimation.animatedValue as Float - (-90f) + 1) / (0f - (-90f) + 1)) * cardDiff)
                    } else {
                        cardView.y =
                            cardValueYList[(index - currentCardIndex + cardList.size) % cardList.size] - (((updatedAnimation.animatedValue as Float - (-90f) + 1) / (0f - (-90f) + 1)) * ((cardViewList.size - 1) * cardDiff))
                    }
                }
                currentCard.translationX = updatedAnimation.animatedValue as Float
                currentCard.rotation = updatedAnimation.animatedValue as Float
            }
            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        val transitionDrawable =
                            TransitionDrawable(arrayOf(cardList[currentCardIndex].gradientDrawable,
                                cardList[(currentCardIndex + 1) % cardList.size].gradientDrawable))
                        frameLayout.background = transitionDrawable
                        transitionDrawable.startTransition(1000)

                        println("animate start rotate left")
                        currentCard.pivotX = 300f
                        currentCard.pivotY = currentCard.height.toFloat() / 2 + 500
                        currentCard.visibility = View.VISIBLE
                        currentCard.cardElevation = currentElevation.toFloat()
                        currentElevation -= 1
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        currentCardIndex = (currentCardIndex + 1) % cardList.size
                        currentCard = cardViewList[currentCardIndex]
                        currentCard.isClickable = true
                    }
                }
            )
        }

        val translationCardRight = ValueAnimator.ofFloat(0f, 1000f).apply {
            duration = 500
            addUpdateListener { updatedAnimation ->
                currentCard.translationX = updatedAnimation.animatedValue as Float
            }
            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        currentCard.visibility = View.VISIBLE
                        if (rotation != 90f)
                            currentCard.rotation = rotation
                        currentCard.alpha = 1f
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        currentCard.visibility = View.GONE
                        currentCard.x = 50f
                        currentCard.y = 300f
                        currentCard.rotation = 90f
                    }
                })
        }

        val rotationCardRight = ValueAnimator.ofFloat(90f, 0f).apply {
            duration = 500
            addUpdateListener { updatedAnimation ->
                cardViewList.forEachIndexed { index, cardView ->
                    if (index != currentCardIndex) {
                        cardView.y =
                            cardValueYList[(index - currentCardIndex + cardList.size) % cardList.size] + (((updatedAnimation.animatedValue as Float - (90f) + 1) / (0f - (90f) + 1)) * cardDiff)
                    } else {
                        cardView.y =
                            cardValueYList[(index - currentCardIndex + cardList.size) % cardList.size] - (((updatedAnimation.animatedValue as Float - (90f) + 1) / (0f - (90f) + 1)) * ((cardViewList.size - 1) * cardDiff))
                    }
                }
                currentCard.rotation = updatedAnimation.animatedValue as Float
                currentCard.alpha = 1 - ((updatedAnimation.animatedValue as Float) / (90f))
            }
            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        val transitionDrawable =
                            TransitionDrawable(arrayOf(cardList[currentCardIndex].gradientDrawable,
                                cardList[(currentCardIndex + 1) % cardList.size].gradientDrawable))
                        frameLayout.background = transitionDrawable
                        transitionDrawable.startTransition(1000)
//                        frameLayout.background = cardList[(currentCardIndex + 1) % cardList.size].gradientDrawable
                        currentCard.pivotX = currentCard.width.toFloat() - 300f
                        currentCard.pivotY = currentCard.height.toFloat() / 2 + 500f
                        currentCard.visibility = View.VISIBLE
                        currentCard.cardElevation = currentElevation.toFloat()
                        currentElevation -= 1
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        currentCardIndex = (currentCardIndex + 1) % cardList.size
                        currentCard = cardViewList[currentCardIndex]
                        currentCard.isClickable = true
                        rotation = 0f
                        currentCard.rotation = 0f
                    }
                }
            )
        }

        val bouncerLeft = AnimatorSet().apply {
            play(translationCardLeft)
            play(rotationCardLeft).after(translationCardLeft)
            // before with after
        }

        val bouncerRight = AnimatorSet().apply {
            play(translationCardRight)
            play(rotationCardRight).after(translationCardRight)
            // before with after
        }

        val rotationLeftHalfSwipe = ValueAnimator.ofFloat(-20f, 0f).apply {
            duration = 500
            addUpdateListener { updatedAnimation ->
                currentCard.rotation = updatedAnimation.animatedValue as Float
                interpolator = OvershootInterpolator()
            }
            addListener(
                object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        rotation = 0f
                        currentCard.isClickable = true
                    }
                }
            )
        }

        val rotationRightHalfSwipe = ValueAnimator.ofFloat(20f, 0f).apply {
            duration = 500
            addUpdateListener { updatedAnimation ->
                currentCard.rotation = updatedAnimation.animatedValue as Float
                interpolator = OvershootInterpolator()
            }
            addListener(
                object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        rotation = 0f
                        currentCard.isClickable = true
                    }
                }
            )
        }


        val overshootLeft = AnimatorSet().apply {
            play(rotationLeftHalfSwipe)
        }


        val overShootRight = AnimatorSet().apply {
            play(rotationRightHalfSwipe)
        }

        currentCard.setOnClickListener {
            Log.d("swipe", "clicked")
        }

        cardViewList.forEach {
            var newX = 10000
            it.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        println("currentX ${event.rawX} $newX")
                        when {
                            // full left swipe
                            newX != 10000 && event.rawX - newX < MIN_SWIPE_DISTANCE -> {
                                currentCard.isClickable = false
                                startAnimation(bouncerLeft)
                                rotation = 0f
                            }
                            // right full swipe
                            newX != 10000 && newX - event.rawX < MIN_SWIPE_DISTANCE -> {
                                currentCard.isClickable = false
                                startAnimation(bouncerRight)
                                rotation = 0f
                            }
                            // right not full swipe
                            newX != 10000 && newX < event.rawX -> {
                                println("not full right swipe")
                                currentCard.isClickable = false
                                startAnimation(overShootRight)
                            }
                            // left not full swipe
                            newX != 10000 && newX > event.rawX -> {
                                println("not full left swipe")
                                currentCard.isClickable = false
                                startAnimation(overshootLeft)
                            }
                            else -> {
                                rotation = 0F
                                currentCard.rotation = rotation
                            }
                        }
                        newX = 10000
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (newX == 10000)
                            newX = event.rawX.toInt()

                        if (event.rawX - newX < 0) {
                            currentCard.pivotX = 0f
                            currentCard.pivotY = 1.5f * y
                            rotation -= 1
                            currentCard.rotation = rotation
                        } else {
                            currentCard.pivotX = x*1.5f
                            currentCard.pivotY = 1.5f * y
                            rotation += 1
                            currentCard.rotation = rotation
                        }
//                        Log.d("swipe2", "move")
                    }
                }
                view.performClick()
            }
        }
    }

    private fun startAnimation(bouncer: AnimatorSet) {
        AnimatorSet().apply {
            play(bouncer)
            start()
        }
    }
}