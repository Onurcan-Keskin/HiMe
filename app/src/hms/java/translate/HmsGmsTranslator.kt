package translate

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.huawei.hime.util.tranlatorHelper.TranslatorHelper
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting

class HmsGmsTranslator(context: Context) : TranslatorHelper {

    private var mlLocalLangDetector: MLLocalLangDetector? = null

    override fun detectLanguage(sourceText: String, tvLCode: TextView, tv: TextView) {
        val detect = mlLocalLangDetector!!.firstBestDetect(sourceText)
        detect.addOnSuccessListener { languageCode ->
            Log.d(
                "HmsGmsTranslator",
                "Language Code: " + languageCode!! + "\n" + tv.text.toString()
            )
            tvLCode.text = languageCode
            translateText(sourceText, languageCode, tv)
        }
        mlLocalLangDetector?.stop()
    }

    override fun initOnCreate() {
        val factory = MLLangDetectorFactory.getInstance()
        val settings = MLLocalLangDetectorSetting.Factory()
            .setTrustedThreshold(0.01F)
            .create()
        mlLocalLangDetector = factory.getLocalLangDetector(settings)
    }

    override fun translateText(sourceText: String, sourceLangCode: String, tv: TextView) {
        val settings = MLRemoteTranslateSetting.Factory()
            .setSourceLangCode(sourceLangCode)
            .setTargetLangCode("en")
            .create()
        val rmTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(settings)
        val task = rmTranslator.asyncTranslate(sourceText)
        Log.d("HmsGmsTranslator", "Things Translate:  $sourceText $sourceLangCode")
        task.addOnSuccessListener { text ->
            tv.text = text
        }.addOnFailureListener {
            try {
                val mlException = it as MLException
                val errorCode = mlException.errCode
                val errorMessage = mlException.message
                Log.e(
                    "HmsGmsTranslator",
                    "Error: $mlException, Code: $errorCode, Message: $errorMessage"
                )
                tv.visibility = View.GONE
            } catch (e: Exception) {

            }
        }
        rmTranslator?.stop()
    }

}