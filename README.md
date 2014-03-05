# Full Color Image

Takes an image and outputs an image of the same size which resembles the original but has only distinct RGB colors. 
This means the images pixels have no two same colors. This obviously only works for images smaller than 16777216 pixels;
if the image is exactly 16777216 pixels, every color will show up exactly once.

## How to build & run

Build with ant.

    java -jar fci.jar inputimage [rngseed [samplesize [numberOfThreads [outfile]]]]

* inputimage: the source image to use. must not be larger than 16777216 pixels. only tested with quadratic powers of 
two sized images (i.e. 128, 512, 4096). Can be jpg, png or gif (tested only with png).
* rngseed: is the seed for the random number generator (duh)
* samplesize: how many available colors are evaluated for the best match
* numberOfThreads: the size of the thread pool used. the height of the image should be evenly dividable by this number,
and the number must not exceed the height of the image.
* outfile: output filename. if omitted, the name will be something like the input image file plus the parameters 
added.

## Considerations

* This is just an evening project. Motivation is from this 
[codegolf.stackexchange thread](http://codegolf.stackexchange.com/questions/22144/images-with-all-colors), but i 
heard and thought about it before.

* Image characteristics don't necessarily get better with a higher sample size; this only means the 
"better matches" will be used first. If the x position would be chosen sequentially and thread count is the same as line
count, that'd mean quality'd degenerate from left to right (but x position isn't chosen sequentially anymore). The 
characteristics change though.

* The same seed with different sample sizes or threads will (obviously) generate different outputs.
