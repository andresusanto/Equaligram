// EQUALIGRAM - EQUALIZER INSTAGRAM
// Andre Susanto, M Yafi, Ramandika P, Kevin Yudi
// Pengcit - IF ITB

#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <android/bitmap.h>
#include <cstring>
#include <unistd.h>

#define  LOG_TAG    "EQUALIZER INSTAGRAM"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


// supaya bisa dipanggil sama java
extern "C"
{
	JNIEXPORT jobject JNICALL Java_com_ganesus_equaligram_MainActivity_loadBitmap (JNIEnv * env, jobject obj, jobject bitmap);
	JNIEXPORT jobject JNICALL Java_com_ganesus_equaligram_MainActivity_genHistogram (JNIEnv * env, jobject obj, jobject bitmem, jobject canvas);
}


/////////////////////////////////////////////////////////////////////////////////////
// helper class and functions
/////////////////////////////////////////////////////////////////////////////////////

class NativeBitmap{
	public:
	uint32_t* pixels;
	AndroidBitmapInfo bitmapInfo;
	
	NativeBitmap(){
		pixels = NULL;
	}
};

typedef struct{
	uint8_t alpha, red, green, blue;
} ARGB;

uint32_t convertArgbToInt(ARGB argb) {
	return (argb.alpha << 24) | (argb.red) | (argb.green << 8) | (argb.blue << 16);
}

void convertIntToArgb(uint32_t pixel, ARGB* argb){
	argb->red = ((pixel) & 0xff);
	argb->green = ((pixel >> 8) & 0xff);
	argb->blue = ((pixel >> 16) & 0xff);
	argb->alpha = ((pixel >> 24) & 0xff);
}


NativeBitmap* convertBitmapToNative(JNIEnv * env, jobject bitmap){
	AndroidBitmapInfo bitmapInfo;
	uint32_t* storedBitmapPixels = NULL;

	int ret;
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo)) < 0){
		LOGE("Error eksekusi AndroidBitmap_getInfo()! error=%d", ret);
		return NULL;
	}

	LOGD("width:%d height:%d stride:%d", bitmapInfo.width, bitmapInfo.height, bitmapInfo.stride);

	if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888){
		LOGE("Format bitmap bukan RGBA_8888!");
		return NULL;
	}

	void* bitmapPixels;
	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0){
		LOGE("Error eksekusi AndroidBitmap_lockPixels()! error=%d", ret);
		return NULL;
	}

	uint32_t* src = (uint32_t*) bitmapPixels;
	storedBitmapPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];
	uint32_t pixelsCount = bitmapInfo.height * bitmapInfo.width;
	memcpy(storedBitmapPixels, src, sizeof(uint32_t) * pixelsCount);
	AndroidBitmap_unlockPixels(env, bitmap);

	// store ke memory sbg array int
	NativeBitmap *nBitmap = new NativeBitmap();
	nBitmap->bitmapInfo = bitmapInfo;
	nBitmap->pixels = storedBitmapPixels;
	return nBitmap;
}

jobject convertNativeToBitmap(JNIEnv * env, NativeBitmap* nBitmap){
	if (nBitmap->pixels == NULL){
		LOGD("Bitmap kosong / error");
		return NULL;
	}

	// manggil fungsi bitmap java via env
	jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
	jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
	jstring configName = env->NewStringUTF("ARGB_8888");
	jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
	jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf","(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
	jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass, valueOfBitmapConfigFunction, configName);
	jobject newBitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, nBitmap->bitmapInfo.width, nBitmap->bitmapInfo.height, bitmapConfig);

	// masukin pixel ke bitmap
	int ret;
	void* bitmapPixels;

	if ((ret = AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels)) < 0){
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
		return NULL;
	}

	uint32_t* newBitmapPixels = (uint32_t*) bitmapPixels;
	uint32_t pixelsCount = nBitmap->bitmapInfo.height * nBitmap->bitmapInfo.width;
	memcpy(newBitmapPixels, nBitmap->pixels, sizeof(uint32_t) * pixelsCount);
	AndroidBitmap_unlockPixels(env, newBitmap);

	//LOGD("convert berhasil");
	return newBitmap;

}

/////////////////////////////////////////////////////////////////////////////////////
// fungsi untuk load bitmap dan store ke native memory
/////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jobject JNICALL Java_com_ganesus_equaligram_MainActivity_loadBitmap (JNIEnv * env, jobject obj, jobject bitmap){
    return env->NewDirectByteBuffer(convertBitmapToNative (env, bitmap), 0);
}


/////////////////////////////////////////////////////////////////////////////////////
// fungsi untuk generate histogram
/////////////////////////////////////////////////////////////////////////////////////

JNIEXPORT jobject JNICALL Java_com_ganesus_equaligram_MainActivity_genHistogram (JNIEnv * env, jobject obj, jobject bitmem, jobject canvas){
	NativeBitmap* nCanvas = convertBitmapToNative(env, canvas);
	NativeBitmap* nBitmap = (NativeBitmap*) env->GetDirectBufferAddress(bitmem);

	if (nBitmap->pixels == NULL || nCanvas->pixels == NULL)
		return NULL;

	uint32_t* hRed = new uint32_t[256];
	uint32_t* hGreen = new uint32_t[256];
	uint32_t* hBlue = new uint32_t[256];

	for (uint16_t i = 0; i < 256; i++){
		hRed[i] = 0; hGreen[i] = 0; hBlue[i] = 0;
	}

	uint32_t nBitmapSize = nBitmap->bitmapInfo.height * nBitmap->bitmapInfo.width;

	ARGB aBlack;
	aBlack.red = 0;
	aBlack.green = 255;
	aBlack.blue = 0;
	aBlack.alpha = 255;

	uint32_t iBlack = convertArgbToInt(aBlack);

	for (uint32_t i = 0; i < nBitmapSize; i++){
		ARGB bitmapColor;
		convertIntToArgb(nBitmap->pixels[i], &bitmapColor);

		hRed[bitmapColor.red]++;
		hGreen[bitmapColor.green]++;
		hBlue[bitmapColor.blue]++;
	}

	for (uint16_t i = 0; i < 256; i++){
		int max = hBlue[i];
		if (max > 500) max = 500;
		if (max == 0) max = 0;

		//LOGD("%d   =   %d", i, max);

		for (int j = 0; j < max; j++){
			nCanvas->pixels[i + j * 500] = iBlack;
		}

	}

	LOGE("Sudah!");
	return convertNativeToBitmap(env, nCanvas);

}