package com.tmvlg.factorcapgame.data.repository.fact.genfun

import com.tmvlg.factorcapgame.data.repository.fact.FactApiService
import retrofit2.http.GET

internal const val factsCount = 1
internal const val url = ("/code/model/generatorcontent.php/?recordtable=generator&recordkey=895"
    + "&gen=Y&itemnumber=$factsCount"
    + "&randomoption=undefined&genimage=No&geneditor=No&nsfw=undefined&keyword=undefined"
    + "&searchfilter=&searchfilterexclude=&tone=Normal&prefix=None&randomai=undefined")

interface GenfunFactApiService : FactApiService {
    @GET(url)
    override suspend fun getFact(): GenfunFact
}
