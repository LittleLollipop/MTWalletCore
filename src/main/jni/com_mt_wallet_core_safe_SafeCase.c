#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <time.h>
#include<Android/log.h>
#include <string.h>
#include "com_mt_wallet_core_safe_SafeCase.h"
#include "Md5.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, LOG_TAG, __VA_ARGS__) // 定义LOGF类型

#ifdef LOG_ENABLED
	#define helper_log(...) LOGD(__VA_ARGS__)
#else
	#define helper_log(...) 
#endif

#ifdef __cplusplus   
extern "C"  
{   
#endif  

static SafeCaseDataParcel* s_data = NULL;

SafeCaseDataParcel* helper_find_parcel(SafeCaseDataParcel* pHeader, jint key){
	helper_log("find parcel with head: %p with key:%d", pHeader, key);

	if(pHeader == NULL)
			return NULL;
	
	helper_log("key attached for head is:%d", pHeader->key);

	if(pHeader->key == key)
			return pHeader;

	return helper_find_parcel(pHeader->next, key);
}

static inline SafeCaseDataParcel* helper_enqueue_parcel(SafeCaseDataParcel *pHeader, SafeCaseDataParcel* pParcel){
		pParcel->next = pHeader;
		return pParcel;
}

void helper_shuffle(char* p, int len, int idx, int iterations){
	if(iterations > MAX_SHUFFLE_TIMES)
			return;
	
	idx = idx % len;
	int targetIdx = arc4random() % len;

	char t = p[idx];
	p[idx] = p[targetIdx];
	p[targetIdx] = t;
	
	helper_shuffle(p, len, idx + 1, iterations + 1);
}

int helper_find_idx(char* ary, int len, char c){
	int i;
	for(i = 0; i < len; i ++){
		if(ary[i] == c)
				return i;
	}

	return -1;
}

jint helper_object_key(JNIEnv* env, jobject obj){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID seqID = (*env)->GetFieldID(env, cls, KEY_FIELD_NAME, KEY_FIELD_TYPE);
	jint key = (*env)->GetIntField(env, obj, seqID);
	helper_log("key for object:%p is %d", obj, key);
	return key;
}

const char* helper_md5_hash(char* szStr){
	static char s_buf[DEFAULT_MD5_STR_LEN];
	MD5_CTX md5;
	MD5Init(&md5);
	unsigned char decrypt[RAW_MD5_DATA_LEN];
	MD5Update(&md5, szStr, strlen(szStr));
	MD5Final(&md5,decrypt);
	memset(s_buf, 0, sizeof(s_buf));
	int i;
	for(i=0; i < RAW_MD5_DATA_LEN ;i++){
		sprintf(s_buf + i * 2, "%02x", decrypt[i]);
	}
	return s_buf;
}

SafeCaseDataParcel* helper_destroy_parcel(jint key){
	if(s_data == NULL){
		helper_log("header is NULL, why do you call destroy?!");
		return NULL;
	}

	SafeCaseDataParcel* pNode = s_data;
	SafeCaseDataParcel* pLastNode = NULL;
	
	while(pNode){
		if(pNode->key == key){
			if(pLastNode != NULL)
				pLastNode->next = pNode->next;

			helper_log("destroying parcel:%p with key:%d", pNode, key);
			
			SafeCaseDataParcel* ret = s_data;

			if(pNode == s_data)
				ret = s_data->next;

			free(pNode); pNode = NULL;
			return ret;
		}

		pLastNode = pNode;
		pNode = pNode->next;
		helper_log("stepping to next parcel:%p", pNode);
	}

	helper_log("no parcel with key:%d found, destroy has no effect!", key);
	return s_data;
}

void helper_print_all_parcel(SafeCaseDataParcel* pHeader) {
#ifdef LOG_ENABLED
    helper_log("parcel header is:%p", pHeader);
    while(pHeader){
        helper_log("{parcel:%p,key:%d}", pHeader, pHeader->key);
        pHeader = pHeader->next;
    }
    helper_log("all above");
#endif
}

JNIEXPORT void JNICALL Java_com_mt_wallet_core_safe_SafeCase_uninitializeJni
  (JNIEnv * env, jobject obj){
  	jint key = helper_object_key(env, obj);
	helper_log("uninitialize called, destroying object with key:%d ...", key);
	helper_log("before destroy:");
	helper_print_all_parcel(s_data);
	s_data = helper_destroy_parcel(key);
	helper_log("after destroy:");
	helper_print_all_parcel(s_data);
  }


JNIEXPORT void JNICALL Java_com_mt_wallet_core_safe_SafeCase_initializeJni
  (JNIEnv * env, jobject obj){
  helper_log("initialize called!");
  	jint key = helper_object_key(env, obj);
	SafeCaseDataParcel* pParcel = helper_find_parcel(s_data, key);
	if(pParcel == NULL){
		helper_log("no parcel found for key:%d, creating...", key);
		pParcel	= malloc(sizeof(SafeCaseDataParcel));
		memset(pParcel, 0, sizeof(SafeCaseDataParcel));
		pParcel->key= key;
		s_data = helper_enqueue_parcel(s_data, pParcel);	
	}

	int i;
	for(i = 0; i < PASS_RADIX; i ++) pParcel->shuffledNumbers[i] = (char)i;

	helper_shuffle(pParcel->shuffledNumbers, PASS_RADIX, 0, 0);
	
#ifdef LOG_ENABLED
	helper_log("after shuffle:");
	for(i = 0; i < PASS_RADIX; i ++)
		helper_log("%d=>%c", i, '!' + pParcel->shuffledNumbers[i]);
#endif

	memset(pParcel->idxAry, 0, sizeof(pParcel->idxAry));
	pParcel->idxArySize = 0;
  }
/*
 * Class:     com_mt_wallet_core_safe_SafeCase
 * Method:    value
 * Signature: ([C)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mt_wallet_core_safe_SafeCase_valueJni
  (JNIEnv *env, jobject obj, jcharArray ary){
	jint key = helper_object_key(env, obj);
	SafeCaseDataParcel* pParcel = helper_find_parcel(s_data, key);

	if(pParcel == NULL)
			return NULL;

	char szPass[MAX_PASS_LEN + 1];
	memset(szPass, 0, MAX_PASS_LEN + 1);

	int i;
	for(i = 0; i < pParcel->idxArySize; i ++){
		int idx = pParcel->idxAry[i];
		char c = pParcel->shuffledNumbers[idx] + '!';
		szPass[i] = c;
	}

	helper_log("raw password:%s", szPass);

	const char* md5Hash = helper_md5_hash(szPass);

	helper_log("md5 hash:%s", md5Hash);

	jstring ret = (*env)->NewStringUTF(env, md5Hash);

	return ret;
  }

/*
 * Class:     com_mt_wallet_core_safe_SafeCase
 * Method:    delete
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_mt_wallet_core_safe_SafeCase_deleteJni
  (JNIEnv * env, jobject obj, jint idx){
	helper_log("delete called, clearning...");
	Java_com_mt_wallet_core_safe_SafeCase_initializeJni(env, obj);
  }

/*
 * Class:     com_mt_wallet_core_safe_SafeCase
 * Method:    put
 * Signature: (C)I
 */
JNIEXPORT jint JNICALL Java_com_mt_wallet_core_safe_SafeCase_putJni
  (JNIEnv *env, jobject obj, jchar c){
  	jint key = helper_object_key(env, obj);
	SafeCaseDataParcel* pParcel = helper_find_parcel(s_data, key);
	
	if(pParcel == NULL){
		helper_log("No parcel found for obj:%p", obj);
		return 256;
	}
	
	int idx = helper_find_idx(pParcel->shuffledNumbers, PASS_RADIX, c - '!');

	helper_log("idx found:%d", idx);

	if(idx == -1)
			return 257;

	pParcel->idxAry[pParcel->idxArySize ++] = idx;

	return time(NULL) % 256;	
  }


#ifdef __cplusplus   
}   
#endif
