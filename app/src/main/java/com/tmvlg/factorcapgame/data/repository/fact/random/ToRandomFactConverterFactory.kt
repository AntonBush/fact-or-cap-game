package com.tmvlg.factorcapgame.data.repository.fact.random

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/*
<div id='z'>Fact.
                    <br/>
                    <br/>
*/

class ToRandomFactConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (RandomFact::class.java == type) {
            Converter { value ->
                val stringBody = value.string()
                val stringBodyWithoutPrerubbish = stringBody.substring(
                    stringBody.findAnyOf(listOf("<div id='z'>"))!!.first
                )
                val sbwp = stringBodyWithoutPrerubbish
                val fact = sbwp.substring(
                    "<div id='z'>".length,
                    sbwp.findAnyOf(listOf("<br/>"))!!.first
                ).trim()
                return@Converter RandomFact(
                    true,
                    fact
                )
            }
        } else null
    }

//    companion object {
//        private val MEDIA_TYPE: MediaType = MediaType.parse("text/plain")!!
//    }
}
