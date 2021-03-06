/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_mt_wallet_core_safe_SafeCase */

#ifndef _Included_com_mt_wallet_core_safe_SafeCase
#define _Included_com_mt_wallet_core_safe_SafeCase
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_mt_wallet_core_safe_SafeCase_initializeJni
  (JNIEnv * env, jobject obj);


JNIEXPORT void JNICALL Java_com_mt_wallet_core_safe_SafeCase_uninitializeJni
  (JNIEnv * env, jobject obj);

/*
 * Class:     com_mt_wallet_core_safe_SafeCase
 * Method:    value
 * Signature: ([C)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mt_wallet_core_safe_SafeCase_valueJni
  (JNIEnv *, jobject, jcharArray);

/*
 * Class:     com_mt_wallet_core_safe_SafeCase
 * Method:    delete
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_mt_wallet_core_safe_SafeCase_deleteJni
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_mt_wallet_core_safe_SafeCase
 * Method:    put
 * Signature: (C)I
 */
JNIEXPORT jint JNICALL Java_com_mt_wallet_core_safe_SafeCase_putJni
  (JNIEnv *, jobject, jchar);


#define MAX_PASS_LEN 20
#define MAX_SHUFFLE_TIMES 680
#define PASS_RADIX 136
#define DEFAULT_MD5_STR_LEN 42 //with '\0' at the end
#define RAW_MD5_DATA_LEN 20
#define KEY_FIELD_NAME "id"
#define KEY_FIELD_TYPE "I"

//#define LOG_ENABLED
#define LOG_TAG "SafeCaseJNI:"

typedef struct SafeCaseDataParcel_tag{
	void* next;
	jint key;
	char idxAry[MAX_PASS_LEN];
	int idxArySize;
	char shuffledNumbers[PASS_RADIX];
} SafeCaseDataParcel;

#ifdef __cplusplus
}

#endif
#endif
