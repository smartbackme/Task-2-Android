package com.kangaroo.nowchart.data.source

import com.kangaroo.nowchart.data.service.ApiService
import com.qdedu.baselibcommon.data.source.ApiSource

object AppService {

    fun getApiService(mApi: String): ApiService {
        return ApiSource.instance(mApi).getApiRetrofit.create(ApiService::class.java)
    }

    fun getApiFromService(mApi: String): ApiService {
        return ApiSource.instance(mApi).getApiFromRetrofit.create(ApiService::class.java)
    }
}
