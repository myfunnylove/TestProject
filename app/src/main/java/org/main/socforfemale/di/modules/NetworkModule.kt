package org.main.socforfemale.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import org.main.socforfemale.base.Base
import org.main.socforfemale.resources.utils.Functions
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Sarvar on 22.08.2017.
 */
@Module(includes = arrayOf(ContextModule::class))
class NetworkModule {

    @Provides
    fun getFile(ctx: Context):File {
        return File(ctx.cacheDir,"app_cache")
    }

    @Provides
    fun getCacheFIle(cacheFile:File):Cache{
        return Cache(cacheFile,10*1000*1000)
    }

    @Provides
    fun client(cache: Cache):OkHttpClient{

        return OkHttpClient.Builder()
                .connectTimeout(7, TimeUnit.MINUTES)
                .readTimeout   (7, TimeUnit.MINUTES)
                .writeTimeout  (7, TimeUnit.MINUTES)
                .cache(cache)
                .addNetworkInterceptor { chain ->
                    val response = chain.proceed(chain.request())
                    val cache    = response.header("Cache-Control")
                    if (cache == null || cache.contains("no-store")  || cache.contains("no-cache") || cache.contains("must-revalidate") || cache.contains("max-age=0")){
                        response.newBuilder()
                                .removeHeader("Pragma")
                                .header("Cache-Control","public, max=age="+5000).build()
                    }else{
                        response
                    }
                }
                .addInterceptor { chain ->
                    var req = chain.request()
                    if (!Functions.isNetworkAvailable(Base.get)) {
                        req = req.newBuilder()
                                .cacheControl(CacheControl.FORCE_CACHE)
                                .build()
                    }

                    if (Functions.isNetworkAvailable(Base.get)){
                        req =  req.newBuilder()
                                .header("Cache-Control", "public, max-age=" + 5000)
                                .removeHeader("Pragma")
                                .build();
                    }else{
                        val maxStale = 60 * 60 * 24 * 28
                        req = req.newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                .removeHeader("Pragma")
                                .build()
                    }


                    chain.proceed(req)

                }

                .build()
    }
}