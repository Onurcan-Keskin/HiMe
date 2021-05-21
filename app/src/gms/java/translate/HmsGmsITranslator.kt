package translate

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.huawei.hime.helpers.ITranslator

class HmsGmsITranslator(context: Context) : ITranslator {

    private var languageIdentifier: FirebaseLanguageIdentification? = null

    override fun detectLanguage(sourceText: String, tvLCode: TextView, tv: TextView) {
        languageIdentifier!!.identifyLanguage(sourceText).addOnSuccessListener { languageCode ->
            tvLCode.text = languageCode
            translateText(sourceText,languageCode,tv)
        }
    }

    override fun initOnCreate() {
        languageIdentifier = FirebaseNaturalLanguage
            .getInstance()
            .getLanguageIdentification(
                FirebaseLanguageIdentificationOptions.Builder()
                    .setConfidenceThreshold(0.03f)
                    .build()
            )
    }

    override fun translateText(sourceText: String, sourceLangCode: String, tv: TextView) {
        val settings = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(
                FirebaseTranslateLanguage.languageForLanguageCode(sourceLangCode)
                    ?: FirebaseTranslateLanguage.EN
            )
            .setTargetLanguage(FirebaseTranslateLanguage.EN)
            .build()
        Log.d("HmsGmsTranslator", "$sourceText $sourceLangCode")
        val rmTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(settings)
        rmTranslator.downloadModelIfNeeded().addOnSuccessListener {
            rmTranslator.translate(sourceText).addOnCompleteListener {
                if (it.isSuccessful){
                    tv.text = it.result
                }else{
                    tv.visibility = View.GONE
                }
            }
        }.addOnFailureListener {
            tv.visibility = View.GONE
            it.printStackTrace()
        }
    }
}