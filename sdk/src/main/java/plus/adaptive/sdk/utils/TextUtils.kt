package plus.adaptive.sdk.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.text.Editable
import android.text.Html
import android.text.Spanned
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import org.xml.sax.XMLReader
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APFont


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

private val apFontMap = mutableMapOf<String, Typeface>()

internal fun requestFontDownload(
    context: Context,
    familyName: String,
    fontStyle: APFont.Style?,
    onSuccess: (typeface: Typeface) -> Unit,
    onError: () -> Unit
) {
    val fontMapKey = "${familyName}:${fontStyle}"

    apFontMap[fontMapKey]?.let {
        onSuccess.invoke(it)
        return@requestFontDownload
    }

    val queryBuilder = QueryBuilder(familyName, fontStyle)
    val query = queryBuilder.build()

    val request = FontRequest(
        "com.google.android.gms.fonts",
        "com.google.android.gms",
        query,
        R.array.com_google_android_gms_fonts_certs)

    val callback = object : FontsContractCompat.FontRequestCallback() {

        override fun onTypefaceRetrieved(typeface: Typeface) {
            apFontMap[fontMapKey] = typeface
            onSuccess.invoke(typeface)
        }

        override fun onTypefaceRequestFailed(reason: Int) {
            onError.invoke()
        }
    }

    val handlerThread = HandlerThread("fonts")
    handlerThread.start()
    val mHandler = Handler(handlerThread.looper)

    FontsContractCompat
        .requestFont(context, request, callback, mHandler)
}

private class QueryBuilder(
    val familyName: String,
    val fontStyle: APFont.Style?
) {

    fun build(): String {
        if (fontStyle == null || fontStyle == APFont.Style.REGULAR) {
            return familyName
        }

        val builder = StringBuilder()
        builder.append("name=").append(familyName)

        when (fontStyle) {
            APFont.Style.THIN -> {
                builder.append("&weight=").append(100)
            }
            APFont.Style.THIN_ITALIC -> {
                builder.append("&weight=").append(100)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.EXTRA_LIGHT -> {
                builder.append("&weight=").append(200)
            }
            APFont.Style.EXTRA_LIGHT_ITALIC -> {
                builder.append("&weight=").append(200)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.LIGHT -> {
                builder.append("&weight=").append(300)
            }
            APFont.Style.LIGHT_ITALIC -> {
                builder.append("&weight=").append(300)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.REGULAR -> { }
            APFont.Style.REGULAR_ITALIC -> {
                builder.append("&italic=").append(1f)
            }
            APFont.Style.MEDIUM -> {
                builder.append("&weight=").append(500)
            }
            APFont.Style.MEDIUM_ITALIC -> {
                builder.append("&weight=").append(500)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.SEMIBOLD -> {
                builder.append("&weight=").append(600)
            }
            APFont.Style.SEMIBOLD_ITALIC -> {
                builder.append("&weight=").append(600)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.BOLD -> {
                builder.append("&weight=").append(700)
            }
            APFont.Style.BOLD_ITALIC -> {
                builder.append("&weight=").append(700)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.EXTRA_BOLD -> {
                builder.append("&weight=").append(800)
            }
            APFont.Style.EXTRA_BOLD_ITALIC -> {
                builder.append("&weight=").append(800)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.BLACK -> {
                builder.append("&weight=").append(900)
            }
            APFont.Style.BLACK_ITALIC -> {
                builder.append("&weight=").append(900)
                builder.append("&italic=").append(1f)
            }
        }

        builder.append("&besteffort=").append(true)

        return builder.toString()
    }
}