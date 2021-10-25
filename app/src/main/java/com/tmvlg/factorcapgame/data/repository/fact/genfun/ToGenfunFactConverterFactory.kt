package com.tmvlg.factorcapgame.data.repository.fact.genfun

import java.lang.reflect.Type
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit

/*
<div id='gencont'>
    <hr size=0 style='border-color:grey;opacity:.5;'>
    <div><p>
        The longest game of mah-jong ever lasted for three years. It finished when the house it was
            being played in burned down.
    </p></div>
    <hr size=0 style='border-color:grey;opacity:.5;'>
    <div><p>
        The national anthem of Lithuania is palindromic. It plays backwards exactly the same way
            that it plays forwards.
    </p></div>
    <hr size=0 style='border-color:grey;opacity:.5;'>
    </div>
</div>
<button id='generate2' type="submit" class="btn btn-primary">Generate Funny Fake Facts</button>
 */

class ToGenfunFactConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (GenfunFact::class.java == type) {
            Converter { value ->
                val stringBody = value.string()
                val stringBodyWithoutPrerubbish = stringBody.substring(
                    stringBody.findAnyOf(listOf("<div id='gencont'>"))!!.first
                )
                val sbwp = stringBodyWithoutPrerubbish
                val fact = sbwp.substring(
                    sbwp.findAnyOf(listOf("<div><p>"))!!.first + "<div><p>".length,
                    sbwp.findAnyOf(listOf("</p></div>"))!!.first
                ).trim()
                return@Converter GenfunFact(
                    false,
                    fact
                )
            }
        } else null
    }

    companion object {
        private val MEDIA_TYPE: MediaType = MediaType.parse("text/plain")!!
    }
}
