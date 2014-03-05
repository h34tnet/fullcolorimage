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
* outfile: output filename. if omitted, the name will be something like in

## Considerations

This is just an evening project. Motivation is from this 
[codegolf.stackexchange thread](http://codegolf.stackexchange.com/questions/22144/images-with-all-colors), but i 
thought about it before.

I stumbled upon certain quirks:

The biggest (the one i can't explain) is: why is it faster the more threads i use? 4096 threads for the 
Executors.newFixedThreadPool is almost twice as fast as 2048 threads and so on. As I tested it on a quadcore i expected
performance to get better with each thread until 4 and then get slightly worse again for every additional one. 
But nope; totally unexpected. No idea why. Note: Threads work on lines. 

Other than that:

* Image characteristics don't necessarily get better with a higher sample size; this only means the 
"better matches" will be used first. If the x position would be chosen sequentially and thread count is the same as line
count, that'd mean quality'd degenerate from left to right (but x position isn't chosen sequentially anymore).

* The same seed with different sample sizes or threads will (obviously) generate different outputs.



## Possible improvements

* Don't operate on lines, operate on offsets. 