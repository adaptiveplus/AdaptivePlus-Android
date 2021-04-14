package com.sprintsquads.adaptiveplus.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.APFont
import com.sprintsquads.adaptiveplus.ext.applyAPFont
import com.sprintsquads.adaptiveplus.utils.processHtmlText


internal class APHtmlTextView: LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, htmlText: String, options: APFont?) : super(context) {
        orientation = VERTICAL
        init(htmlText, options)
    }

    private fun init(htmlText: String, options: APFont?) {
        var i = 0

        while (i < htmlText.length) {
            if (htmlText.startsWith("<table", i)) {
                var k = htmlText.indexOf(">", i)
                if (k == -1) k = htmlText.length else k++

                var j = htmlText.indexOf("</table>", k)
                if (j == -1) j = htmlText.length

                val tempStr = htmlText.substring(k, j)
                if (tempStr.isNotEmpty()) {
                    val apHtmlTableView = HtmlTableView(context, tempStr, options)
                    val layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    addView(apHtmlTableView, layoutParams)
                }

                i = j + 8
            }
            else {
                var j = htmlText.indexOf("<table", i)
                if (j == -1) j = htmlText.length

                val tempStr = htmlText.substring(i, j)
                if (tempStr.isNotEmpty()) {
                    val apTitleTextView = TextView(context).apply {
                        textSize = 34f
                        setTextColor(ContextCompat.getColor(context, R.color.apWhite))
                        // Setting substring
                        text = processHtmlText(tempStr)
                    }
                    options?.let { apTitleTextView.applyAPFont(it) }

                    val layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    addView(apTitleTextView, layoutParams)
                }

                i = j
            }
        }
    }

    private class HtmlTableView: LinearLayout {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

        constructor(context: Context, htmlText: String, options: APFont?) : super(context) {
            orientation = VERTICAL
            background = ContextCompat.getDrawable(context, R.drawable.ap_bg_html_table)

            val mPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0.5f, resources.displayMetrics).toInt()
            setPadding(mPadding, mPadding, mPadding, mPadding)

            init(htmlText, options)
        }

        private fun init(htmlText: String, options: APFont?) {
            var i = 0

            while (i < htmlText.length) {
                if (htmlText.startsWith("<tr", i)) {
                    var k = htmlText.indexOf(">", i)
                    if (k == -1) k = htmlText.length else k++

                    var j = htmlText.indexOf("</tr>", k)
                    if (j == -1) j = htmlText.length

                    val tempStr = htmlText.substring(k, j)
                    if (tempStr.isNotEmpty()) {
                        val apHtmlTableRowView = HtmlTableRowView(context, tempStr, options)
                        val layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        addView(apHtmlTableRowView, layoutParams)
                    }

                    i = j + 5
                }
                else {
                    var j = htmlText.indexOf("<tr", i)
                    if (j == -1) j = htmlText.length
                    i = j
                }
            }
        }
    }

    class HtmlTableRowView: LinearLayout {
        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

        constructor(context: Context, htmlText: String, options: APFont?) : super(context) {
            orientation = HORIZONTAL
            init(htmlText, options)
        }

        private fun init(htmlText: String, options: APFont?) {
            var i = 0

            while (i < htmlText.length) {
                if (htmlText.startsWith("<th", i) || htmlText.startsWith("<td", i)) {
                    var k = htmlText.indexOf(">", i)
                    if (k == -1) k = htmlText.length else k++

                    var j =
                        if (htmlText.startsWith("<th", i))
                            htmlText.indexOf("</th>", k)
                        else
                            htmlText.indexOf("</td>", k)
                    if (j == -1) j = htmlText.length

                    var tempStr = htmlText.substring(k, j)
                    if (tempStr.isNotEmpty()) {
                        if (htmlText.startsWith("<th", i))
                            tempStr = "<b>$tempStr</b>"

                        val apTitleTextView = TextView(context).apply {
                            textSize = 34f
                            setTextColor(ContextCompat.getColor(context, R.color.apWhite))
                            background = ContextCompat.getDrawable(context, R.drawable.ap_bg_html_table)

                            val mPadding = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                            setPadding(mPadding, mPadding, mPadding, mPadding)

                            // Setting substring
                            text = processHtmlText(tempStr)
                        }
                        options?.let { apTitleTextView.applyAPFont(it) }
                        apTitleTextView.gravity = Gravity.CENTER

                        val layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                        addView(apTitleTextView, layoutParams)
                    }

                    i = j + 5
                }
                else {
                    var j = htmlText.indexOf("<th", i)
                    val k = htmlText.indexOf("<td", i)
                    if (j == -1 || (k != -1 && k < j))
                        j = if (k == -1) htmlText.length else k

                    i = j
                }
            }
        }
    }
}