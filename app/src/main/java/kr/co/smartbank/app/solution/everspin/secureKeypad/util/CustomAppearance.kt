package kr.co.everspin.eversafe.eversafenumericpadexample.keypad

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import kr.co.everspin.eversafe.keypad.widget.params.ESKeyViewInfo
import kr.co.everspin.eversafe.keypad.widget.params.ESKeypadAppearanceManageable
import kr.co.everspin.eversafe.keypad.widget.params.ESSpecialKeyTypes
import kr.co.smartbank.app.R

class CustomAppearance(
    private val resources: Resources
): ESKeypadAppearanceManageable {

    override fun getCustomForCharacter(p0: Char): ESKeyViewInfo? {
        var viewInfo: ESKeyViewInfo? = ESKeyViewInfo()

        viewInfo!!.backgroundNormal = ColorDrawable(0xFF19416E.toInt())
        viewInfo.backgroundPressed = ColorDrawable(0xFF32557D.toInt())

        when(p0){
            '0'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_10, null)
            '1'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_1, null)
            '2'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_2, null)
            '3'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_3, null)
            '4'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_4, null)
            '5'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_5, null)
            '6'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_6, null)
            '7'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_7, null)
            '8'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_8, null)
            '9'->viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources, R.drawable.es_bc_skm_9, null)
            else->viewInfo = null
        }
        return viewInfo
    }

    override fun getCustomImageForNullKeys(): Drawable? {
        return ResourcesCompat.getDrawable(resources, R.drawable.es_special_num_lock, null)
    }

    override fun getCustomForSpecialKey(p0: ESSpecialKeyTypes?): ESKeyViewInfo? {
        var viewInfo: ESKeyViewInfo? = ESKeyViewInfo()
        when(p0){
            ESSpecialKeyTypes.BackSpace -> {
                viewInfo!!.forgroundNormal =
                    ResourcesCompat.getDrawable(resources, R.drawable.es_special_del, null)
            }
            ESSpecialKeyTypes.Shuffle -> {
                viewInfo!!.forgroundNormal =
                    ResourcesCompat.getDrawable(resources, R.drawable.es_special_logo, null)
            }
            else -> viewInfo = null
        }
        if(viewInfo != null){
            viewInfo.backgroundNormal = ColorDrawable(0xFF19416E.toInt())
            viewInfo.backgroundPressed = ColorDrawable(0xFF32557D.toInt())
        }
        return viewInfo
    }

    override fun getKeypadBackground(): Drawable {
        return ColorDrawable(0xFF32557D.toInt())
    }


}