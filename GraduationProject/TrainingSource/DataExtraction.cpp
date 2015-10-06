#include <windows.h>
#include <tchar.h> 
#include <stdio.h>
#include <strsafe.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2\objdetect\objdetect.hpp>

using namespace std;
using namespace cv;
#pragma comment(lib, "User32.lib")

void humanDetect()
{

	HANDLE hFind;
	WIN32_FIND_DATA data;
	int i = 0;

	char *dirName = "E:\\다운로드\\UCF50\\UCF50\\GolfSwing\\";
	hFind = FindFirstFile(L"E:\\다운로드\\UCF50\\UCF50\\GolfSwing\\*.*", &data);

	Mat frame;
	//string path = "E:\\다운로드\\UCF50\\UCF50\\GolfSwing\\v_GolfSwing_g02_c01.avi";
	//VideoCapture cap(path);
	//cap.set(CV_CAP_PROP_FRAME_WIDTH, 320);
	//cap.set(CV_CAP_PROP_FRAME_HEIGHT, 240);

	

	
	namedWindow("opencv", CV_WINDOW_AUTOSIZE);
	HOGDescriptor hog;
	hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());

	if (hFind != INVALID_HANDLE_VALUE) {
		do {
			int nSize = wcslen(data.cFileName) * 2 + 1;
			char *charStr;
			charStr = new char[nSize];
			wcstombs(charStr, data.cFileName, nSize);
			string name = charStr;
			if (charStr[0] == '.') continue;
			if (name.find("png") != string::npos) continue;
			//printf("%s%s\n", dirName, charStr);
			char *videoPath;
			videoPath = new char[nSize+strlen(dirName)];
			videoPath[0] = '\0';
			strcat(videoPath,dirName);
			strcat(videoPath, charStr);
			printf("%s\n", videoPath);
			
			VideoCapture cap(videoPath);
			//cap.set(CV_CAP_PROP_FRAME_WIDTH, 320);
			//cap.set(CV_CAP_PROP_FRAME_HEIGHT, 240);
			if (!cap.isOpened())
				return;
			int cnt = 0;
			while (true)
			{
				Mat img;
				//cap >> img;
				bool bSuccess = cap.read(img);
				if (!bSuccess) break;
				if (img.empty())
					continue;

				vector<Rect> found, found_filtered;
				hog.detectMultiScale(img, found, 0, Size(8, 8), Size(32, 32), 1.05, 2);
				size_t i, j;
				for (i = 0; i<found.size(); i++)
				{
					Rect r = found[i];
					for (j = 0; j<found.size(); j++)
						if (j != i && (r & found[j]) == r)
							break;
					if (j == found.size())
						found_filtered.push_back(r);
				}
				Mat crop;
				for (i = 0; i<found_filtered.size(); i++)
				{
					Rect r = found_filtered[i];
					r.x += cvRound(r.width*0.1);
					r.width = cvRound(r.width*0.8);
					r.y += cvRound(r.height*0.07);
					r.height = cvRound(r.height*0.8);
					printf("(%d,%d)(%d,%d,%d,%d)\n",img.cols,img.rows, r.x, r.width, r.y, r.height);
					if (r.x < 0) r.x = 0;
					if (r.y < 0) r.y = 0;
					if (r.x + r.width > img.cols) r.x = img.cols - r.width;
					if (r.y + r.height > img.rows) r.y = img.rows - r.height;
					//rectangle(img, r.tl(), r.br(), Scalar(0, 255, 0), 3);
					crop = img(r);
					if (cnt % 4 == 0) {
						vector<int> compression_params;
						compression_params.push_back(CV_IMWRITE_PNG_COMPRESSION);
						compression_params.push_back(9);

						try {
							Size size;
							size.height = 128;
							size.width = 64;
							resize(crop, crop, Size(64, 128));
							string path = videoPath;
							path += to_string(cnt);
							path += to_string(i);
							path += ".png";

							imwrite(path, crop, compression_params);
						}
						catch (runtime_error& ex) {
							fprintf(stderr, "Exception converting image to PNG format: %s\n", ex.what());
						}
					}
				}
				if (found_filtered.size()) {
					imshow("opencv", crop);
					cnt++;
				}
				else
					imshow("opencv", img);
				if (waitKey(30) == 27)
					break;
			}

		} while (FindNextFile(hFind, &data));
		FindClose(hFind);
	}
}

void imageResize() {

	HANDLE hFind;
	WIN32_FIND_DATA data;
	char image[500][260];
	int i = 0;

	char *dirName = "D:\\GP\\Dataset\\positive\\";
	hFind = FindFirstFile(L"D:\\GP\\Dataset\\positive\\*.*", &data);
	if (hFind != INVALID_HANDLE_VALUE) {
		do {
			int nSize = wcslen(data.cFileName) * 2 + 1;
			char *charStr;
			charStr = new char[nSize];
			wcstombs(charStr, data.cFileName, nSize);
			if (charStr[0] == '.') continue;
			printf("%s%s\n", dirName, charStr);
			strcpy(image[i], dirName);
			strcat(image[i], charStr);
			Mat img = imread(image[i], IMREAD_COLOR);
			if (!img.data) {
				printf("No image!\n");
				continue;
			}
			Size size;
			size.height = 128;
			size.width = 64;
			resize(img, img, Size(64, 128));
			char *newloc;
			strcpy(image[i], "D:\\GP\\Dataset\\re\\");
			strcat(image[i], charStr);
			imwrite(image[i], img);
			i++;

		} while (FindNextFile(hFind, &data));
		FindClose(hFind);
	}
}

int main(int argc, char* argv[])
{
	//imageResize();
	humanDetect();
}
/*
int main(int argc, char* argv[]) {
	Mat img = imread("features.dat");
	namedWindow("Display window", WINDOW_AUTOSIZE); // Create a window for display.
	imshow("Display window", img); // Show our image inside it.
	waitKey(0); // Wait for a keystroke in the window
	return 0;
}*/