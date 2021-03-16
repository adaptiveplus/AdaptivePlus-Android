package com.sprintsquads.adaptiveplus.utils

import android.text.Editable
import android.text.Html
import android.text.Spanned
import org.xml.sax.XMLReader


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