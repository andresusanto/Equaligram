# Equaligram
![Histogram Equalization](https://upload.wikimedia.org/wikipedia/commons/c/ca/Histogrammeinebnung.png)

Android Histogram Equalizer with Android NDK for faster performance.

## What is Histogram Equalization
Histogram equalization is a image processing process that project images' histogram so they are spreaded evenly. This process may be required in order to make an image processable by computers.

More information about Histogram Equalization: https://en.wikipedia.org/wiki/Histogram_equalization

## What is Equaligram
Equaligram is a Histogram Equalizer Android Application that has four main histogram equalizing algorithm. Equaligram is developed by using Android NDK (https://developer.android.com/tools/sdk/ndk/index.html) to achieve better performance than if it uses dalvik or art.

## Building
We use `Android Studio 1.3` to develop and build this project. You may need `Android Studio 1.3` or later to open and build this project.

You may need `MinGW` or `Cygwin` to debug NDK Applications.

## Screen shoots
Equaligram has four algorithm that you can select:

![Algorithm Selector](/../screenshoot/screenshoots/intro.jpg?raw=true "Algorithm Selector")

Pre-processed image and its histogram:

![Pre-processed image](/../screenshoot/screenshoots/awal.jpg?raw=true "Pre-processed image")

Post-processed image and its histogram, there are also some parameter sliders to find optimum value:

![Post-processed image](/../screenshoot/screenshoots/proses.jpg?raw=true "Post-processed image")

## License
MIT License
