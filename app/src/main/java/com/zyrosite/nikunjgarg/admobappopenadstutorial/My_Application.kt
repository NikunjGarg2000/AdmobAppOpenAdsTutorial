package com.zyrosite.nikunjgarg.admobappopenadstutorial

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class MyApplication : Application(), ActivityLifecycleCallbacks
{
    private var appOpenAdManager: AppOpenAdManager? = null
    var currentActivity: Activity? = null
    override fun onCreate() {
        super.onCreate()
        this.registerActivityLifecycleCallbacks(this)
        appOpenAdManager = AppOpenAdManager()
    }

    override fun onActivityCreated(activity: Activity, @Nullable savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager?.isShowingAd!!) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        appOpenAdManager?.showAdIfAvailable(activity, onShowAdCompleteListener)
    }
}

interface OnShowAdCompleteListener {
    fun onShowAdComplete()
}

private class AppOpenAdManager
{
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    
    private fun loadAd(context: Context) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable) {
            return
        }
        isLoadingAd = true
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            context.getString(R.string.MainActivityAppOpenAd),
            adRequest,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    try {
                        showAdIfAvailable(context as Activity)
                    } catch (e: Exception) {
                        Log.i("TAG", e.message.toString())
                    }
                    Log.e("TAG", "onAdLoaded.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.e(
                        "TAG",
                        "onAdFailedToLoad: " + loadAdError.message
                    )
                }
            })
    }

    private val isAdAvailable: Boolean
        get() =
            appOpenAd != null

    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    Log.e(
                        "TAG",
                        "Ad shown completely!"
                    )
                }
            })
    }

    fun showAdIfAvailable(
        activity: Activity,
        onShowAdCompleteListener: OnShowAdCompleteListener =
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    Log.e(
                        "TAG",
                        "Ad shown completely!"
                    )                }
            }
    ) {
        if (isShowingAd) {
            Log.e(
                "TAG",
                "The app open ad is already showing."
            )
            return
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable) {
            Log.e(
                "TAG",
                "The app open ad is not ready yet."
            )
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
            return
        }
        Log.e("TAG", "Will show ad.")
        appOpenAd!!.setFullScreenContentCallback(
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Log.e(
                        "TAG",
                        "onAdDismissedFullScreenContent."
                    )
                    onShowAdCompleteListener.onShowAdComplete()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.e(
                        "TAG",
                        "onAdFailedToShowFullScreenContent: " + adError.message
                    )
                    onShowAdCompleteListener.onShowAdComplete()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.e(
                        "TAG",
                        "onAdShowedFullScreenContent."
                    )
                }
            })
        isShowingAd = true
        appOpenAd!!.show(activity)
    }
}