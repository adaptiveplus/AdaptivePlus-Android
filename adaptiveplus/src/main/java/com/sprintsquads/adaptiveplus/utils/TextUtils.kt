package com.sprintsquads.adaptiveplus.utils

import android.text.Editable
import android.text.Html
import android.text.Spanned
import org.xml.sax.XMLReader


//internal fun formatTextView(textView: TextView, options: APTextComponent) {
//    options.fontSize?.let {
//        textView.textSize = it.toFloat()
//    }
//    options.color?.let {
//        textView.setTextColor(getColorFromHex(it))
//    }
//
//    textView.typeface = getTypeface(
//        textView.context, options.fontWeight, options.fontStyle)
//
//    options.textAlign?.let {
//        textView.gravity = when (it) {
//            APTextComponent.TextAlign.LEFT -> Gravity.START
//            APTextComponent.TextAlign.CENTER -> Gravity.CENTER
//            APTextComponent.TextAlign.RIGHT -> Gravity.END
//        }
//    }
//}

internal fun processHtmlText(htmlStr: String): Spanned {
    val myHtmlStr = htmlStr
        .replace("<ul", "<s10s-ul")
        .replace("</ul>", "</s10s-ul>")
        .replace("<ol", "<s10s-ol")
        .replace("</ol>", "</s10s-ol>")
        .replace("<li", "<s10s-li")
        .replace("</li>", "</s10s-li>")
        .replace("\n", "<br/>")

    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(myHtmlStr, Html.FROM_HTML_MODE_LEGACY, null, CustomTagHandler())
    } else {
        Html.fromHtml(myHtmlStr, null, CustomTagHandler())
    }
}

private class CustomTagHandler: Html.TagHandler {

    private var parent: String? = null
    private var orderIndex = 1


    override fun handleTag(
        opening: Boolean,
        tag: String?,
        output: Editable?,
        xmlReader: XMLReader?
    ) {
        if (tag in listOf("s10s-ul", "s10s-ol")) {
            parent = if (opening) tag else null
        }

        if (tag.equals("s10s-ol") && opening) {
            orderIndex = 1
        }

        if (tag.equals("s10s-li")) {
            if (parent.equals("s10s-ul") && opening) {
                output?.append("\n\t• ")
            } else if (parent.equals("s10s-ol") && opening) {
                output?.append("\n\t$orderIndex. ")
                orderIndex++
            }
        }
    }
}

//internal class HtmlTextView: LinearLayout {
//
//    constructor(context: Context) : super(context)
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//    constructor(context: Context, htmlText: String, options: AdaptiveTagTemplate.TextDetails.Options?) : super(context) {
//        orientation = VERTICAL
//        init(htmlText, options)
//    }
//
//    private fun init(htmlText: String, options: AdaptiveTagTemplate.TextDetails.Options?) {
//        var i = 0
//
//        while (i < htmlText.length) {
//            if (htmlText.startsWith("<table", i)) {
//                var k = htmlText.indexOf(">", i)
//                if (k == -1) k = htmlText.length else k++
//
//                var j = htmlText.indexOf("</table>", k)
//                if (j == -1) j = htmlText.length
//
//                val tempStr = htmlText.substring(k, j)
//                if (tempStr.isNotEmpty()) {
//                    val apHtmlTableView = HtmlTableView(context, tempStr, options)
//                    val layoutParams = LayoutParams(
//                        LayoutParams.MATCH_PARENT,
//                        LayoutParams.WRAP_CONTENT
//                    )
//                    addView(apHtmlTableView, layoutParams)
//                }
//
//                i = j + 8
//            }
//            else {
//                var j = htmlText.indexOf("<table", i)
//                if (j == -1) j = htmlText.length
//
//                val tempStr = htmlText.substring(i, j)
//                if (tempStr.isNotEmpty()) {
//                    val apTitleTextView = TextView(context).apply {
//                        textSize = 34f
//                        setTextColor(ContextCompat.getColor(context, R.color.apWhite))
//                        // Setting substring
//                        text = processHtmlText(tempStr)
//                    }
//                    options?.let { formatTextView(apTitleTextView, it) }
//
//                    val layoutParams = LayoutParams(
//                        LayoutParams.MATCH_PARENT,
//                        LayoutParams.WRAP_CONTENT
//                    )
//                    addView(apTitleTextView, layoutParams)
//                }
//
//                i = j
//            }
//        }
//    }
//
//    class HtmlTableView: LinearLayout {
//        constructor(context: Context) : super(context)
//        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//        constructor(context: Context, htmlText: String, options: AdaptiveTemplate.TextDetails.Options?) : super(context) {
//            orientation = VERTICAL
//            background = ContextCompat.getDrawable(context, R.drawable.ap_bg_html_table)
//
//            val mPadding = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 0.5f, resources.displayMetrics).toInt()
//            setPadding(mPadding, mPadding, mPadding, mPadding)
//
//            init(htmlText, options)
//        }
//
//        private fun init(htmlText: String, options: AdaptiveTemplate.TextDetails.Options?) {
//            var i = 0
//
//            while (i < htmlText.length) {
//                if (htmlText.startsWith("<tr", i)) {
//                    var k = htmlText.indexOf(">", i)
//                    if (k == -1) k = htmlText.length else k++
//
//                    var j = htmlText.indexOf("</tr>", k)
//                    if (j == -1) j = htmlText.length
//
//                    val tempStr = htmlText.substring(k, j)
//                    if (tempStr.isNotEmpty()) {
//                        val apHtmlTableRowView = HtmlTableRowView(context, tempStr, options)
//                        val layoutParams = LayoutParams(
//                            LayoutParams.MATCH_PARENT,
//                            LayoutParams.WRAP_CONTENT
//                        )
//                        addView(apHtmlTableRowView, layoutParams)
//                    }
//
//                    i = j + 5
//                }
//                else {
//                    var j = htmlText.indexOf("<tr", i)
//                    if (j == -1) j = htmlText.length
//                    i = j
//                }
//            }
//        }
//    }
//
//    class HtmlTableRowView: LinearLayout {
//        constructor(context: Context) : super(context)
//        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//        constructor(context: Context, htmlText: String, options: AdaptiveTemplate.TextDetails.Options?) : super(context) {
//            orientation = HORIZONTAL
//            init(htmlText, options)
//        }
//
//        private fun init(htmlText: String, options: AdaptiveTemplate.TextDetails.Options?) {
//            var i = 0
//
//            while (i < htmlText.length) {
//                if (htmlText.startsWith("<th", i) || htmlText.startsWith("<td", i)) {
//                    var k = htmlText.indexOf(">", i)
//                    if (k == -1) k = htmlText.length else k++
//
//                    var j =
//                        if (htmlText.startsWith("<th", i))
//                            htmlText.indexOf("</th>", k)
//                        else
//                            htmlText.indexOf("</td>", k)
//                    if (j == -1) j = htmlText.length
//
//                    var tempStr = htmlText.substring(k, j)
//                    if (tempStr.isNotEmpty()) {
//                        if (htmlText.startsWith("<th", i))
//                            tempStr = "<b>$tempStr</b>"
//
//                        val apTitleTextView = TextView(context).apply {
//                            textSize = 34f
//                            setTextColor(ContextCompat.getColor(context, R.color.apWhite))
//                            background = ContextCompat.getDrawable(context, R.drawable.ap_bg_html_table)
//
//                            val mPadding = TypedValue.applyDimension(
//                                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
//                            setPadding(mPadding, mPadding, mPadding, mPadding)
//
//                            // Setting substring
//                            text = processHtmlText(tempStr)
//                        }
//                        options?.let { formatTextView(apTitleTextView, it) }
//                        apTitleTextView.gravity = Gravity.CENTER
//
//                        val layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
//                        addView(apTitleTextView, layoutParams)
//                    }
//
//                    i = j + 5
//                }
//                else {
//                    var j = htmlText.indexOf("<th", i)
//                    val k = htmlText.indexOf("<td", i)
//                    if (j == -1 || (k != -1 && k < j))
//                        j = if (k == -1) htmlText.length else k
//
//                    i = j
//                }
//            }
//        }
//    }
//}