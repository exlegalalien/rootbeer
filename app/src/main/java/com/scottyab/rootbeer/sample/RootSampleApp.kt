package com.scottyab.rootbeer.sample

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.annotation.Keep
import com.whitecryption.annotation.FunctionGroup
import com.whitecryption.annotation.SecureCallback
import timber.log.Timber

class RootSampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        if (BuildConfig.DEBUG) {
            initStrictMode()
        }

        mContext = applicationContext
    }

    private fun initStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyDeathOnNetwork()
                .build()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .detectCleartextNetwork()
                    .penaltyLog()
                    .build()
            )
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        /*
           Because this method is called from a callback, it must be in the "obfuscation" group to avoid recursive callbacks.
        */
        @FunctionGroup(name = "obfuscation")
        fun getMainAppContext(): Context? {
            return mContext
        }

        private const val TAG: String = "RootSampleApp.Application"

        /*
                This is the callback function invoked by the protected app when it detects a threat. This method will be protected
                because "Main" is a startup class. This method is included in the "obfuscation" function group; therefore, it will
                be converted to native code and obfuscated, but other security checks will not be included in it. Because the method
                is never referenced from anywhere in the Java code, we assign the @androidx.annotation.Keep annotation to prevent
                this callback from being removed by ProGuard or R8. Marking the method with the
                @com.whitecryption.annotation.SecureCallback annotation also forces pruning this method from the final protected
                app.
                */
        @JvmStatic
        @SuppressLint("LogNotTimber")
        @SecureCallback
        @Keep
        fun TamperCallback() {
            // To avoid recursive detection, we must not call any potentially protected code from here!
            Log.w(TAG, "enter Main.TamperCallback()...")
            val myContext: Context? = getMainAppContext()
            // Context could be null, if callback is called before class onCreate method has been called and context has been saved
            if (myContext != null) {
                Toast.makeText(myContext, "TamperCallback", Toast.LENGTH_SHORT).show()
            }
            Log.w(TAG, "leave Main.TamperCallback()...")
        }
    }
}
