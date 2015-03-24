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
    hog.detectMultiScale(mGr, found, 0, Size(8,8), Size(4,4), 1.05, 2);
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
}
/*
	 public Bitmap peopleDetect (String path) {
        Bitmap bitmap = null;
	        Bitmap bitmap = null;

	        float execTime;
	            long time = System.currentTimeMillis ();
	            //we Create a matrix of the image for OpenCV and it is placed in it our photo
	            Mat mat = new Mat ();
	            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.dong);
	            Utils.bitmapToMat (bitmap, mat);
	            //Perekonvertiruem a matrix with RGB on graduation of the gray
	            Imgproc.cvtColor (mat, mat, Imgproc. COLOR_RGB2GRAY, 4);
	            HOGDescriptor hog = new HOGDescriptor ();
	            //the standard determinant of people Is gained and installed to its our descriptor
	            MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector ();
	            hog.setSVMDetector (descriptors);
	            //It is defined variables in which search results (locations - the right-angled areas will be placed, weights - weight (it is possible to tell relevance) an appropriate location)
	            MatOfRect locations = new MatOfRect ();
	            MatOfDouble weights = new MatOfDouble ();
	            //As a matter of fact, the analysis of photos. Results register in locations and weights
	            hog.detectMultiScale (mat, locations, weights);
	            execTime = ((float) (System.currentTimeMillis () - time)) / 1000f;
	            //Variables for selection of areas in a photo
	            Point rectPoint1 = new Point ();
	            Point rectPoint2 = new Point ();
	            Scalar fontColor = new Scalar (0, 0, 0);
	            Point fontPoint = new Point ();
	            //If there is a result - is added on a photo of area and weight of each of them
	            if (locations.rows () > 0) {
	                List<Rect> rectangles = locations.toList ();
	                int i = 0;
	                List<Double> weightList = weights.toList ();
	                for (Rect rect: rectangles) {
	                    float weigh = weightList.get (i ++).floatValue ();

	                    rectPoint1.x = rect.x;
	                    rectPoint1.y = rect.y;
	                    fontPoint.x = rect.x;
	                    fontPoint.y = rect.y - 4;
	                    rectPoint2.x = rect.x + rect.width;
	                    rectPoint2.y = rect.y + rect.height;
	                    final Scalar rectColor = new Scalar (0, 0, 0);
	                    //It is added on images the found information
	                    Core.rectangle (mat, rectPoint1, rectPoint2, rectColor, 2);
	                    Core.putText (mat,
	                            String.format ("%1.2f", weigh),
	                            fontPoint, Core. FONT_HERSHEY_PLAIN, 1.5, fontColor,
	                            2, Core. LINE_AA, false);

	                }
	            }
	            fontPoint.x = 15;
	            fontPoint.y = bitmap.getHeight () - 20;
	            //It is added the additional debug information
	            Core.putText (mat,
	                    "Processing time:" + execTime + "width:" + bitmap.getWidth () + "height:" + bitmap.getHeight (),
	                    fontPoint, Core. FONT_HERSHEY_PLAIN, 1.5, fontColor,
	                    2, Core. LINE_AA, false);
	            Utils.matToBitmap (mat, bitmap);
	        Utils.matToBitmap(mGray, bitmap);
	        return bitmap;
	    }

	            */
