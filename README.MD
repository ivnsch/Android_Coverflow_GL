Android Coverflow with OpenGL 1.0 (incomplete)

Coverflow View implemented with OpenGL 1.0 (thus compatible with all devices).


![ScreenShot](https://raw.github.com/i-schuetz/Android_Coverflow_GL/blob/master/pic.png)

[Video](http://www.youtube.com/watch?v=aR2cxE4JXa4&feature=youtu.be)



Stopping with this because I don't have time - hopefully somebody else can continue it.




## Features:

- Scroll and zoom on swipe.
- Slides far away appear darker.


- There's picking functionality (disabled), in order to make the slides selectable. It doesn't work well, I think because of missing Z-Order checks.
Picking basic functionality was taken from http://android-raypick.blogspot.de/2012/04/first-i-want-to-state-this-is-my-first.html
- There's also functionality to render views (any Android layout), as textures on the slides. 
- And finally, there's functionality to download used images from the web. For some reason the slides show black on some devices. I haven't checked why.
But under certain circunstances, this works, believe me!

These can be enabled uncommenting some easy to find lines in the code.



## Dependency:

Because of the image loader, the project requires the universal image loader library, which can be downloaded from:
https://github.com/nostra13/Android-Universal-Image-Loader





## License
-------

The MIT License

Copyright (c) 2013 Ivan Schütz / http://www.ivanschuetz.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.