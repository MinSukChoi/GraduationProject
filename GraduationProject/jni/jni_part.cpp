#include <jni.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <vector>


#include <android/log.h>

#define LOG_TAG "GraduationProject/PeopleDetection"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_example_hellocv_MainActivity_FindPeople(JNIEnv*, jobject, jlong addrGray);
JNIEXPORT jint JNICALL Java_com_example_hellocv_MainActivity_FindFeature(JNIEnv*, jobject, jlong addrGray,jlong addrDescriptor);
JNIEXPORT void JNICALL Java_com_example_hellocv_MainActivity_GetDescriptor(JNIEnv*, jobject, jlong addrGray,jlong addrDescriptor);

JNIEXPORT void JNICALL Java_com_example_hellocv_MainActivity_FindPeople(JNIEnv*, jobject, jlong addrGray)
{
    LOGD("Java_com_example_hellocv_MainActivity_FindPeople enter");
    Mat& mGr  = *(Mat*)addrGray;
    resize(mGr,mGr,cvSize(mGr.cols/8,mGr.rows/8));
    vector<Rect> found, found_filtered;
    size_t i, j;

    HOGDescriptor hog;
    hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());
    //Point p1,p2;
    //p1.x=100; p1.y=100;
    //p2.x=200; p2.y=200;
    //vector<double> weight;

    //rectangle(mGr, p1, p2, cv::Scalar(0,255,0), 3);

    double t = (double)getTickCount();
    hog.detectMultiScale(mGr, found, 0, Size(8,8), Size(16,16), 1.05, 2);
    t = (double)getTickCount() - t;
    LOGD("Detection Time: %gms", t*1000./cv::getTickFrequency());
    LOGD("People: %d", found.size());
    //hog.detectMultiScale(mGr,found,weight,-.05, Size(4,4), Size(0,0), 1.05, 0);
    for( i = 0; i < found.size(); i++ )
    {
        Rect r = found[i];
        for( j = 0; j < found.size(); j++ )
            if( j != i && (r & found[j]) == r)
                break;
        if( j == found.size() )
            found_filtered.push_back(r);
    }

    for( i = 0; i < found_filtered.size(); i++ )
    {
        Rect r = found_filtered[i];
        // the HOG detector returns slightly larger rectangles than the real objects.
        // so we slightly shrink the rectangles to get a nicer output.
        r.x += cvRound(r.width*0.1);
        r.width = cvRound(r.width*0.8);
        r.y += cvRound(r.height*0.07);
        r.height = cvRound(r.height*0.8);
        rectangle(mGr, r.tl(), r.br(), cv::Scalar(0,255,0), 3);
    }
}
JNIEXPORT jint JNICALL Java_com_example_hellocv_MainActivity_FindFeature(JNIEnv*, jobject, jlong addrGray,jlong addrDescriptor)
{
    LOGD("Java_com_example_hellocv_MainActivity_FindFeature enter");
    Mat& mGr  = *(Mat*)addrGray;
    Mat& mDescriptor = *(Mat*)addrDescriptor;
    //resize(mGr,mGr,cvSize(mGr.cols/8,mGr.rows/8));

    Mat angleofs,grad;

    HOGDescriptor hog;


    double t = (double)getTickCount();
    hog.computeGradient(mGr,grad,angleofs);
    t = (double)getTickCount() - t;
    double distance=0;
    for(int i=0;i<grad.rows;i++)
    {
       distance += abs(mDescriptor.at<float>(i,0) - grad.at<float>(i,0));
    }
    LOGD("Detection Time: %gms", t*1000./cv::getTickFrequency());
    LOGD("Distance: %f", distance);
    return (int)distance;


}

JNIEXPORT void JNICALL Java_com_example_hellocv_MainActivity_GetDescriptor(JNIEnv*, jobject, jlong addrGray,jlong addrDescriptor) {
    LOGD("Java_com_example_hellocv_MainActivity_GetDescriptor enter");

    Mat& mGr  = *(Mat*)addrGray;
    Mat& grad = *(Mat*)addrDescriptor;

    HOGDescriptor hog;
    Mat angleofs;
    //This function computes the hog features for you
    hog.computeGradient(mGr,grad,angleofs);

    /*
    for(int i=0;i<ders.size();i++)
    {
      Hogfeat.at<float>(i,0)=ders.at(i);

    }
    //Now your HOG features are stored in Hogfeat matrix

    you can also set the window size, cell size and block size by using object hog as follows:

    hog.blockSize=16;
    hog.cellSize=4;
    hog.blockStride=8;

    //This is for comparing the HOG features of two images without using any SVM
    //(It is not an efficient way but useful when you want to compare only few or two images)
    //Simple distance
    //Consider you have two hog feature vectors for two images Hogfeat1 and Hogfeat2 and those are same size.
    double distance=0;
    for(int i=0;i<Hogfeat.rows;i++)
    {
       distance+ = abs(Hogfeat.at<float>(i,0) - Hogfeat.at<float>(i,0));
    }
    if(distance < Threshold)
    cout<<"Two images are of same class"<<endl;
    else
    cout<<"Two images are of different class"<<endl;
    */

}
}
