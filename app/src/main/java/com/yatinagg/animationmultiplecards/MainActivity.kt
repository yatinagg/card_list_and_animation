package com.yatinagg.animationmultiplecards

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cardList: MutableList<Card>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardList = mutableListOf(Card(text = "Card 1", color = Color.BLUE,
            gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.argb(255, 255, 56, 255),
                    Color.argb(255, 56, 56, 255)))))
        cardList.add(Card(text = "Card 2", color = Color.GREEN))
        cardList.add(Card(text = "Card 3", color = Color.MAGENTA))
        cardList.add(Card(text = "Card 4", color = Color.YELLOW))
        cardList.add(Card(text = "Card 5", color = Color.CYAN))

        val customLayout = CustomLayout(findViewById(R.id.frame_layout))
        customLayout.setCardViews(cardList)

    }
}