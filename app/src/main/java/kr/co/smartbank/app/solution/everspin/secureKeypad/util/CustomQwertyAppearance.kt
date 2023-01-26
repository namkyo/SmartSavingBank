package kr.co.smartbank.app.solution.everspin.secureKeypad.util

import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import kr.co.everspin.eversafe.keypad.widget.params.*
import kr.co.smartbank.app.R

class CustomQwertyAppearance(
        private val resources: Resources) : ESKeypadAppearanceManageable, ESKeypadBtnAppearanceManageable {

    //[[ -- ESKeypadBtnAppearanceManageable
    override fun getCustomForCancelBtn(): ESBtnViewInfo {
        val btnViewInfo = ESBtnViewInfo()
        btnViewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_special_cancel, null)
        btnViewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
        btnViewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
        return btnViewInfo
    }

    override fun getCustomForDoneBtn(): ESBtnViewInfo {
        val btnViewInfo = ESBtnViewInfo()
        btnViewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_special_done, null)
        btnViewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
        btnViewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
        return btnViewInfo
    }

    //키패드의 배경
    override fun getKeypadBackground(): Drawable {
        return ColorDrawable(Color.parseColor("#072E5E"))
    }

    //키패드 빈자리 전경 설정
    override fun getCustomImageForNullKeys(): Drawable? {
        return ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_b, null)
    }

    //특수 키의 전경/배경을 설정
    override fun getCustomForSpecialKey(type: ESSpecialKeyTypes): ESKeyViewInfo {
        var viewInfo = ESKeyViewInfo()
        when (type) {
            ESSpecialKeyTypes.BackSpace -> {
                viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_bs, null)
                viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)
                viewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
                viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
            }
            ESSpecialKeyTypes.Done -> {
                viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_ic, null)
                viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)
                viewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
                viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
            }
            ESSpecialKeyTypes.Shuffle -> {
                viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_ckba, null)
                //viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)
                viewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
                viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
            }
            ESSpecialKeyTypes.Space -> {
                viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_115, null)
                viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)
                viewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
                viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
            }
            ESSpecialKeyTypes.Shift -> {
                viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_sf, null)
               // viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)
                viewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
                viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
            }
            ESSpecialKeyTypes.Toggle -> viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_bc_skm_ct, null)
            ESSpecialKeyTypes.Cancel -> {
                viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,R.drawable.es_special_cancel, null)
                //viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)
                viewInfo.backgroundNormal = ColorDrawable(Color.CYAN)
                viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
            }
        }
        //        if (null != viewInfo) {
//            viewInfo.setBackgroundNormal(new ColorDrawable(Color.LTGRAY));
//            viewInfo.setBackgroundPressed(new ColorDrawable(Color.BLACK));
//        }
        return viewInfo
    }

    //키패드 자판 변경
    override fun getCustomForCharacter(character: Char): ESKeyViewInfo {
        val viewInfo = ESKeyViewInfo()

        //보안 키패드의 키 전경을 설정해 주기 위한 메서드
        viewInfo.forgroundNormal = ResourcesCompat.getDrawable(resources,customQwertyResIdMap[character.toInt()], null)
        viewInfo.forgroundNormal.colorFilter= BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_IN)

        //보안 키패드의 키가 눌린 경우에 전경을 설정해 주기 위한 메서드
        viewInfo.forgroundPressed=ColorDrawable(Color.BLACK)

        //보안 키패드의 키 배경을 설정해 주기 위한 메서드
        //키 누르기 전
        if (character == '0'||character == 't'||character == 'Y'||character == 'N') {
            viewInfo.backgroundNormal = ColorDrawable(Color.GRAY)
        }else{
            viewInfo.backgroundNormal = ColorDrawable(Color.parseColor("#072E5E"))
        }
        //키 눌렀을때
        viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)

        return viewInfo
    }

    companion object {
        private val customQwertybackGroudMap = arrayOfNulls<Drawable>(128)
        private val customQwertyResIdMap = IntArray(128)

        init {
            customQwertybackGroudMap['1'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['2'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['3'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['4'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['5'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['6'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['7'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['8'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['9'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            //        customQwertybackGroudMap[(int)'0'] = new ColorDrawable(Color.parseColor("#072E5E"));
            customQwertybackGroudMap['a'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['A'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['b'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['B'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['c'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['C'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
            customQwertybackGroudMap['d'.toInt()] = ColorDrawable(Color.parseColor("#072E5E"))
        }

        init {
            customQwertyResIdMap['1'.toInt()] = R.drawable.es_bc_skm_11
            customQwertyResIdMap['2'.toInt()] = R.drawable.es_bc_skm_12
            customQwertyResIdMap['3'.toInt()] = R.drawable.es_bc_skm_13
            customQwertyResIdMap['4'.toInt()] = R.drawable.es_bc_skm_14
            customQwertyResIdMap['5'.toInt()] = R.drawable.es_bc_skm_15
            customQwertyResIdMap['6'.toInt()] = R.drawable.es_bc_skm_16
            customQwertyResIdMap['7'.toInt()] = R.drawable.es_bc_skm_17
            customQwertyResIdMap['8'.toInt()] = R.drawable.es_bc_skm_18
            customQwertyResIdMap['9'.toInt()] = R.drawable.es_bc_skm_19
            customQwertyResIdMap['0'.toInt()] = R.drawable.es_bc_skm_20
            customQwertyResIdMap['q'.toInt()] = R.drawable.es_bc_skm_21
            customQwertyResIdMap['w'.toInt()] = R.drawable.es_bc_skm_22
            customQwertyResIdMap['e'.toInt()] = R.drawable.es_bc_skm_23
            customQwertyResIdMap['r'.toInt()] = R.drawable.es_bc_skm_24
            customQwertyResIdMap['t'.toInt()] = R.drawable.es_bc_skm_25
            customQwertyResIdMap['y'.toInt()] = R.drawable.es_bc_skm_26
            customQwertyResIdMap['u'.toInt()] = R.drawable.es_bc_skm_27
            customQwertyResIdMap['i'.toInt()] = R.drawable.es_bc_skm_28
            customQwertyResIdMap['o'.toInt()] = R.drawable.es_bc_skm_29
            customQwertyResIdMap['p'.toInt()] = R.drawable.es_bc_skm_30
            customQwertyResIdMap['a'.toInt()] = R.drawable.es_bc_skm_31
            customQwertyResIdMap['s'.toInt()] = R.drawable.es_bc_skm_32
            customQwertyResIdMap['d'.toInt()] = R.drawable.es_bc_skm_33
            customQwertyResIdMap['f'.toInt()] = R.drawable.es_bc_skm_34
            customQwertyResIdMap['g'.toInt()] = R.drawable.es_bc_skm_35
            customQwertyResIdMap['h'.toInt()] = R.drawable.es_bc_skm_36
            customQwertyResIdMap['j'.toInt()] = R.drawable.es_bc_skm_37
            customQwertyResIdMap['k'.toInt()] = R.drawable.es_bc_skm_38
            customQwertyResIdMap['l'.toInt()] = R.drawable.es_bc_skm_39
            customQwertyResIdMap['z'.toInt()] = R.drawable.es_bc_skm_40
            customQwertyResIdMap['x'.toInt()] = R.drawable.es_bc_skm_41
            customQwertyResIdMap['c'.toInt()] = R.drawable.es_bc_skm_42
            customQwertyResIdMap['v'.toInt()] = R.drawable.es_bc_skm_43
            customQwertyResIdMap['b'.toInt()] = R.drawable.es_bc_skm_44
            customQwertyResIdMap['n'.toInt()] = R.drawable.es_bc_skm_45
            customQwertyResIdMap['m'.toInt()] = R.drawable.es_bc_skm_46
            customQwertyResIdMap['Q'.toInt()] = R.drawable.es_bc_skm_57
            customQwertyResIdMap['W'.toInt()] = R.drawable.es_bc_skm_58
            customQwertyResIdMap['E'.toInt()] = R.drawable.es_bc_skm_59
            customQwertyResIdMap['R'.toInt()] = R.drawable.es_bc_skm_60
            customQwertyResIdMap['T'.toInt()] = R.drawable.es_bc_skm_61
            customQwertyResIdMap['Y'.toInt()] = R.drawable.es_bc_skm_62
            customQwertyResIdMap['U'.toInt()] = R.drawable.es_bc_skm_63
            customQwertyResIdMap['I'.toInt()] = R.drawable.es_bc_skm_64
            customQwertyResIdMap['O'.toInt()] = R.drawable.es_bc_skm_65
            customQwertyResIdMap['P'.toInt()] = R.drawable.es_bc_skm_66
            customQwertyResIdMap['A'.toInt()] = R.drawable.es_bc_skm_67
            customQwertyResIdMap['S'.toInt()] = R.drawable.es_bc_skm_68
            customQwertyResIdMap['D'.toInt()] = R.drawable.es_bc_skm_69
            customQwertyResIdMap['F'.toInt()] = R.drawable.es_bc_skm_70
            customQwertyResIdMap['G'.toInt()] = R.drawable.es_bc_skm_71
            customQwertyResIdMap['H'.toInt()] = R.drawable.es_bc_skm_72
            customQwertyResIdMap['J'.toInt()] = R.drawable.es_bc_skm_73
            customQwertyResIdMap['K'.toInt()] = R.drawable.es_bc_skm_74
            customQwertyResIdMap['L'.toInt()] = R.drawable.es_bc_skm_75
            customQwertyResIdMap['Z'.toInt()] = R.drawable.es_bc_skm_76
            customQwertyResIdMap['X'.toInt()] = R.drawable.es_bc_skm_77
            customQwertyResIdMap['C'.toInt()] = R.drawable.es_bc_skm_78
            customQwertyResIdMap['V'.toInt()] = R.drawable.es_bc_skm_79
            customQwertyResIdMap['B'.toInt()] = R.drawable.es_bc_skm_80
            customQwertyResIdMap['N'.toInt()] = R.drawable.es_bc_skm_81
            customQwertyResIdMap['M'.toInt()] = R.drawable.es_bc_skm_82
            customQwertyResIdMap['!'.toInt()] = R.drawable.es_bc_skm_83
            customQwertyResIdMap['@'.toInt()] = R.drawable.es_bc_skm_84
            customQwertyResIdMap['#'.toInt()] = R.drawable.es_bc_skm_85
            customQwertyResIdMap['$'.toInt()] = R.drawable.es_bc_skm_86
            customQwertyResIdMap['%'.toInt()] = R.drawable.es_bc_skm_87
            customQwertyResIdMap['^'.toInt()] = R.drawable.es_bc_skm_88
            customQwertyResIdMap['&'.toInt()] = R.drawable.es_bc_skm_89
            customQwertyResIdMap['*'.toInt()] = R.drawable.es_bc_skm_90
            customQwertyResIdMap['('.toInt()] = R.drawable.es_bc_skm_91
            customQwertyResIdMap[')'.toInt()] = R.drawable.es_bc_skm_92
            customQwertyResIdMap['`'.toInt()] = R.drawable.es_bc_skm_93
            customQwertyResIdMap['-'.toInt()] = R.drawable.es_bc_skm_94
            customQwertyResIdMap['='.toInt()] = R.drawable.es_special_25
            customQwertyResIdMap['\\'.toInt()] = R.drawable.es_bc_skm_96
            customQwertyResIdMap['['.toInt()] = R.drawable.es_bc_skm_97
            customQwertyResIdMap[']'.toInt()] = R.drawable.es_bc_skm_98
            customQwertyResIdMap[';'.toInt()] = R.drawable.es_bc_skm_99
            customQwertyResIdMap['\''.toInt()] = R.drawable.es_bc_skm_100
            customQwertyResIdMap[','.toInt()] = R.drawable.es_bc_skm_101
            customQwertyResIdMap['.'.toInt()] = R.drawable.es_bc_skm_102
            customQwertyResIdMap['/'.toInt()] = R.drawable.es_bc_skm_103
            customQwertyResIdMap['~'.toInt()] = R.drawable.es_special_22
            customQwertyResIdMap['_'.toInt()] = R.drawable.es_special_24
            customQwertyResIdMap['+'.toInt()] = R.drawable.es_bc_skm_106
            customQwertyResIdMap['|'.toInt()] = R.drawable.es_bc_skm_107
            customQwertyResIdMap['{'.toInt()] = R.drawable.es_bc_skm_108
            customQwertyResIdMap['}'.toInt()] = R.drawable.es_bc_skm_109
            customQwertyResIdMap[':'.toInt()] = R.drawable.es_bc_skm_110
            customQwertyResIdMap['\"'.toInt()] = R.drawable.es_bc_skm_111
            customQwertyResIdMap['<'.toInt()] = R.drawable.es_bc_skm_112
            customQwertyResIdMap['>'.toInt()] = R.drawable.es_bc_skm_113
            customQwertyResIdMap['?'.toInt()] = R.drawable.es_bc_skm_114
        }
    }
}
