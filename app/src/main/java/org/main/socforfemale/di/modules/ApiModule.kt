package org.main.socforfemale.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.main.socforfemale.rest.Http
import org.main.socforfemale.rest.API
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Sarvar on 22.08.2017.
 */

@Module(includes = arrayOf(NetworkModule::class))
class ApiModule {

    @Provides
    fun gson():Gson = GsonBuilder().setLenient().create()

    @Provides
    fun retrofit(client: OkHttpClient,gson: Gson):Retrofit{
        return Retrofit.Builder()
                .baseUrl(Http.BASE_URL)

                .client(client)

                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
    @Provides
    fun getAPIClient(retrofit: Retrofit): API = retrofit.create(API::class.java)

}