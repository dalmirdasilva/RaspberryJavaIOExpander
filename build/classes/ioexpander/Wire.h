#ifndef __RASPBERRY_Java_ioexpander_Wire_JNI_H__
#define __RASPBERRY_Java_ioexpander_Wire_JNI_H__ 1

#include <jni.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <linux/i2c-dev.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <fcntl.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Initiate the library. (Only as a master)
 * This should normally be called only once.
 * It maps the BSC0 (0x7E20_5000) registers.
 */
JNIEXPORT jint JNICALL Java_ioexpander_Wire_begin(JNIEnv *env, jobject obj, jbyte channel);

/**
 * Unmap the BSC0 registers.
 */
JNIEXPORT void JNICALL Java_ioexpander_Wire_stop(JNIEnv *env, jobject obj);

/**
 * Begin a transmission to the I2C slave device with the given 
 * address. Subsequently, queue bytes for transmission with the 
 * write() function and transmit them by calling endTransmission().
 * 
 * @param address               The device address.
 */
JNIEXPORT void JNICALL Java_ioexpander_Wire_beginTransmission(JNIEnv *env, jobject obj, jint address);

/**
 * Ends a transmission to a slave device that was begun by 
 * beginTransmission() and transmits the bytes that were queued by 
 * write().
 * 
 * @return                      Nothing for now.
 */
JNIEXPORT jbyte JNICALL Java_ioexpander_Wire_endTransmission(JNIEnv *env, jobject obj);

/**
 * Used to request bytes from a slave device. The bytes may then be 
 * retrieved with the available() and read() functions.
 * 
 * @param address               The slave address.
 * @param len                   The length of data. Need be <= 16 
 *                              due the FIFO limits.
 */
JNIEXPORT jbyte JNICALL Java_ioexpander_Wire_requestFrom(JNIEnv *env, jobject obj, jint address, jint len);

/**
 * Queues bytes for transmission to slave device (in-between calls 
 * to beginTransmission() and endTransmission()).
 * 
 * @param buf                   The bytes to be queued.
 * @param len                   The number of byte to be queued.
 * @return                      The number of accepted bytes.
 */
JNIEXPORT jint JNICALL Java_ioexpander_Wire_writeBytes(JNIEnv *env, jobject obj, jbyteArray buf, jint len);

/**
 * Returns 1 if there is one or more bytes to be read.
 * 
 * @return                      0 if there is no bytes to be read in
 *                              the internal FIFO.
 */
JNIEXPORT jint JNICALL Java_ioexpander_Wire_available(JNIEnv *env, jobject obj);

/**
 * Reads a byte that was transmitted from a slave device to a master
 * after a call to requestFrom()
 *
 * @return                      The byte read.
 */
JNIEXPORT jint JNICALL Java_ioexpander_Wire_readByte(JNIEnv *env, jobject obj);

/**
 * For now, does nothing.
 */
JNIEXPORT void JNICALL Java_ioexpander_Wire_flush(JNIEnv *env, jobject obj);

/**
 * Retrieve FD from object
 */
int Java_ioexpander_Wire_getFileDescriptor(JNIEnv *env, jobject obj);

/**
 * Set file descriptior field.
 */
void Java_ioexpander_Wire_setFileDescriptor(JNIEnv *env, jobject obj, int fd);

#ifdef __cplusplus
}
#endif

#endif /* __RASPBERRY_Java_ioexpander_Wire_JNI_H__ */
