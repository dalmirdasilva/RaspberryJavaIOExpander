#include "Wire.h"

JNIEXPORT jint JNICALL Java_ioexpander_Wire_begin(JNIEnv *env, jobject obj, jbyte channel) {
    char f[11] = "/dev/i2c-0";
    f[9] = '0' + (int) (channel & 0x01);
    int fd = open(f, O_RDWR);
    if (fd < 0) {
        perror("Cannot open i2c bus.");
        exit(1);
    }
    Java_ioexpander_Wire_setFileDescriptor(env, obj, fd);
}

JNIEXPORT void JNICALL Java_ioexpander_Wire_stop(JNIEnv *env, jobject obj) {
    int fd = Java_ioexpander_Wire_getFileDescriptor(env, obj);
    close((int) fd);
}

JNIEXPORT void JNICALL Java_ioexpander_Wire_beginTransmission(JNIEnv *env, jobject obj, jint address) {
    int fd = Java_ioexpander_Wire_getFileDescriptor(env, obj);
    if (ioctl((int) fd, I2C_SLAVE, (int) address) < 0) {
        perror("Cannot set i2c address.");
        exit(1);
    }
}

JNIEXPORT jbyte JNICALL Java_ioexpander_Wire_endTransmission(JNIEnv *env, jobject obj) {
    return 0;
}

JNIEXPORT jbyte JNICALL Java_ioexpander_Wire_requestFrom(JNIEnv *env, jobject obj, jint address, jint len) {
    int fd = Java_ioexpander_Wire_getFileDescriptor(env, obj);
    if (ioctl((int) fd, I2C_SLAVE, (int) address) < 0) {
        perror("Cannot set i2c address.");
        exit(1);
    }
    return len;
}

JNIEXPORT jint JNICALL Java_ioexpander_Wire_writeBytes(JNIEnv *env, jobject obj, jbyteArray buf, jint len) {
    int fd = Java_ioexpander_Wire_getFileDescriptor(env, obj);
    jboolean isCopy = false;
    jbyte* ptr = env->GetByteArrayElements(buf, &isCopy);
    return ::write((int) fd, (char*) ptr, (int)len);
}

JNIEXPORT jint JNICALL Java_ioexpander_Wire_available(JNIEnv *env, jobject obj) {
    return 1;
}

JNIEXPORT jint JNICALL Java_ioexpander_Wire_readByte(JNIEnv *env, jobject obj) {
    int fd = Java_ioexpander_Wire_getFileDescriptor(env, obj);
    char buf[1];
    if (::read((int) fd, buf, 1) != 1) {
        perror("Cannot read i2c.");
        exit(1);
    }
    return (jint) buf[0];
}

JNIEXPORT void JNICALL Java_ioexpander_Wire_flush(JNIEnv *env, jobject obj) {
}

int Java_ioexpander_Wire_getFileDescriptor(JNIEnv *env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "fd", "I");
    int fd = env->GetIntField(obj, fid);
    if (fd == -1) {
        perror("Wire not initialized. FD is -1.");
        exit(1);
    }
    return fd;
}

void Java_ioexpander_Wire_setFileDescriptor(JNIEnv *env, jobject obj, int fd) {
    jclass cls = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(cls, "fd", "I");
    env->SetIntField(obj ,fid, fd);
}
