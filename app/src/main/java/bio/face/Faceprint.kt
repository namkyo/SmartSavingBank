package bio.face

class Faceprint {
    external fun jniFALibInit(
            path: String?,
            nSize1: Int, ASE_Data1: ByteArray?,
            nSize2: Int, ASE_Data2: ByteArray?,
            nSize3: Int, ASE_Data3: ByteArray?,
            nSize4: Int, ASE_Data4: ByteArray?,
            nSize5: Int, ASE_Data5: ByteArray?,
            nSize6: Int, ASE_Data6: ByteArray?,
            nSize7: Int, ASE_Data7: ByteArray?
    ): Int

    external fun jniFALibUnInit(): Int
    external fun jniFAExtractDb(
            Image: ByteArray?,
            nWidth: Int,
            nHeight: Int,
            nFormat: Int,
            DBFeature: ByteArray?): Int

    external fun jniFAExtractDbBase64(
            Image: ByteArray?,
            nWidth: Int,
            nHeight: Int,
            nFormat: Int,
            DBFeature64: ByteArray?): Int

    external fun jniFAExtractDbBmp(
            Image: ByteArray?,
            nSize: Int,
            DBFeature: ByteArray?): Int

    external fun jniFAExtractDbBmpBase64(
            Image: ByteArray?,
            nSize: Int,
            DBFeature64: ByteArray?): Int

    external fun jniFAExtractDbJpg(
            Image: ByteArray?,
            nSize: Int,
            DBFeature: ByteArray?): Int

    external fun jniFAExtractDbJpgBase64(
            Image: ByteArray?,
            nSize: Int,
            DBFeature64: ByteArray?): Int

    external fun getDeFeatureBase64(Feature: ByteArray?, arr: IntArray?): ByteArray?
    external fun jniGetVersion(): String?

    companion object {
        init {

            //  System.loadLibrary("c++_shared");  //  add
            System.loadLibrary("Faceprintex")
        }
    }

    external fun QualityCheckJPG(buf: ByteArray?, nSize: Int, score_flag: IntArray?): Int
    external fun QualityCheckBMP(buf: ByteArray?, nSize: Int, score_flag: IntArray?): Int
}

